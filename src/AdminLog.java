import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class AdminLog {
    public static Connection con;
    private JFrame frame;

    AdminLog() {
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
        frame = new JFrame("Admin Access");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Full screen
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Use GridBagLayout to center the inner content
        frame.setLayout(new GridBagLayout());
        ThemeUI.styleFrame(frame);

        // 2. Main Admin Panel (The "Form Box")
        JPanel adminPanel = new JPanel();
        adminPanel.setLayout(null); // Absolute positioning inside the box
        adminPanel.setPreferredSize(new Dimension(600, 500));
        ThemeUI.stylePanel(adminPanel);

        // 3. Labels
        JLabel AdminHeader = new JLabel("Admin Login", SwingConstants.CENTER);
        AdminHeader.setBounds(175, 60, 250, 40);
        ThemeUI.styleHeading(AdminHeader);
        adminPanel.add(AdminHeader);

        JLabel AdminIDLabel = new JLabel("Admin ID: ");
        AdminIDLabel.setBounds(100, 180, 150, 30);
        ThemeUI.styleLabel(AdminIDLabel);
        adminPanel.add(AdminIDLabel);

        JLabel AdminPassLabel = new JLabel("Password: ");
        AdminPassLabel.setBounds(100, 230, 150, 30);
        ThemeUI.styleLabel(AdminPassLabel);
        adminPanel.add(AdminPassLabel);

        // 4. Input Fields
        JTextField AdminidField = new JTextField();
        AdminidField.setBounds(260, 180, 250, 35);
        ThemeUI.styleTextField(AdminidField);
        adminPanel.add(AdminidField);

        // Password masking
        JPasswordField AdminpassField = new JPasswordField();
        AdminpassField.setBounds(260, 230, 250, 35);
        ThemeUI.stylePasswordField(AdminpassField);
        adminPanel.add(AdminpassField);

        // 5. Buttons
        JButton login = new JButton("LogIn");
        login.setBounds(150, 330, 120, 40);
        ThemeUI.styleButton(login);
        adminPanel.add(login);

        JButton reset = new JButton("Clear");
        reset.setBounds(300, 330, 120, 40);
        ThemeUI.styleButton(reset);
        adminPanel.add(reset);

        JButton back = new JButton("←");
        back.setBounds(10, 10, 60, 30);
        ThemeUI.styleDangerButton(back);
        adminPanel.add(back);

        // 6. Add the admin box to the center of the frame
        frame.add(adminPanel, new GridBagConstraints());

        // --- Action Listeners ---
        back.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new homePage();
            }
        });

        login.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String aId = AdminidField.getText().trim();
                String aps = new String(AdminpassField.getPassword()).trim();

                if (aId.isEmpty() || aps.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Required data missing", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (check(aId, aps)) {
                    frame.dispose();
                    new AdminDash(aId);
                    JOptionPane.showMessageDialog(null, "Welcome Admin!");
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid Admin Credentials", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
                AdminidField.setText("");
                AdminpassField.setText("");
            }
        });

        reset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AdminidField.setText("");
                AdminpassField.setText("");
            }
        });

        frame.setVisible(true);
    }

    public static boolean check(String aId, String aps) {
        try {
            String query = "SELECT EMAIL, PASSWORD FROM ADMIN_DATA WHERE EMAIL=? AND PASSWORD=?";
            PreparedStatement p = con.prepareStatement(query);
            p.setString(1, aId);
            p.setString(2, aps);
            ResultSet rs = p.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) throws Exception {
        new AdminLog();
    }
}