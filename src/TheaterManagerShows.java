import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class TheaterManagerShows {
    public static Connection con;
    private JFrame frame;
    private String theaterId;
    private int internalTheaterId = -1; // Correct numeric ID from DB
    private JComboBox<String> cbScreens, cbMovies;
    private JTextField txtTimings, txtPrice;
    private DefaultTableModel model;
    private JTable requestsTable;

    public TheaterManagerShows(String tId) {
        this.theaterId = tId;
        try {
            String dbURL = "jdbc:mysql://localhost:3306/movies_booking";
            String dbUser = "root";
            String dbPassword = "root";

            con = DriverManager.getConnection(dbURL, dbUser, dbPassword);
            System.out.println("Connected Successfully");

            // Ensure columns exist just in case
            try {
                Statement st = con.createStatement();
                st.execute("ALTER TABLE shows ADD COLUMN approval_status VARCHAR(20) DEFAULT 'PENDING'");
            } catch (Exception e) {
            }
            try {
                Statement st = con.createStatement();
                st.execute("ALTER TABLE shows ADD COLUMN screen_id INT");
            } catch (Exception e) {
            }

            // Fetch the actual Internal ID from theater_managers
            String fetchIdQuery = "SELECT id FROM theater_managers WHERE theater_id = ?";
            PreparedStatement getInternal = con.prepareStatement(fetchIdQuery);
            getInternal.setString(1, tId);
            ResultSet rsId = getInternal.executeQuery();
            if (rsId.next()) {
                this.internalTheaterId = rsId.getInt("id");
            } else {
                System.out.println("Critical Error: Theater Manager ID not found in database.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // 1. Frame Setup
        frame = new JFrame("Manager - Create and Manage Shows (" + theaterId + ")");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());
        ThemeUI.styleFrame(frame);

        // 2. Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setPreferredSize(new Dimension(900, 600));
        ThemeUI.stylePanel(mainPanel);

        // Top Header Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(ThemeUI.PANEL_BG);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton back = new JButton("← Back");
        ThemeUI.styleDangerButton(back);
        back.setPreferredSize(new Dimension(100, 30));
        topPanel.add(back, BorderLayout.WEST);

        JLabel header = new JLabel("Apply for a Movie (Create Show)", SwingConstants.CENTER);
        ThemeUI.styleHeading(header);
        topPanel.add(header, BorderLayout.CENTER);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // 3. Center Table (View Requests)
        String[] columns = { "Show ID", "Screen ID", "Movie Name", "Timings", "Price", "Status" };
        model = new DefaultTableModel(columns, 0);
        requestsTable = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(requestsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        ThemeUI.styleTable(requestsTable, scrollPane);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        loadRequests();

        // 4. Action Area (Bottom Panel)
        JPanel bottomPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        bottomPanel.setBackground(ThemeUI.PANEL_BG);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        JPanel row1 = new JPanel(new FlowLayout());
        row1.setBackground(ThemeUI.PANEL_BG);
        cbScreens = new JComboBox<>();
        cbMovies = new JComboBox<>();
        loadDropdowns();

        JLabel l1 = new JLabel("Screen ID:");
        ThemeUI.styleSubtitle(l1);
        row1.add(l1);
        row1.add(cbScreens);

        JLabel l2 = new JLabel("  Movie Name:");
        ThemeUI.styleSubtitle(l2);
        row1.add(l2);
        row1.add(cbMovies);
        bottomPanel.add(row1);

        JPanel row2 = new JPanel(new FlowLayout());
        row2.setBackground(ThemeUI.PANEL_BG);
        txtTimings = new JTextField(15);
        ThemeUI.styleTextField(txtTimings);
        txtPrice = new JTextField(8);
        ThemeUI.styleTextField(txtPrice);

        JLabel l3 = new JLabel("Timings (e.g. YYYY-MM-DD HH:MM:SS):");
        ThemeUI.styleSubtitle(l3);
        row2.add(l3);
        row2.add(txtTimings);

        JLabel l4 = new JLabel("  Price:");
        ThemeUI.styleSubtitle(l4);
        row2.add(l4);
        row2.add(txtPrice);
        bottomPanel.add(row2);

        JPanel row3 = new JPanel(new FlowLayout());
        row3.setBackground(ThemeUI.PANEL_BG);

        JButton btnRequest = new JButton("Request Admin Approval");
        ThemeUI.styleButton(btnRequest);

        JButton btnDelete = new JButton("Delete Request");
        ThemeUI.styleDangerButton(btnDelete);

        row3.add(btnRequest);
        row3.add(btnDelete);
        bottomPanel.add(row3);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        frame.add(mainPanel, new GridBagConstraints());

        // --- Action Listeners ---
        back.addActionListener(e -> {
            frame.dispose();
            new TheaterManagerDash(theaterId);
        });

        btnRequest.addActionListener(e -> requestMovie());
        btnDelete.addActionListener(e -> deleteRequest());

        frame.setVisible(true);
    }

    private void loadDropdowns() {
        try {
            if (con == null)
                return;
            Statement st = con.createStatement();

            // Load Screens for this theater
            String queryScreens = "SELECT id, screen_number FROM screens WHERE theater_id = '" + theaterId + "'";
            ResultSet rsScreens = st.executeQuery(queryScreens);
            while (rsScreens.next()) {
                cbScreens.addItem(rsScreens.getInt("id") + " - Screen " + rsScreens.getString("screen_number"));
            }

            // Load Movies
            ResultSet rsMovies = st.executeQuery("SELECT MOVIE_ID, MOVIE_NAME FROM MOVIES");
            while (rsMovies.next()) {
                cbMovies.addItem(rsMovies.getInt("MOVIE_ID") + " - " + rsMovies.getString("MOVIE_NAME"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadRequests() {
        try {
            if (con == null)
                return;
            model.setRowCount(0);

            String query = "SELECT s.show_id, s.screen_id, m.MOVIE_NAME, s.show_time, s.price, s.approval_status " +
                    "FROM shows s JOIN movies m ON s.movie_id = m.MOVIE_ID " +
                    "WHERE s.theater_id = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, internalTheaterId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[] {
                        rs.getInt("show_id"),
                        rs.getString("screen_id"),
                        rs.getString("MOVIE_NAME"),
                        rs.getString("show_time"),
                        rs.getDouble("price"),
                        rs.getString("approval_status")
                });
            }
        } catch (Exception ex) {
            System.out.println("Error fetching requests: " + ex.getMessage());
        }
    }

    private void requestMovie() {
        String screenSelection = (String) cbScreens.getSelectedItem();
        String movieSelection = (String) cbMovies.getSelectedItem();
        String timings = txtTimings.getText().trim();
        String priceStr = txtPrice.getText().trim();

        if (screenSelection == null || movieSelection == null || timings.isEmpty() || priceStr.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int screenId = Integer.parseInt(screenSelection.split(" - ")[0]);
            int movieId = Integer.parseInt(movieSelection.split(" - ")[0]);
            double price = Double.parseDouble(priceStr);

            String query = "INSERT INTO shows (theater_id, screen_id, movie_id, show_time, approval_status, price) VALUES (?, ?, ?, ?, 'PENDING', ?)";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, internalTheaterId);
            ps.setInt(2, screenId);
            ps.setInt(3, movieId);
            ps.setString(4, timings);
            ps.setDouble(5, price);

            ps.executeUpdate();

            txtTimings.setText("");
            txtPrice.setText("");
            loadRequests();
            JOptionPane.showMessageDialog(frame, "Movie successfully requested for Admin Approval.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void deleteRequest() {
        int selectedRow = requestsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a request to delete.", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int showId = (int) model.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(frame, "Delete this request?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String query = "DELETE FROM shows WHERE show_id = ?";
                PreparedStatement ps = con.prepareStatement(query);
                ps.setInt(1, showId);
                ps.executeUpdate();

                loadRequests();
                JOptionPane.showMessageDialog(frame, "Request deleted successfully.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error deleting request: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        new TheaterManagerShows("TM001");
    }
}
