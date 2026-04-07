import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class AdminDash {
    public static Connection con;
    private JFrame frame;
    private String adminEmail;

    public AdminDash(String email) {
        this.adminEmail = email;
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
        frame = new JFrame("Admin Dashboard");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Full screen
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Use GridBagLayout to center the inner content
        frame.setLayout(new GridBagLayout());
        ThemeUI.styleFrame(frame);

        // 2. Main Dashboard Panel (The "Menu Box")
        JPanel dashPanel = new JPanel();
        dashPanel.setLayout(null);
        dashPanel.setPreferredSize(new Dimension(600, 580));
        ThemeUI.stylePanel(dashPanel);

        // Header Label
        JLabel header = new JLabel("Welcome to Admin Dashboard", SwingConstants.CENTER);
        header.setBounds(100, 30, 400, 40);
        ThemeUI.styleHeading(header);
        dashPanel.add(header);

        // 3. Menu Buttons
        JButton Movies = new JButton("Movies");
        Movies.setBounds(175, 120, 250, 40);
        ThemeUI.styleButton(Movies);
        dashPanel.add(Movies);

        JButton Theaters = new JButton("Theaters");
        Theaters.setBounds(175, 180, 250, 40);
        ThemeUI.styleButton(Theaters);
        dashPanel.add(Theaters);

        JButton ManageTheaters = new JButton("Manage Theaters");
        ManageTheaters.setBounds(175, 240, 250, 40);
        ThemeUI.styleButton(ManageTheaters);
        dashPanel.add(ManageTheaters);

        JButton Users = new JButton("Users");
        Users.setBounds(175, 300, 250, 40);
        ThemeUI.styleButton(Users);
        dashPanel.add(Users);

        JButton Logout = new JButton("LogOut");
        Logout.setBounds(175, 360, 250, 40);
        ThemeUI.styleDangerButton(Logout);
        dashPanel.add(Logout);

        // Back button (Top Left)
        JButton back = new JButton("←");
        back.setBounds(10, 10, 60, 30);
        ThemeUI.styleDangerButton(back);
        dashPanel.add(back);

        // 4. Add the dashPanel to the center of the frame
        frame.add(dashPanel, new GridBagConstraints());

        // --- Action Listeners ---
        back.addActionListener(e -> {
            frame.dispose();
            new AdminLog();
        });

        Movies.addActionListener(e -> {
            frame.dispose();
            new AdminManageMovies();
        });

        Theaters.addActionListener(e -> {
            frame.dispose();
            new AdminTheaters();
        });

        ManageTheaters.addActionListener(e -> {
            frame.dispose();
            new AdminManageTheaters();
        });

        Users.addActionListener(e -> {
            frame.dispose();
            new AdminManageUsers();
        });

        Logout.addActionListener(e -> {
            frame.dispose();
            new homePage();
        });

        frame.setVisible(true);
    }

    public static void main(String[] args) throws Exception {
        new AdminDash("admin@example.com");
    }
}
