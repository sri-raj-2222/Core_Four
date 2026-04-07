import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class AdminManageSeats {
    public static Connection con;
    private JFrame frame;

    public AdminManageSeats() {
        try {
            String dbURL = "jdbc:mysql://localhost:3306/movies_booking";
            String dbUser = "root";
            String dbPassword = "root";
            con = DriverManager.getConnection(dbURL, dbUser, dbPassword);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 1. Frame Setup
        frame = new JFrame("Admin - Seat Management");
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

        JLabel lblHeader = new JLabel("Seat Overview", SwingConstants.CENTER);
        ThemeUI.styleHeading(lblHeader);
        topPanel.add(lblHeader, BorderLayout.CENTER);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // 4. Center Table Panel (View Seats dynamically mapping)
        String[] columnNames = { "Seat ID", "Show ID", "Movie", "Seat Number", "Status" };
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        ThemeUI.styleTable(table, scrollPane);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // 5. Bottom Control Panel
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(2, 1));
        bottomPanel.setBackground(ThemeUI.PANEL_BG);

        // Row 1 inputs (Block/VIP)
        JPanel row1 = new JPanel(new FlowLayout());
        row1.setBackground(ThemeUI.PANEL_BG);

        JComboBox<String> cbStatus = new JComboBox<>(new String[] { "VIP", "Premium", "Blocked" });

        JLabel l1 = new JLabel("Set Status: ");
        ThemeUI.styleSubtitle(l1);
        row1.add(l1);
        row1.add(cbStatus);

        JButton btnUpdateStatus = new JButton("Update Selected Seat");
        ThemeUI.styleButton(btnUpdateStatus);
        row1.add(btnUpdateStatus);
        bottomPanel.add(row1);

        // Row 2 Buttons
        JPanel row2 = new JPanel(new FlowLayout());
        row2.setBackground(ThemeUI.PANEL_BG);
        JButton btnRefresh = new JButton("Refresh Data");
        ThemeUI.styleButton(btnRefresh);
        row2.add(btnRefresh);
        bottomPanel.add(row2);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        frame.add(mainPanel, new GridBagConstraints());

        // --- Action Listeners ---
        btnBack.addActionListener(e -> {
            frame.dispose();
            new AdminDash("admin");
        });

        btnRefresh.addActionListener(e -> loadSeats(tableModel));

        btnUpdateStatus.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int seatId = (int) tableModel.getValueAt(selectedRow, 0);
                String newStatus = (String) cbStatus.getSelectedItem();

                try {
                    PreparedStatement pst = con.prepareStatement("UPDATE seats SET status = ? WHERE seat_id = ?");
                    pst.setString(1, newStatus);
                    pst.setInt(2, seatId);
                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(frame, "Seat Status Updated to " + newStatus);
                    loadSeats(tableModel);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Error updating seat: " + ex.getMessage());
                }

            } else {
                JOptionPane.showMessageDialog(frame, "Please select a seat to update its status.");
            }
        });

        // Initial Load
        loadSeats(tableModel);

        frame.setVisible(true);
    }

    private void loadSeats(DefaultTableModel tableModel) {
        tableModel.setRowCount(0);
        try {
            if (con != null) {
                Statement st = con.createStatement();
                String query = "SELECT st.seat_id, s.show_id, m.MOVIE_NAME, st.seat_number, st.status " +
                        "FROM seats st " +
                        "JOIN shows s ON st.show_id = s.show_id " +
                        "JOIN movies m ON s.movie_id = m.MOVIE_ID";
                ResultSet rs = st.executeQuery(query);
                while (rs.next()) {
                    tableModel.addRow(new Object[] {
                            rs.getInt("seat_id"),
                            rs.getInt("show_id"),
                            rs.getString("MOVIE_NAME"),
                            rs.getString("seat_number"),
                            rs.getString("status")
                    });
                }
            }
        } catch (Exception e) {
            System.out.println("Nothing to load for seats or error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new AdminManageSeats();
    }
}
