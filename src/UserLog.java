import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class UserLog {
    public static Connection con;
    private JFrame frame;

    UserLog() {
        try {
            // DB connection
            String dbURL = "jdbc:mysql://localhost:3306/movies_booking";
            String dbUser = "root";
            String dbPassword = "root";

            con = DriverManager.getConnection(dbURL, dbUser, dbPassword);
            System.out.println("Connected Successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 1. Frame Setup for Full Screen
        frame = new JFrame("User Login");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Full screen
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Use GridBagLayout to center the inner content
        frame.setLayout(new GridBagLayout());
        ThemeUI.styleFrame(frame);

        // 2. Main Login Panel (The "Form Box")
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(null); // Keep absolute positioning inside this box
        loginPanel.setPreferredSize(new Dimension(600, 500));
        ThemeUI.stylePanel(loginPanel);

        // 3. Labels
        JLabel UserLabel = new JLabel("User Login", SwingConstants.CENTER);
        UserLabel.setBounds(175, 50, 250, 30);
        ThemeUI.styleHeading(UserLabel);
        loginPanel.add(UserLabel);

        JLabel UserIDLabel = new JLabel("Email ID: ");
        UserIDLabel.setBounds(100, 150, 150, 30);
        ThemeUI.styleLabel(UserIDLabel);
        loginPanel.add(UserIDLabel);

        JLabel UserPassLabel = new JLabel("Password: ");
        UserPassLabel.setBounds(100, 210, 150, 30);
        ThemeUI.styleLabel(UserPassLabel);
        loginPanel.add(UserPassLabel);

        // 4. Input Fields
        JTextField UseridField = new JTextField();
        UseridField.setBounds(260, 150, 250, 35);
        ThemeUI.styleTextField(UseridField);
        loginPanel.add(UseridField);

        // Changed to JPasswordField for security
        JPasswordField UserpassField = new JPasswordField();
        UserpassField.setBounds(260, 210, 250, 35);
        ThemeUI.stylePasswordField(UserpassField);
        loginPanel.add(UserpassField);

        // 5. Buttons
        JButton signUp = new JButton("SignUp");
        signUp.setBounds(130, 300, 130, 40);
        ThemeUI.styleButton(signUp);
        loginPanel.add(signUp);

        JButton login = new JButton("LogIn");
        login.setBounds(330, 300, 130, 40);
        ThemeUI.styleButton(login);
        loginPanel.add(login);

        JButton back = new JButton("←");
        back.setBounds(10, 10, 60, 30);
        ThemeUI.styleDangerButton(back);
        loginPanel.add(back);

        // 6. Add the login box to the center of the frame
        frame.add(loginPanel, new GridBagConstraints());

        // --- Action Listeners ---
        login.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String uId = UseridField.getText().trim();
                String ups = new String(UserpassField.getPassword()).trim(); // Get password from JPasswordField

                if (uId.isEmpty() || ups.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Required data missing", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (check(uId, ups)) {
                    frame.dispose(); // Close login window
                    new UserDash(uId); // Redirect to your Dashboard
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid Email or Password", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
                UseridField.setText("");
                UserpassField.setText("");
            }
        });

        signUp.addActionListener(e -> {
            frame.dispose();
            new UserReg();
        });

        back.addActionListener(e -> {
            frame.dispose();
            new homePage();
        });

        frame.setVisible(true);
    }

    // --- DAO Logic (Strictly preserved) ---
    public static boolean check(String uId, String ups) {
        try {
            String query = "SELECT EMAIL, PASSWORD FROM USERS WHERE EMAIL=? AND PASSWORD=?";
            PreparedStatement p = con.prepareStatement(query);
            p.setString(1, uId);
            p.setString(2, ups);
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) throws Exception {
        new UserLog();
    }
}