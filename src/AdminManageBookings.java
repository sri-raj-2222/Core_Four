import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class AdminManageBookings {
    public static Connection con;
    private JFrame frame;

    public AdminManageBookings() {
        try {
            String dbURL = "jdbc:mysql://localhost:3306/movies_booking";
            String dbUser = "root";
            String dbPassword = "root";
            con = DriverManager.getConnection(dbURL, dbUser, dbPassword);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 1. Frame Setup
        frame = new JFrame("Admin - Manage Bookings");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());
        ThemeUI.styleFrame(frame);

        // 2. Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setPreferredSize(new Dimension(800, 600));
        ThemeUI.stylePanel(mainPanel);

        // 3. Top Header Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(ThemeUI.PANEL_BG);

        JButton btnBack = new JButton("← Back");
        ThemeUI.styleDangerButton(btnBack);
        topPanel.add(btnBack, BorderLayout.WEST);

        JLabel lblHeader = new JLabel("Booking Management", SwingConstants.CENTER);
        ThemeUI.styleHeading(lblHeader);
        topPanel.add(lblHeader, BorderLayout.CENTER);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // 4. Center Table Panel (View Bookings)
        String[] columnNames = { "Booking ID", "User Email", "Movie (Show)", "Seats", "Total Amount ($)", "Status" };
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        ThemeUI.styleTable(table, scrollPane);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // 5. Bottom Control Panel (Delete / Refresh)
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setBackground(ThemeUI.PANEL_BG);

        JButton btnDelete = new JButton("Cancel Selected Booking");
        ThemeUI.styleDangerButton(btnDelete);

        JButton btnRefresh = new JButton("Refresh Data");
        ThemeUI.styleButton(btnRefresh);

        bottomPanel.add(btnDelete);
        bottomPanel.add(btnRefresh);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        frame.add(mainPanel, new GridBagConstraints());

        // --- Action Listeners ---
        btnBack.addActionListener(e -> {
            frame.dispose();
            new AdminDash("admin");
        });

        btnRefresh.addActionListener(e -> loadBookings(tableModel));

        btnDelete.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int bookingId = (int) tableModel.getValueAt(selectedRow, 0);

                int confirm = JOptionPane.showConfirmDialog(frame, "Cancel Booking ID " + bookingId + "?",
                        "Confirm Cancel", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        PreparedStatement pst = con.prepareStatement("DELETE FROM bookings WHERE booking_id = ?");
                        pst.setInt(1, bookingId);
                        pst.executeUpdate();
                        JOptionPane.showMessageDialog(frame, "Booking Cancelled/Deleted.");
                        loadBookings(tableModel);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frame, "Error cancelling booking: " + ex.getMessage());
                    }
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a booking to cancel.");
            }
        });

        // Initial Load
        loadBookings(tableModel);

        frame.setVisible(true);
    }

    private void loadBookings(DefaultTableModel tableModel) {
        tableModel.setRowCount(0);
        try {
            if (con != null) {
                Statement st = con.createStatement();
                String query = "SELECT b.booking_id, b.user_email, m.MOVIE_NAME, b.seat_numbers, b.total_amount, b.status "
                        +
                        "FROM bookings b " +
                        "JOIN SHOWS s ON b.show_id = s.show_id " +
                        "JOIN MOVIES m ON s.movie_id = m.MOVIE_ID " +
                        "ORDER BY b.booking_date DESC";
                ResultSet rs = st.executeQuery(query);
                while (rs.next()) {
                    tableModel.addRow(new Object[] {
                            rs.getInt("booking_id"),
                            rs.getString("user_email"),
                            rs.getString("MOVIE_NAME"),
                            rs.getString("seat_numbers"),
                            rs.getDouble("total_amount"),
                            rs.getString("status")
                    });
                }
            }
        } catch (Exception e) {
            // Note: If no bookings exist yet or table is slightly mismatched, it logs here
            // gracefully
            System.out.println("Nothing to load for bookings: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new AdminManageBookings();
    }
}
