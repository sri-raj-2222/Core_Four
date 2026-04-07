import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class UserBookings {
    public static Connection con;
    private JFrame frame;

    private String userEmail;

    public UserBookings(String email) {
        this.userEmail = email;
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
        frame = new JFrame("My Bookings");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // GridBagLayout to center the table container
        frame.setLayout(new GridBagLayout());
        ThemeUI.styleFrame(frame);

        // 2. Main Container Panel (The "Data Box")
        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(null);
        containerPanel.setPreferredSize(new Dimension(800, 600)); // Slightly wider for table data
        ThemeUI.stylePanel(containerPanel);

        // Header
        JLabel header = new JLabel("Your Booking History", SwingConstants.CENTER);
        header.setBounds(200, 20, 400, 30);
        ThemeUI.styleHeading(header);
        containerPanel.add(header);

        // 3. Table Setup
        // Column Names
        String[] columns = { "Booking ID", "Movie Name", "Show Date", "Seats", "Total Price" };
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(50, 80, 700, 400);

        ThemeUI.styleTable(table, scrollPane);
        containerPanel.add(scrollPane);

        // 4. Back button
        JButton back = new JButton("←");
        back.setBounds(10, 10, 60, 30);
        ThemeUI.styleDangerButton(back);
        containerPanel.add(back);

        // --- Logic to load data from MySQL ---
        try {
            String query = "SELECT b.id, m.MOVIE_NAME, s.show_time, b.seat_numbers, b.total_amount " +
                    "FROM bookings b " +
                    "JOIN shows s ON b.show_id = s.show_id " +
                    "JOIN movies m ON s.movie_ID = m.MOVIE_ID " +
                    "WHERE b.user_email = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, userEmail);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String fullTime = rs.getString("show_time");
                model.addRow(new Object[] {
                        rs.getString("id"),
                        rs.getString("MOVIE_NAME"),
                        fullTime,
                        rs.getString("seat_numbers"),
                        rs.getString("total_amount")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Action listener for table rows
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    String bookingId = model.getValueAt(row, 0).toString();
                    String movieName = model.getValueAt(row, 1).toString();
                    String showDate = model.getValueAt(row, 2).toString();
                    String seats = model.getValueAt(row, 3).toString();
                    String totalPrice = model.getValueAt(row, 4).toString();

                    String details = "Booking ID: " + bookingId + "\n"
                            + "Movie: " + movieName + "\n"
                            + "Time: " + showDate + "\n"
                            + "Seats: " + seats + "\n"
                            + "Total Price: Rs. " + totalPrice + "\n\n"
                            + "Do you want to cancel this ticket?";

                    Object[] options = { "OK", "Cancel Ticket" };
                    int choice = JOptionPane.showOptionDialog(frame, details, "Booking Details",
                            JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

                    if (choice == 1) { // "Cancel Ticket" selected (index 1)
                        int confirm = JOptionPane.showConfirmDialog(frame,
                                "Are you sure you want to cancel booking " + bookingId
                                        + "? This action cannot be undone.",
                                "Confirm Cancellation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                        if (confirm == JOptionPane.YES_OPTION) {
                            try {
                                String deleteQuery = "DELETE FROM bookings WHERE id = ?";
                                PreparedStatement delPst = con.prepareStatement(deleteQuery);
                                delPst.setString(1, bookingId);
                                int affected = delPst.executeUpdate();

                                if (affected > 0) {
                                    JOptionPane.showMessageDialog(frame, "Ticket cancelled successfully.", "Success",
                                            JOptionPane.INFORMATION_MESSAGE);
                                    model.removeRow(row); // Remove from UI
                                } else {
                                    JOptionPane.showMessageDialog(frame,
                                            "Failed to cancel ticket. It might have already been removed.", "Error",
                                            JOptionPane.ERROR_MESSAGE);
                                }
                            } catch (SQLException ex) {
                                JOptionPane.showMessageDialog(frame, "Database error: " + ex.getMessage(), "Error",
                                        JOptionPane.ERROR_MESSAGE);
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            }
        });

        // 5. Action Listeners
        back.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new UserDash(userEmail);
            }
        });

        // Add the container to the frame
        frame.add(containerPanel, new GridBagConstraints());

        frame.setVisible(true);
    }

    public static void main(String[] args) throws Exception {
        // new UserBookings("test@example.com");
    }
}