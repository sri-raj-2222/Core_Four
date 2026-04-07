import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class AdminProfile {
    private JFrame frame;
    private String adminEmail;
    private JLabel nameVal, emailVal, phoneVal, ageVal;

    public AdminProfile(String email) {
        this.adminEmail = email;

        frame = new JFrame("Admin Profile");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());
        ThemeUI.styleFrame(frame);

        // Profile Card
        JPanel profilePanel = new JPanel(null);
        profilePanel.setPreferredSize(new Dimension(600, 500));
        ThemeUI.stylePanel(profilePanel);

        // Header
        JLabel header = new JLabel("Admin Profile", SwingConstants.CENTER);
        header.setBounds(200, 30, 200, 40);
        ThemeUI.styleHeading(header);
        profilePanel.add(header);

        // --- SIDE HEADINGS (Labels) ---
        JLabel lblName = new JLabel("Name:");
        lblName.setBounds(100, 120, 100, 30);
        ThemeUI.styleSubtitle(lblName);
        profilePanel.add(lblName);

        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setBounds(100, 170, 100, 30);
        ThemeUI.styleSubtitle(lblEmail);
        profilePanel.add(lblEmail);

        JLabel lblPhone = new JLabel("Phone:");
        lblPhone.setBounds(100, 220, 100, 30);
        ThemeUI.styleSubtitle(lblPhone);
        profilePanel.add(lblPhone);

        JLabel lblAge = new JLabel("Age:");
        lblAge.setBounds(100, 270, 100, 30);
        ThemeUI.styleSubtitle(lblAge);
        profilePanel.add(lblAge);

        // --- VALUE LABELS (Placeholders) ---
        nameVal = new JLabel("...");
        nameVal.setBounds(250, 120, 300, 30);
        ThemeUI.styleLabel(nameVal);
        profilePanel.add(nameVal);

        emailVal = new JLabel(adminEmail);
        emailVal.setBounds(250, 170, 300, 30);
        ThemeUI.styleLabel(emailVal);
        profilePanel.add(emailVal);

        phoneVal = new JLabel("...");
        phoneVal.setBounds(250, 220, 300, 30);
        ThemeUI.styleLabel(phoneVal);
        profilePanel.add(phoneVal);

        ageVal = new JLabel("...");
        ageVal.setBounds(250, 270, 300, 30);
        ThemeUI.styleLabel(ageVal);
        profilePanel.add(ageVal);

        // Back button
        JButton back = new JButton("←");
        back.setBounds(10, 10, 60, 30);
        ThemeUI.styleDangerButton(back);
        profilePanel.add(back);
        back.addActionListener(e -> {
            frame.dispose();
            new AdminDash(adminEmail);
        });

        // DAO EXECUTION
        getData();

        frame.add(profilePanel, new GridBagConstraints());
        frame.setVisible(true);
    }

    public void getData() {
        String query = "SELECT NAME, EMAIL, PHONE_NUMBER, AGE FROM admin_data WHERE EMAIL = ?";

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/movies_booking", "root", "root");
                PreparedStatement pst = con.prepareStatement(query)) {

            pst.setString(1, adminEmail);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                nameVal.setText(rs.getString("NAME"));
                emailVal.setText(rs.getString("EMAIL"));
                phoneVal.setText(rs.getString("PHONE_NUMBER"));
                ageVal.setText(String.valueOf(rs.getInt("AGE")));
            } else {
                JOptionPane.showMessageDialog(frame, "Admin Not Found in Database");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Database Error: " + e.getMessage());
        }
    }
}
