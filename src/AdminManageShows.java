import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class AdminManageShows {
    public static Connection con;
    private JFrame frame;
    private JComboBox<String> cbMovies, cbTheaters;

    public AdminManageShows() {
        try {
            String dbURL = "jdbc:mysql://localhost:3306/movies_booking";
            String dbUser = "root";
            String dbPassword = "root";
            con = DriverManager.getConnection(dbURL, dbUser, dbPassword);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 1. Frame Setup
        frame = new JFrame("Admin - Manage Shows");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());
        ThemeUI.styleFrame(frame);

        // 2. Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setPreferredSize(new Dimension(900, 600));
        ThemeUI.stylePanel(mainPanel);

        // 3. Top Header Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(ThemeUI.PANEL_BG);

        JButton btnBack = new JButton("← Back");
        ThemeUI.styleDangerButton(btnBack);
        topPanel.add(btnBack, BorderLayout.WEST);

        JLabel lblHeader = new JLabel("Schedule Movie Shows", SwingConstants.CENTER);
        ThemeUI.styleHeading(lblHeader);
        topPanel.add(lblHeader, BorderLayout.CENTER);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // 4. Center Table Panel (View Shows)
        String[] columnNames = { "Show ID", "Movie", "Theater", "Date & Time", "Ticket Price" };
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable showsTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(showsTable);
        ThemeUI.styleTable(showsTable, scrollPane);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // 5. Bottom Control Panel (Add/Delete Show Form)
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(3, 1, 5, 5));
        bottomPanel.setBackground(ThemeUI.PANEL_BG);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Row 1: Comboboxes
        JPanel row1 = new JPanel(new FlowLayout());
        row1.setBackground(ThemeUI.PANEL_BG);
        cbMovies = new JComboBox<>();
        cbTheaters = new JComboBox<>();
        loadDropdowns();

        JLabel l1 = new JLabel("Select Movie:");
        ThemeUI.styleSubtitle(l1);
        row1.add(l1);
        row1.add(cbMovies);

        JLabel l2 = new JLabel("  Select Theater:");
        ThemeUI.styleSubtitle(l2);
        row1.add(l2);
        row1.add(cbTheaters);
        bottomPanel.add(row1);

        // Row 2: Inputs
        JPanel row2 = new JPanel(new FlowLayout());
        row2.setBackground(ThemeUI.PANEL_BG);
        JTextField txtDateTime = new JTextField(15);
        ThemeUI.styleTextField(txtDateTime);
        JTextField txtPrice = new JTextField(8);
        ThemeUI.styleTextField(txtPrice);

        JLabel l3 = new JLabel("Date/Time (YYYY-MM-DD HH:MM:SS):");
        ThemeUI.styleSubtitle(l3);
        row2.add(l3);
        row2.add(txtDateTime);

        JLabel l4 = new JLabel("  Price:");
        ThemeUI.styleSubtitle(l4);
        row2.add(l4);
        row2.add(txtPrice);
        bottomPanel.add(row2);

        // Row 3: Buttons
        JPanel row3 = new JPanel(new FlowLayout());
        row3.setBackground(ThemeUI.PANEL_BG);

        JButton btnAdd = new JButton("Schedule Show");
        ThemeUI.styleButton(btnAdd);
        btnAdd.setBackground(ThemeUI.SUCCESS_BTN);

        JButton btnDelete = new JButton("Delete Selected Show");
        ThemeUI.styleDangerButton(btnDelete);

        JButton btnRefresh = new JButton("Refresh Data");
        ThemeUI.styleButton(btnRefresh);

        row3.add(btnAdd);
        row3.add(btnDelete);
        row3.add(btnRefresh);
        bottomPanel.add(row3);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        frame.add(mainPanel, new GridBagConstraints());

        // --- Action Listeners ---

        btnBack.addActionListener(e -> {
            frame.dispose();
            new AdminDash("admin");
        });

        btnRefresh.addActionListener(e -> {
            loadShows(tableModel);
        });

        btnAdd.addActionListener(e -> {
            try {
                String movieSelection = (String) cbMovies.getSelectedItem();
                String theaterSelection = (String) cbTheaters.getSelectedItem();
                String dateTime = txtDateTime.getText().trim();
                String priceStr = txtPrice.getText().trim();

                if (movieSelection == null || theaterSelection == null || dateTime.isEmpty() || priceStr.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please fill all fields!");
                    return;
                }

                int movieId = Integer.parseInt(movieSelection.split(" - ")[0]);
                int theaterId = Integer.parseInt(theaterSelection.split(" - ")[0]);
                double price = Double.parseDouble(priceStr);

                String query = "INSERT INTO SHOWS (movie_id, theater_id, show_time, price) VALUES (?, ?, ?, ?)";
                PreparedStatement pst = con.prepareStatement(query);
                pst.setInt(1, movieId);
                pst.setInt(2, theaterId);
                pst.setString(3, dateTime);
                pst.setDouble(4, price);

                pst.executeUpdate();
                JOptionPane.showMessageDialog(frame, "Show Successfully Scheduled!");
                txtDateTime.setText("");
                txtPrice.setText("");
                loadShows(tableModel);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
            }
        });

        btnDelete.addActionListener(e -> {
            int selectedRow = showsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int showId = (int) tableModel.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(frame, "Delete Show ID " + showId + "?", "Confirm",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        PreparedStatement pst = con.prepareStatement("DELETE FROM SHOWS WHERE show_id = ?");
                        pst.setInt(1, showId);
                        pst.executeUpdate();
                        loadShows(tableModel);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frame, "Error deleting show: " + ex.getMessage());
                    }
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a show to delete.");
            }
        });

        // Initial Load
        loadShows(tableModel);
        frame.setVisible(true);
    }

    private void loadDropdowns() {
        try {
            if (con == null)
                return;
            Statement st = con.createStatement();

            // Load Movies
            ResultSet rsMovies = st.executeQuery("SELECT MOVIE_ID, MOVIE_NAME FROM MOVIES");
            while (rsMovies.next()) {
                cbMovies.addItem(rsMovies.getInt("MOVIE_ID") + " - " + rsMovies.getString("MOVIE_NAME"));
            }

            // Load Theaters
            ResultSet rsTheaters = st.executeQuery("SELECT theater_id, name FROM theaters");
            while (rsTheaters.next()) {
                cbTheaters.addItem(rsTheaters.getInt("theater_id") + " - " + rsTheaters.getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadShows(DefaultTableModel tableModel) {
        tableModel.setRowCount(0);
        try {
            if (con != null) {
                Statement st = con.createStatement();
                String query = "SELECT s.show_id, m.MOVIE_NAME, t.name AS THEATER_NAME, s.show_time, s.price " +
                        "FROM SHOWS s " +
                        "JOIN MOVIES m ON s.movie_id = m.MOVIE_ID " +
                        "JOIN theaters t ON s.theater_id = t.theater_id ORDER BY s.show_time DESC";
                ResultSet rs = st.executeQuery(query);
                while (rs.next()) {
                    tableModel.addRow(new Object[] {
                            rs.getInt("show_id"),
                            rs.getString("MOVIE_NAME"),
                            rs.getString("THEATER_NAME"),
                            rs.getTimestamp("show_time").toString(),
                            rs.getDouble("price")
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new AdminManageShows();
    }
}
