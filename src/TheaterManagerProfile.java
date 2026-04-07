import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class TheaterManagerProfile {
    public static Connection con;
    private JFrame frame;
    private String theaterId;

    private JLabel lblTheaterNameVal;
    private JLabel lblNumScreensVal;

    public TheaterManagerProfile(String tId) {
        this.theaterId = tId;
        try {
            String dbURL = "jdbc:mysql://localhost:3306/movies_booking";
            String dbUser = "root";
            String dbPassword = "root";
            con = DriverManager.getConnection(dbURL, dbUser, dbPassword);
        } catch (Exception e) {
            e.printStackTrace();
        }

        frame = new JFrame("Manager Profile - " + theaterId);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());
        ThemeUI.styleFrame(frame);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setPreferredSize(new Dimension(600, 500));
        ThemeUI.stylePanel(mainPanel);

        // Header
        JLabel header = new JLabel("Theater Profile", SwingConstants.CENTER);
        header.setBounds(100, 30, 400, 40);
        ThemeUI.styleHeading(header);
        mainPanel.add(header);

        // 1. Theater ID
        JLabel lblId = new JLabel("Theater ID:");
        lblId.setBounds(80, 120, 150, 30);
        ThemeUI.styleSubtitle(lblId);
        mainPanel.add(lblId);

        JLabel lblIdVal = new JLabel(theaterId);
        lblIdVal.setBounds(250, 120, 250, 30);
        ThemeUI.styleLabel(lblIdVal);
        mainPanel.add(lblIdVal);

        // 2. Theater Name
        JLabel lblName = new JLabel("Theater Name:");
        lblName.setBounds(80, 180, 150, 30);
        ThemeUI.styleSubtitle(lblName);
        mainPanel.add(lblName);

        lblTheaterNameVal = new JLabel("Loading...");
        lblTheaterNameVal.setBounds(250, 180, 250, 30);
        ThemeUI.styleLabel(lblTheaterNameVal);
        mainPanel.add(lblTheaterNameVal);

        // 3. Number of Screens
        JLabel lblScreens = new JLabel("No. of Screens:");
        lblScreens.setBounds(80, 240, 150, 30);
        ThemeUI.styleSubtitle(lblScreens);
        mainPanel.add(lblScreens);

        lblNumScreensVal = new JLabel("Loading...");
        lblNumScreensVal.setBounds(250, 240, 250, 30);
        ThemeUI.styleLabel(lblNumScreensVal);
        mainPanel.add(lblNumScreensVal);

        // Change Password Button
        JButton btnChangePassword = new JButton("Change Password");
        btnChangePassword.setBounds(200, 300, 200, 40);
        ThemeUI.styleButton(btnChangePassword);
        mainPanel.add(btnChangePassword);

        // Back Button
        JButton back = new JButton("←");
        back.setBounds(10, 10, 60, 30);
        ThemeUI.styleDangerButton(back);
        mainPanel.add(back);

        frame.add(mainPanel, new GridBagConstraints());

        loadProfileData();

        // Actions
        back.addActionListener(e -> {
            frame.dispose();
            new TheaterManagerDash(theaterId);
        });

        btnChangePassword.addActionListener(e -> changePassword());

        frame.setVisible(true);
    }

    private void loadProfileData() {
        try {
            if (con == null)
                return;

            String query = "SELECT tm.name, (SELECT COUNT(*) FROM screens s WHERE s.theater_id = tm.theater_id) AS actual_screens FROM theater_managers tm WHERE tm.theater_id = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, theaterId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                lblTheaterNameVal.setText(rs.getString("name"));
                lblNumScreensVal.setText(String.valueOf(rs.getInt("actual_screens")));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Error loading profile: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void changePassword() {
        JPasswordField newPasswordField = new JPasswordField(15);
        ThemeUI.stylePasswordField(newPasswordField);
        JPasswordField confirmPasswordField = new JPasswordField(15);
        ThemeUI.stylePasswordField(confirmPasswordField);

        JPanel panel = new JPanel(new GridLayout(4, 1, 5, 5));
        JLabel l1 = new JLabel("Enter new password:");
        ThemeUI.styleLabel(l1);
        panel.add(l1);
        panel.add(newPasswordField);

        JLabel l2 = new JLabel("Confirm new password:");
        ThemeUI.styleLabel(l2);
        panel.add(l2);
        panel.add(confirmPasswordField);

        int option = JOptionPane.showConfirmDialog(frame, panel, "Change Password", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String newPass = new String(newPasswordField.getPassword()).trim();
            String confirmPass = new String(confirmPasswordField.getPassword()).trim();

            if (newPass.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Password cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!newPass.equals(confirmPass)) {
                JOptionPane.showMessageDialog(frame, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                String query = "UPDATE theater_managers SET password = ? WHERE theater_id = ?";
                PreparedStatement ps = con.prepareStatement(query);
                ps.setString(1, newPass);
                ps.setString(2, theaterId);

                int rows = ps.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(frame, "Password updated successfully.");
                } else {
                    JOptionPane.showMessageDialog(frame, "Error updating password.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Database error: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        new TheaterManagerProfile("TM001");
    }
}
