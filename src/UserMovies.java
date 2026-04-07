import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class UserMovies {
    private JFrame frame;
    private String userEmail;

    // Constructor accepts the email from UserDash
    public UserMovies(String email) {
        this.userEmail = email;

        // 1. Frame Setup
        frame = new JFrame("Movie Catalog");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());
        ThemeUI.styleFrame(frame);

        // 2. Main Centered Box
        JPanel mainBox = new JPanel(null);
        mainBox.setPreferredSize(new Dimension(900, 650));
        ThemeUI.stylePanel(mainBox);

        JLabel header = new JLabel("Now Showing", SwingConstants.CENTER);
        header.setBounds(300, 20, 300, 40);
        ThemeUI.styleHeading(header);
        mainBox.add(header);

        // 3. Grid Panel (Matches your database dump)
        JPanel gridPanel = new JPanel(new GridLayout(0, 3, 20, 25));
        gridPanel.setBackground(ThemeUI.PANEL_BG);

        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setBounds(40, 80, 820, 530);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        mainBox.add(scrollPane);

        // 4. Back Button
        JButton back = new JButton("←");
        back.setBounds(10, 10, 55, 30);
        ThemeUI.styleDangerButton(back);
        mainBox.add(back);
        back.addActionListener(e -> {
            frame.dispose();
            new UserDash(userEmail);
        });

        // 5. DAO Logic: Pulling from your real MySQL database
        fetchMoviesFromDatabase(gridPanel);

        frame.add(mainBox, new GridBagConstraints());
        frame.setVisible(true);
    }

    private void fetchMoviesFromDatabase(JPanel gridPanel) {
        String query = "SELECT DISTINCT m.MOVIE_ID, m.MOVIE_NAME, m.POSTER_PATH, m.MOVIE_GENRE FROM MOVIES m JOIN shows s ON m.MOVIE_ID = s.movie_ID WHERE s.approval_status = 'ACCEPTED'";

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/movies_booking", "root", "root");
                PreparedStatement pst = con.prepareStatement(query);
                ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("MOVIE_ID");
                String name = rs.getString("MOVIE_NAME");
                String path = rs.getString("POSTER_PATH");
                String genre = rs.getString("MOVIE_GENRE");

                // Add to UI
                gridPanel.add(createMovieCard(id, name, genre, path));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Database Error: " + e.getMessage());
        }
    }

    private JPanel createMovieCard(int id, String name, String genre, String path) {
        JPanel card = new JPanel(new BorderLayout());
        ThemeUI.stylePanel(card);

        // Poster Image from your DB Path
        JLabel imgLabel = new JLabel();
        ImageIcon icon = new ImageIcon(path);
        Image img = icon.getImage().getScaledInstance(180, 250, Image.SCALE_SMOOTH);
        imgLabel.setIcon(new ImageIcon(img));
        imgLabel.setHorizontalAlignment(JLabel.CENTER);

        // Footer info
        JPanel footer = new JPanel(new GridLayout(2, 1));
        footer.setBackground(ThemeUI.PANEL_BG);

        JLabel lblName = new JLabel(name, SwingConstants.CENTER);
        ThemeUI.styleSubtitle(lblName);

        JButton btnBook = new JButton("Book Now");
        ThemeUI.styleButton(btnBook);

        // Passing userEmail session to the next page
        btnBook.addActionListener(e -> {
            frame.dispose();
            new TheaterTimingPage(id, name, userEmail);
        });

        footer.add(lblName);
        footer.add(btnBook);

        card.add(imgLabel, BorderLayout.CENTER);
        card.add(footer, BorderLayout.SOUTH);

        return card;
    }
}