import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class TheaterTimingPage {
    private JFrame frame;
    private int selectedMovieId;
    private String selectedMovieName;
    private String userEmail;
    private JPanel theaterListPanel;

    public TheaterTimingPage(int movieId, String movieName, String email) {
        this.selectedMovieId = movieId;
        this.selectedMovieName = movieName;
        this.userEmail = email;

        frame = new JFrame("Select Theater - " + movieName);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());
        ThemeUI.styleFrame(frame);

        JPanel mainBox = new JPanel(new BorderLayout());
        mainBox.setPreferredSize(new Dimension(850, 650));
        ThemeUI.stylePanel(mainBox);

        // Header
        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.setBackground(ThemeUI.PANEL_BG);
        JLabel title = new JLabel("Available Shows for " + movieName, SwingConstants.CENTER);
        ThemeUI.styleHeading(title);
        headerPanel.add(title);

        JPanel dateBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        dateBar.setBackground(ThemeUI.PANEL_BG);
        String[] dates = { "Today", "Tomorrow" };
        for (String d : dates) {
            JButton dBtn = new JButton(d);
            ThemeUI.styleButton(dBtn);
            dateBar.add(dBtn);
        }
        headerPanel.add(dateBar);
        mainBox.add(headerPanel, BorderLayout.NORTH);

        // Theater List
        theaterListPanel = new JPanel();
        theaterListPanel.setLayout(new BoxLayout(theaterListPanel, BoxLayout.Y_AXIS));
        theaterListPanel.setBackground(ThemeUI.BACKGROUND);
        JScrollPane scrollPane = new JScrollPane(theaterListPanel);
        scrollPane.setBorder(null);
        mainBox.add(scrollPane, BorderLayout.CENTER);

        // Back Button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(ThemeUI.PANEL_BG);
        JButton back = new JButton("← Back");
        ThemeUI.styleDangerButton(back);
        back.addActionListener(e -> {
            frame.dispose();
            new UserMovies(userEmail);
        });
        bottomPanel.add(back);
        mainBox.add(bottomPanel, BorderLayout.SOUTH);

        loadTheaterDAO();

        frame.add(mainBox, new GridBagConstraints());
        frame.setVisible(true);
    }

    private void loadTheaterDAO() {
        String query = "SELECT t.name, t.location, s.show_time, s.show_id " +
                "FROM shows s JOIN theaters t ON s.theater_id = t.theater_id " +
                "WHERE s.movie_ID = ? AND s.approval_status = 'ACCEPTED' ORDER BY t.name";

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/movies_booking", "root", "root");
                PreparedStatement pst = con.prepareStatement(query)) {

            pst.setInt(1, selectedMovieId);
            ResultSet rs = pst.executeQuery();

            String currentTheater = "";
            JPanel timePanel = null;

            while (rs.next()) {
                String tName = rs.getString("name");
                String tLoc = rs.getString("location");
                String fullTime = rs.getString("show_time");
                String displayTime = fullTime;
                if (fullTime != null && fullTime.contains(" ")) {
                    String[] parts = fullTime.split(" ");
                    if (parts.length > 1) displayTime = parts[1];
                }
                if (displayTime != null && displayTime.length() >= 5) {
                    displayTime = displayTime.substring(0, 5);
                }
                final String finalTime = (displayTime != null) ? displayTime : "N/A";
                int showId = rs.getInt("show_id");

                if (!tName.equals(currentTheater)) {
                    currentTheater = tName;

                    JPanel row = new JPanel(new BorderLayout());
                    row.setMaximumSize(new Dimension(800, 100));
                    row.setBackground(ThemeUI.PANEL_BG);
                    row.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

                    JLabel tInfo = new JLabel("<html><b>" + tName + "</b><br><small>" + tLoc + "</small></html>");
                    ThemeUI.styleLabel(tInfo);
                    row.add(tInfo, BorderLayout.WEST);

                    timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
                    timePanel.setBackground(ThemeUI.PANEL_BG);
                    row.add(timePanel, BorderLayout.CENTER);

                    theaterListPanel.add(row);
                    theaterListPanel.add(new JSeparator());
                }

                JButton timeBtn = new JButton(displayTime);
                ThemeUI.styleButton(timeBtn);
                timeBtn.addActionListener(e -> {
                    frame.dispose();
                    new SeatSelection(showId, selectedMovieName, tName, finalTime, userEmail);
                });

                if (timePanel != null)
                    timePanel.add(timeBtn);
            }
            theaterListPanel.revalidate();
            theaterListPanel.repaint();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}