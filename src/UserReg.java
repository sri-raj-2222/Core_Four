import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class UserReg {
    public static Connection con;
    private JFrame frame;

    public UserReg() {
        // DB connection logic
        String dbURL = "jdbc:mysql://localhost:3306/movies_booking";
        String dbUser = "root";
        String dbPassword = "root";

        try {
            con = DriverManager.getConnection(dbURL, dbUser, dbPassword);
            System.out.println("Connected Successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 1. Frame Setup for Full Screen
        frame = new JFrame("User Registration");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Full screen
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Use GridBagLayout on the Frame to center the inner content
        frame.setLayout(new GridBagLayout());
        ThemeUI.styleFrame(frame);

        // 2. Main Form Panel (The "Form Box")
        JPanel formPanel = new JPanel();
        formPanel.setLayout(null);
        formPanel.setPreferredSize(new Dimension(600, 500));
        ThemeUI.stylePanel(formPanel);

        // --- All your original components added to formPanel instead of frame ---
        JLabel UserRegistration = new JLabel("User Registration");
        UserRegistration.setBounds(150, 20, 300, 30);
        ThemeUI.styleHeading(UserRegistration);
        formPanel.add(UserRegistration);

        // Labels
        JLabel UserName = new JLabel("Name: ");
        UserName.setBounds(100, 80, 250, 30);
        ThemeUI.styleLabel(UserName);
        formPanel.add(UserName);

        JLabel Email = new JLabel("Email: ");
        Email.setBounds(100, 130, 250, 30);
        ThemeUI.styleLabel(Email);
        formPanel.add(Email);

        JLabel Phone = new JLabel("Phone Number: ");
        Phone.setBounds(100, 180, 250, 30);
        ThemeUI.styleLabel(Phone);
        formPanel.add(Phone);

        JLabel Age = new JLabel("Age: ");
        Age.setBounds(100, 230, 250, 30);
        ThemeUI.styleLabel(Age);
        formPanel.add(Age);

        JLabel Password = new JLabel("Password: ");
        Password.setBounds(100, 280, 250, 30);
        ThemeUI.styleLabel(Password);
        formPanel.add(Password);

        JLabel ConformPass = new JLabel("Confirm Password: ");
        ConformPass.setBounds(100, 330, 250, 30);
        ThemeUI.styleLabel(ConformPass);
        formPanel.add(ConformPass);

        // Inputs
        JTextField Namefield = new JTextField();
        Namefield.setBounds(300, 80, 200, 30);
        ThemeUI.styleTextField(Namefield);
        formPanel.add(Namefield);

        JTextField emailfield = new JTextField();
        emailfield.setBounds(300, 130, 200, 30);
        ThemeUI.styleTextField(emailfield);
        formPanel.add(emailfield);

        JTextField Phonefield = new JTextField();
        Phonefield.setBounds(300, 180, 200, 30);
        ThemeUI.styleTextField(Phonefield);
        formPanel.add(Phonefield);

        JTextField agefield = new JTextField();
        agefield.setBounds(300, 230, 200, 30);
        ThemeUI.styleTextField(agefield);
        formPanel.add(agefield);

        JPasswordField Passwordfield = new JPasswordField();
        Passwordfield.setBounds(300, 280, 200, 30);
        ThemeUI.stylePasswordField(Passwordfield);
        formPanel.add(Passwordfield);

        JPasswordField ConfPassfield = new JPasswordField();
        ConfPassfield.setBounds(300, 330, 200, 30);
        ThemeUI.stylePasswordField(ConfPassfield);
        formPanel.add(ConfPassfield);

        // Buttons
        JButton Clear = new JButton("Clear");
        Clear.setBounds(150, 400, 130, 30);
        ThemeUI.styleButton(Clear);
        formPanel.add(Clear);

        JButton Submit = new JButton("Submit");
        Submit.setBounds(330, 400, 130, 30);
        ThemeUI.styleButton(Submit);
        formPanel.add(Submit);

        JButton back = new JButton("←");
        back.setBounds(10, 10, 60, 30);
        ThemeUI.styleDangerButton(back);
        formPanel.add(back);

        // 3. Add the form box to the center of the full-screen frame
        frame.add(formPanel, new GridBagConstraints());

        // --- Action Listeners (Unchanged logic) ---
        back.addActionListener(e -> {
            frame.dispose();
            new UserLog();
        });

        Clear.addActionListener(e -> {
            Namefield.setText("");
            emailfield.setText("");
            Phonefield.setText("");
            agefield.setText("");
            Passwordfield.setText("");
            ConfPassfield.setText("");
        });

        Submit.addActionListener(e -> {
            String name = Namefield.getText().trim();
            String email = emailfield.getText().trim();
            String phone = Phonefield.getText().trim();
            String age = agefield.getText().trim();
            String Pass = new String(Passwordfield.getPassword()).trim();
            String ConPass = new String(ConfPassfield.getPassword()).trim();

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || age.isEmpty() || Pass.isEmpty()
                    || ConPass.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Required data missing", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!Pass.equals(ConPass)) {
                JOptionPane.showMessageDialog(frame, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!email.endsWith("@gmail.com")) {
                JOptionPane.showMessageDialog(frame, "Email is not Valid", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                int k = Integer.parseInt(age);
                if (k < 18 || k > 60) {
                    JOptionPane.showMessageDialog(frame, "Invalid Age (18-60 only)", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Age must be a number", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (phone.length() != 10) {
                JOptionPane.showMessageDialog(frame, "Invalid Phone Number", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (check_mail(email)) {
                JOptionPane.showMessageDialog(frame, "User already Exists with this Email", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (check_phone(phone)) {
                JOptionPane.showMessageDialog(frame, "User already Exists with the Phone Number", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            insert(name, email, phone, age, Pass);
            JOptionPane.showMessageDialog(frame, "User Registered Successfully", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            frame.dispose();
            new UserLog();
        });

        frame.setVisible(true);
    }

    // --- DAO Logic ---
    public static boolean check_mail(String email) {
        try {
            String query = "SELECT EMAIL FROM USERS WHERE EMAIL=?";
            PreparedStatement p = con.prepareStatement(query);
            p.setString(1, email);
            ResultSet rs = p.executeQuery();
            if (rs.next())
                return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean check_phone(String phone) {
        try {
            String query = "SELECT PHONE_NUMBER FROM USERS WHERE PHONE_NUMBER=?";
            PreparedStatement p = con.prepareStatement(query);
            p.setString(1, phone);
            ResultSet rs = p.executeQuery();
            if (rs.next())
                return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void insert(String name, String email, String phone, String age, String pass) {
        try {
            String query = "INSERT INTO USERS VALUES (?,?,?,?,?)";
            PreparedStatement p = con.prepareStatement(query);
            int agee = Integer.parseInt(age);
            long ph = Long.parseLong(phone);
            p.setInt(1, agee);
            p.setString(2, name);
            p.setLong(3, ph);
            p.setString(4, email);
            p.setString(5, pass);
            p.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new UserReg();
    }
}