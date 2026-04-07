import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class AdminAddMovie {
    public static Connection con;
    private JFrame frame;

    public AdminAddMovie() {
        try {
            String dbURL = "jdbc:mysql://localhost:3306/movies_booking";
            String dbUser = "root";
            String dbPassword = "root";
            con = DriverManager.getConnection(dbURL, dbUser, dbPassword);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 1. Frame Setup
        frame = new JFrame("Admin - Manual Movie Entry");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());
        ThemeUI.styleFrame(frame);

        // 2. Centered Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(null);
        formPanel.setPreferredSize(new Dimension(600, 550));
        ThemeUI.stylePanel(formPanel);

        JLabel header = new JLabel("Add Movie to Database", SwingConstants.CENTER);
        header.setBounds(150, 30, 300, 30);
        ThemeUI.styleHeading(header);
        formPanel.add(header);

        // 3. Input Fields
        // Name
        JLabel lblName = new JLabel("Movie Name:");
        lblName.setBounds(70, 100, 150, 30);
        ThemeUI.styleSubtitle(lblName);
        formPanel.add(lblName);

        JTextField txtName = new JTextField();
        txtName.setBounds(230, 100, 280, 30);
        ThemeUI.styleTextField(txtName);
        formPanel.add(txtName);

        // Genre
        JLabel lblGenre = new JLabel("Movie Genre:");
        lblGenre.setBounds(70, 150, 150, 30);
        ThemeUI.styleSubtitle(lblGenre);
        formPanel.add(lblGenre);

        JTextField txtGenre = new JTextField();
        txtGenre.setBounds(230, 150, 280, 30);
        ThemeUI.styleTextField(txtGenre);
        formPanel.add(txtGenre);

        // Duration
        JLabel lblDuration = new JLabel("Duration (min):");
        lblDuration.setBounds(70, 200, 150, 30);
        ThemeUI.styleSubtitle(lblDuration);
        formPanel.add(lblDuration);

        JTextField txtDuration = new JTextField();
        txtDuration.setBounds(230, 200, 280, 30);
        ThemeUI.styleTextField(txtDuration);
        formPanel.add(txtDuration);

        // Language
        JLabel lblLang = new JLabel("Language:");
        lblLang.setBounds(70, 250, 150, 30);
        ThemeUI.styleSubtitle(lblLang);
        formPanel.add(lblLang);

        JTextField txtLang = new JTextField();
        txtLang.setBounds(230, 250, 280, 30);
        ThemeUI.styleTextField(txtLang);
        formPanel.add(txtLang);

        // Path (Manual Entry)
        JLabel lblPath = new JLabel("Poster Path:");
        lblPath.setBounds(70, 300, 150, 30);
        ThemeUI.styleSubtitle(lblPath);
        formPanel.add(lblPath);

        JTextField txtPath = new JTextField();
        txtPath.setBounds(230, 300, 280, 30);
        ThemeUI.styleTextField(txtPath);
        formPanel.add(txtPath);

        JLabel pathHint = new JLabel("Use / instead of \\ (e.g. C:/images/p.jpg)");
        pathHint.setBounds(230, 330, 300, 20);
        pathHint.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        pathHint.setForeground(ThemeUI.DANGER_BTN);
        formPanel.add(pathHint);

        // 4. Buttons
        JButton btnSave = new JButton("Add Movie");
        btnSave.setBounds(230, 400, 150, 40);
        ThemeUI.styleButton(btnSave);
        btnSave.setBackground(ThemeUI.SUCCESS_BTN);
        formPanel.add(btnSave);

        JButton btnBack = new JButton("←");
        btnBack.setBounds(10, 10, 60, 30);
        ThemeUI.styleDangerButton(btnBack);
        formPanel.add(btnBack);

        // --- Logic ---

        btnSave.addActionListener(e -> {
            String name = txtName.getText();
            String genre = txtGenre.getText();
            String dur = txtDuration.getText();
            String lang = txtLang.getText();
            // Automatically clean the path in case Admin uses backslashes
            String path = txtPath.getText().replace("\\", "/");

            if (name.isEmpty() || path.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please provide at least Name and Path!");
                return;
            }

            try {
                String query = "INSERT INTO MOVIES (MOVIE_NAME, MOVIE_GENRE, MOVIE_DURATION, MOVIE_LANGUAGE, POSTER_PATH) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement pst = con.prepareStatement(query);
                pst.setString(1, name);
                pst.setString(2, genre);
                pst.setString(3, dur);
                pst.setString(4, lang);
                pst.setString(5, path);

                pst.executeUpdate();
                JOptionPane.showMessageDialog(frame, "Database Updated! Users can now see " + name);

                // Return to Manage Movies page
                frame.dispose();
                new AdminManageMovies();

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "SQL Error: " + ex.getMessage());
            }
        });

        btnBack.addActionListener(e -> {
            frame.dispose();
            new AdminManageMovies();
        });

        frame.add(formPanel, new GridBagConstraints());
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new AdminAddMovie();
    }
}