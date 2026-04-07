import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class TheaterManagerDash {
    public static Connection con;
    private JFrame frame;
    private String theaterId;

    public TheaterManagerDash(String tId) {
        this.theaterId = tId;
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
        frame = new JFrame("Theater Manager Dashboard");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Full screen
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Use GridBagLayout to center the inner content
        frame.setLayout(new GridBagLayout());
        ThemeUI.styleFrame(frame);

        // 2. Main Dashboard Panel (The "Menu Box")
        JPanel dashPanel = new JPanel();
        dashPanel.setLayout(null);
        dashPanel.setPreferredSize(new Dimension(650, 580)); // Slightly wider to fit text
        ThemeUI.stylePanel(dashPanel);

        // Header Label
        JLabel header = new JLabel("Manager Dashboard - " + theaterId, SwingConstants.CENTER);
        header.setBounds(100, 30, 450, 40);
        ThemeUI.styleHeading(header);
        dashPanel.add(header);

        // 3. Menu Buttons
        JButton AddEditScreens = new JButton("Add/Edit Screens");
        AddEditScreens.setBounds(175, 120, 300, 40);
        ThemeUI.styleButton(AddEditScreens);
        dashPanel.add(AddEditScreens);

        JButton AddEditSeats = new JButton("Add/Edit Seats");
        AddEditSeats.setBounds(175, 180, 300, 40);
        ThemeUI.styleButton(AddEditSeats);
        dashPanel.add(AddEditSeats);

        JButton CreateManageShows = new JButton("Create and Manage Shows");
        CreateManageShows.setBounds(175, 240, 300, 40);
        ThemeUI.styleButton(CreateManageShows);
        dashPanel.add(CreateManageShows);

        JButton Profile = new JButton("Profile");
        Profile.setBounds(175, 300, 300, 40);
        ThemeUI.styleButton(Profile);
        dashPanel.add(Profile);

        JButton TheaterBooking = new JButton("Theater Booking");
        TheaterBooking.setBounds(175, 360, 300, 40);
        ThemeUI.styleButton(TheaterBooking);
        dashPanel.add(TheaterBooking);

        JButton Logout = new JButton("LogOut");
        Logout.setBounds(175, 420, 300, 40);
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
            new TheaterManagerLog();
        });

        AddEditScreens.addActionListener(e -> {
            frame.dispose();
            new TheaterManagerScreens(theaterId);
        });

        AddEditSeats.addActionListener(e -> {
            frame.dispose();
            new TheaterManagerSeats(theaterId);
        });

        CreateManageShows.addActionListener(e -> {
            frame.dispose();
            new TheaterManagerShows(theaterId);
        });

        Profile.addActionListener(e -> {
            frame.dispose();
            new TheaterManagerProfile(theaterId);
        });

        TheaterBooking.addActionListener(e -> {
            frame.dispose();
            new TheaterManagerBookings(theaterId);
        });

        Logout.addActionListener(e -> {
            frame.dispose();
            new homePage();
        });

        frame.setVisible(true);
    }

    public static void main(String[] args) throws Exception {
        new TheaterManagerDash("TM001");
    }
}
