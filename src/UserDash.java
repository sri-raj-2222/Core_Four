import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class UserDash {
    public static Connection con;
    private JFrame frame;
    private String userEmail; // Session variable

    public UserDash(String email) {
        this.userEmail = email;

        try {
            // DB connection
            String dbURL = "jdbc:mysql://localhost:3306/movies_booking";
            String dbUser = "root";
            String dbPassword = "root";
            con = DriverManager.getConnection(dbURL, dbUser, dbPassword);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 1. Frame Setup
        frame = new JFrame("User Dashboard");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());
        ThemeUI.styleFrame(frame);

        // 2. Main Dashboard Panel
        JPanel dashPanel = new JPanel();
        dashPanel.setLayout(null);
        dashPanel.setPreferredSize(new Dimension(600, 500));
        ThemeUI.stylePanel(dashPanel);

        JLabel header = new JLabel("Welcome to User Dashboard", SwingConstants.CENTER);
        header.setBounds(100, 30, 400, 40);
        ThemeUI.styleHeading(header);
        dashPanel.add(header);

        // 3. All 4 Menu Buttons
        JButton Movies = new JButton("Browse Movies");
        Movies.setBounds(175, 120, 250, 40);
        ThemeUI.styleButton(Movies);
        dashPanel.add(Movies);

        JButton Bookings = new JButton("My Bookings");
        Bookings.setBounds(175, 190, 250, 40);
        ThemeUI.styleButton(Bookings);
        dashPanel.add(Bookings);

        JButton Profile = new JButton("My Profile");
        Profile.setBounds(175, 260, 250, 40);
        ThemeUI.styleButton(Profile);
        dashPanel.add(Profile);

        JButton Logout = new JButton("LogOut");
        Logout.setBounds(175, 330, 250, 40);
        ThemeUI.styleDangerButton(Logout);
        dashPanel.add(Logout);

        JButton back = new JButton("←");
        back.setBounds(10, 10, 60, 30);
        ThemeUI.styleDangerButton(back);
        dashPanel.add(back);

        // 4. Action Listeners with Session Routing
        back.addActionListener(e -> {
            frame.dispose();
            // Optional: Redirect back if needed
        });

        Movies.addActionListener(e -> {
            frame.dispose();
            new UserMovies(userEmail);
        });

        Bookings.addActionListener(e -> {
            frame.dispose();
            new UserBookings(userEmail);
        });

        Profile.addActionListener(e -> {
            frame.dispose();
            new UserProfile(userEmail);
        });

        Logout.addActionListener(e -> {
            frame.dispose();
            new homePage();
        });

        frame.add(dashPanel, new GridBagConstraints());
        frame.setVisible(true);
    }
}