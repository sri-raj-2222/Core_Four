import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class homePage {
    public static Connection con;
    private JFrame frame;

    public homePage() {
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
        frame = new JFrame("Movie Booking System - Home");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Makes it full screen
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Use GridBagLayout to center the inner content
        frame.setLayout(new GridBagLayout());
        ThemeUI.styleFrame(frame);

        // 2. Center Panel (The "Selection Box")
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(null); // Keep absolute positioning for components inside the box
        centerPanel.setPreferredSize(new Dimension(600, 500)); // Maintain original dimensions
        ThemeUI.stylePanel(centerPanel);

        // 3. Labels and Buttons (Added to centerPanel)
        JLabel role = new JLabel("🎥 Movie Booking System");
        role.setBounds(100, 50, 400, 40);
        ThemeUI.styleHeading(role);
        centerPanel.add(role);

        JLabel subRole = new JLabel("Select your Role", SwingConstants.CENTER);
        subRole.setBounds(175, 100, 250, 30);
        ThemeUI.styleSubtitle(subRole);
        centerPanel.add(subRole);

        JButton Admin = new JButton("Admin Login");
        Admin.setBounds(175, 160, 250, 40);
        ThemeUI.styleButton(Admin);
        centerPanel.add(Admin);

        JButton TheaterManager = new JButton("Theater Manager");
        TheaterManager.setBounds(175, 230, 250, 40);
        ThemeUI.styleButton(TheaterManager);
        centerPanel.add(TheaterManager);

        JButton User = new JButton("User Login");
        User.setBounds(175, 300, 250, 40);
        ThemeUI.styleButton(User);
        centerPanel.add(User);

        // 4. Action Listeners
        Admin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose(); // Close home page
                new AdminLog();
            }
        });

        User.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose(); // Close home page
                new UserLog();
            }
        });

        TheaterManager.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose(); // Close home page
                new TheaterManagerLog();
            }
        });

        // 5. Add the centerPanel to the Frame
        frame.add(centerPanel, new GridBagConstraints());

        frame.setVisible(true);
    }

    public static void main(String[] args) throws Exception {
        new homePage();
    }
}