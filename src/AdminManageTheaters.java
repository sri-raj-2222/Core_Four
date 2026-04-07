import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class AdminManageTheaters {
    public static Connection con;
    private JFrame frame;
    private JTable tablePending;
    private JTable tableHandled;
    private DefaultTableModel modelPending;
    private DefaultTableModel modelHandled;
    private JTextField showIdField;

    public AdminManageTheaters() {
        try {
            String dbURL = "jdbc:mysql://localhost:3306/movies_booking";
            String dbUser = "root";
            String dbPassword = "root";
            con = DriverManager.getConnection(dbURL, dbUser, dbPassword);
        } catch (Exception e) {
            e.printStackTrace();
        }

        frame = new JFrame("Admin - Manage Theaters & Approvals");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Full screen
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());
        ThemeUI.styleFrame(frame);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setPreferredSize(new Dimension(1000, 600));
        ThemeUI.stylePanel(mainPanel);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(ThemeUI.PANEL_BG);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton back = new JButton("← Back");
        ThemeUI.styleDangerButton(back);
        back.setPreferredSize(new Dimension(100, 30));
        topPanel.add(back, BorderLayout.WEST);

        JLabel header = new JLabel("Show Approvals", SwingConstants.CENTER);
        ThemeUI.styleHeading(header);
        topPanel.add(header, BorderLayout.CENTER);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tabbedPane.setBackground(ThemeUI.PANEL_BG);
        tabbedPane.setForeground(ThemeUI.PRIMARY_TEXT);

        // TAB 1: Pending Approvals
        JPanel pendingPanel = new JPanel(new BorderLayout());
        pendingPanel.setBackground(ThemeUI.BACKGROUND);
        String[] columns = { "Show ID", "Theater", "Movie Name", "Genre", "Duration (m)", "Language", "Timing",
                "Status" };
        modelPending = new DefaultTableModel(columns, 0);
        tablePending = new JTable(modelPending);

        JScrollPane scrollPending = new JScrollPane(tablePending);
        ThemeUI.styleTable(tablePending, scrollPending);
        scrollPending.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        pendingPanel.add(scrollPending, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomPanel.setBackground(ThemeUI.PANEL_BG);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        JLabel lblShowId = new JLabel("Enter Show ID:");
        ThemeUI.styleSubtitle(lblShowId);
        bottomPanel.add(lblShowId);

        showIdField = new JTextField(10);
        ThemeUI.styleTextField(showIdField);
        bottomPanel.add(showIdField);

        JButton btnAccept = new JButton("Accept");
        ThemeUI.styleButton(btnAccept);
        btnAccept.setBackground(ThemeUI.SUCCESS_BTN);
        bottomPanel.add(btnAccept);

        JButton btnReject = new JButton("Reject");
        ThemeUI.styleDangerButton(btnReject);
        bottomPanel.add(btnReject);

        pendingPanel.add(bottomPanel, BorderLayout.SOUTH);

        // TAB 2: Handled Shows
        JPanel handledPanel = new JPanel(new BorderLayout());
        handledPanel.setBackground(ThemeUI.BACKGROUND);
        modelHandled = new DefaultTableModel(columns, 0);
        tableHandled = new JTable(modelHandled);

        JScrollPane scrollHandled = new JScrollPane(tableHandled);
        ThemeUI.styleTable(tableHandled, scrollHandled);
        scrollHandled.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        handledPanel.add(scrollHandled, BorderLayout.CENTER);

        tabbedPane.addTab("Pending Approvals", pendingPanel);
        tabbedPane.addTab("Accepted / Rejected", handledPanel);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        loadShows();

        frame.add(mainPanel, new GridBagConstraints());

        // --- Action Listeners ---
        back.addActionListener(e -> {
            frame.dispose();
            new AdminDash("admin");
        });

        btnAccept.addActionListener(e -> processApproval("ACCEPTED"));
        btnReject.addActionListener(e -> processApproval("REJECTED"));

        frame.setVisible(true);
    }

    private void loadShows() {
        try {
            if (con == null)
                return;
            modelPending.setRowCount(0);
            modelHandled.setRowCount(0);

            String query = "SELECT s.show_id, t.name AS theater_name, m.MOVIE_NAME, m.MOVIE_GENRE, " +
                    "m.MOVIE_DURATION, m.MOVIE_LANGUAGE, s.show_time, s.approval_status " +
                    "FROM shows s " +
                    "LEFT JOIN theaters t ON s.theater_id = t.theater_id " +
                    "LEFT JOIN movies m ON s.movie_ID = m.MOVIE_ID";

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(query);

            while (rs.next()) {
                String id = String.valueOf(rs.getInt("show_id"));
                String tName = rs.getString("theater_name") != null ? rs.getString("theater_name") : "Unknown";
                String mName = rs.getString("MOVIE_NAME") != null ? rs.getString("MOVIE_NAME") : "Unknown";
                String genre = rs.getString("MOVIE_GENRE") != null ? rs.getString("MOVIE_GENRE") : "N/A";
                String duration = rs.getString("MOVIE_DURATION") != null ? rs.getString("MOVIE_DURATION") : "0";
                String lang = rs.getString("MOVIE_LANGUAGE") != null ? rs.getString("MOVIE_LANGUAGE") : "N/A";
                String time = rs.getString("show_time");
                String status = rs.getString("approval_status");

                if (status == null)
                    status = "UNKNOWN";

                if (status.equalsIgnoreCase("PENDING")) {
                    modelPending.addRow(new Object[] { id, tName, mName, genre, duration, lang, time, status });
                } else {
                    modelHandled.addRow(new Object[] { id, tName, mName, genre, duration, lang, time, status });
                }
            }
        } catch (Exception ex) {
            System.out.println("Error fetching shows: " + ex.getMessage());
        }
    }

    private void processApproval(String newStatus) {
        String input = showIdField.getText().trim();
        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter a Show ID.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int showId = Integer.parseInt(input);

            String updateQuery = "UPDATE shows SET approval_status = ? WHERE show_id = ?";
            PreparedStatement ps = con.prepareStatement(updateQuery);
            ps.setString(1, newStatus);
            ps.setInt(2, showId);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(frame, "Show " + showId + " has been " + newStatus + ".");
                showIdField.setText("");
                loadShows(); // Refresh tables
            } else {
                JOptionPane.showMessageDialog(frame, "Show ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(frame, "Invalid Show ID. Must be a number.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Database error: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new AdminManageTheaters();
    }
}
