import java.awt.*;
import java.sql.*;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class TheaterManagerBookings {
    private JFrame frame;
    private String theaterId;
    private DefaultTableModel model;

    private JComboBox<String> movieFilterCombo;
    private JComboBox<String> showTimeFilterCombo;

    public TheaterManagerBookings(String theaterId) {
        this.theaterId = theaterId;

        // 1. Frame Setup
        frame = new JFrame("Manager - Bookings (" + theaterId + ")");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());
        ThemeUI.styleFrame(frame);

        // 2. Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setPreferredSize(new Dimension(1000, 650));
        ThemeUI.stylePanel(mainPanel);

        // Top Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(ThemeUI.PANEL_BG);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton back = new JButton("←");
        ThemeUI.styleDangerButton(back);
        back.setPreferredSize(new Dimension(60, 30));
        topPanel.add(back, BorderLayout.WEST);

        JLabel header = new JLabel("All Bookings for Your Theater", SwingConstants.CENTER);
        ThemeUI.styleHeading(header);
        topPanel.add(header, BorderLayout.CENTER);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Filter Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        filterPanel.setBackground(ThemeUI.PANEL_BG);
        filterPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        JLabel mLabel = new JLabel("Filter by Movie:");
        ThemeUI.styleSubtitle(mLabel);
        filterPanel.add(mLabel);
        movieFilterCombo = new JComboBox<>();
        movieFilterCombo.setPreferredSize(new Dimension(150, 30));
        filterPanel.add(movieFilterCombo);

        JLabel tLabel = new JLabel("Filter by Show Time:");
        ThemeUI.styleSubtitle(tLabel);
        filterPanel.add(tLabel);
        showTimeFilterCombo = new JComboBox<>();
        showTimeFilterCombo.setPreferredSize(new Dimension(150, 30));
        filterPanel.add(showTimeFilterCombo);

        JButton btnApply = new JButton("Apply Filters");
        ThemeUI.styleButton(btnApply);
        JButton btnClear = new JButton("Clear Filters");
        ThemeUI.styleDangerButton(btnClear);
        filterPanel.add(btnApply);
        filterPanel.add(btnClear);

        // Add Filter Panel to the top of the center area
        JPanel centerContainer = new JPanel(new BorderLayout());
        centerContainer.setBackground(ThemeUI.PANEL_BG);
        centerContainer.add(filterPanel, BorderLayout.NORTH);

        // 3. Table Setup
        String[] columns = { "Booking ID", "User Email", "Movie", "Show Date", "Time", "Seats", "Total (Rs)",
                "Status" };
        model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(table);
        ThemeUI.styleTable(table, scrollPane);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        centerContainer.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(centerContainer, BorderLayout.CENTER);

        // Fetch Initial Data
        populateFilterCombos();
        loadBookings("All", "All");

        // --- Action Listeners ---
        back.addActionListener(e -> {
            frame.dispose();
            new TheaterManagerDash(theaterId);
        });

        btnApply.addActionListener(e -> {
            String selectedMovie = (String) movieFilterCombo.getSelectedItem();
            String selectedTime = (String) showTimeFilterCombo.getSelectedItem();
            loadBookings(selectedMovie, selectedTime);
        });

        btnClear.addActionListener(e -> {
            movieFilterCombo.setSelectedIndex(0);
            showTimeFilterCombo.setSelectedIndex(0);
            loadBookings("All", "All");
        });

        frame.add(mainPanel, new GridBagConstraints());
        frame.setVisible(true);
    }

    private void populateFilterCombos() {
        movieFilterCombo.addItem("All");
        showTimeFilterCombo.addItem("All");

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/movies_booking", "root",
                "root")) {
            String movieQ = "SELECT DISTINCT m.MOVIE_NAME FROM shows s JOIN movies m ON s.movie_id = m.MOVIE_ID JOIN theater_managers tm ON s.theater_id = tm.id WHERE tm.theater_id = ?";
            PreparedStatement pstM = con.prepareStatement(movieQ);
            pstM.setString(1, theaterId);
            ResultSet rsM = pstM.executeQuery();
            while (rsM.next()) {
                movieFilterCombo.addItem(rsM.getString("MOVIE_NAME"));
            }

            String timeQ = "SELECT DISTINCT s.show_time FROM shows s JOIN theater_managers tm ON s.theater_id = tm.id WHERE tm.theater_id = ?";
            PreparedStatement pstT = con.prepareStatement(timeQ);
            pstT.setString(1, theaterId);
            ResultSet rsT = pstT.executeQuery();
            while (rsT.next()) {
                String fullTime = rsT.getString("show_time");
                if (fullTime != null && fullTime.length() >= 16) {
                    showTimeFilterCombo.addItem(fullTime.substring(11, 16));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void loadBookings(String movieFilter, String timeFilter) {
        model.setRowCount(0);

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/movies_booking", "root",
                "root")) {
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append(
                    "SELECT b.id, b.user_email AS email, m.MOVIE_NAME, s.show_time, b.seat_numbers, b.total_amount, b.status ");
            queryBuilder.append("FROM bookings b ");
            queryBuilder.append("JOIN shows s ON b.show_id = s.show_id ");
            queryBuilder.append("JOIN movies m ON s.movie_id = m.MOVIE_ID ");
            queryBuilder.append("JOIN theater_managers tm ON s.theater_id = tm.id ");
            queryBuilder.append("WHERE tm.theater_id = ? ");

            Vector<String> parameters = new Vector<>();
            parameters.add(theaterId);

            if (movieFilter != null && !movieFilter.equals("All")) {
                queryBuilder.append("AND m.MOVIE_NAME = ? ");
                parameters.add(movieFilter);
            }
            if (timeFilter != null && !timeFilter.equals("All")) {
                queryBuilder.append("AND s.show_time LIKE ? ");
                parameters.add("%" + timeFilter + "%");
            }

            queryBuilder.append("ORDER BY s.show_time DESC");

            PreparedStatement pst = con.prepareStatement(queryBuilder.toString());
            for (int i = 0; i < parameters.size(); i++) {
                pst.setString(i + 1, parameters.get(i));
            }

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String fullTime = rs.getString("show_time");
                String displayDate = (fullTime != null && fullTime.length() >= 10) ? fullTime.substring(0, 10) : "N/A";
                String displayTime = (fullTime != null && fullTime.length() >= 16) ? fullTime.substring(11, 16)
                        : fullTime;
                String status = rs.getString("status");
                if (status == null || status.isEmpty())
                    status = "UNKNOWN";

                model.addRow(new Object[] {
                        rs.getString("id"),
                        rs.getString("email"),
                        rs.getString("MOVIE_NAME"),
                        displayDate,
                        displayTime,
                        rs.getString("seat_numbers"),
                        rs.getString("total_amount"),
                        status
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Database error: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
