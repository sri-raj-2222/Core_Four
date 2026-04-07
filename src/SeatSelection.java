import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;

public class SeatSelection {
    private JFrame frame;
    private int showId;
    private String movieName;
    private String theaterName;
    private String time;
    private String userEmail;
    private int capacity = 0;
    private int screenId = -1; // Added screenId
    private double ticketPrice = 150.0; // Default price if not found
    private Set<String> bookedSeats;
    private Set<String> selectedSeats;

    public SeatSelection(int showId, String movieName, String theaterName, String time, String userEmail) {
        this.showId = showId;
        this.movieName = movieName;
        this.theaterName = theaterName;
        this.time = time;
        this.userEmail = userEmail;
        this.bookedSeats = new HashSet<>();
        this.selectedSeats = new HashSet<>();

        // 1. Fetch data from DB
        fetchShowData();
        fetchBookedSeats();
        fetchDisabledSeats();

        // 2. UI Setup
        frame = new JFrame("Select Seats - " + movieName);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());
        ThemeUI.styleFrame(frame);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setPreferredSize(new Dimension(900, 700));
        ThemeUI.stylePanel(mainPanel);

        // Top Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(ThemeUI.PANEL_BG);
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JButton back = new JButton("← Back");
        ThemeUI.styleDangerButton(back);
        back.addActionListener(e -> {
            frame.dispose();
            new UserMovies(userEmail);
        });
        topPanel.add(back, BorderLayout.WEST);

        JLabel header = new JLabel("<html><center><b>" + movieName + "</b><br><small>" + theaterName + " | " + time
                + "</small></center></html>");
        ThemeUI.styleHeading(header);
        topPanel.add(header, BorderLayout.CENTER);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Center Panel (Grid of seats)
        JPanel gridPanel = new JPanel();
        // Calculate columns and rows based on capacity. e.g. 10 columns per row
        int cols = 10;
        int rows = (int) Math.ceil((double) capacity / cols);
        if (rows == 0)
            rows = 1;
        gridPanel.setLayout(new GridLayout(rows, cols, 10, 10));
        gridPanel.setBackground(ThemeUI.BACKGROUND);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Screen Label
        JPanel screenPanel = new JPanel();
        screenPanel.setBackground(ThemeUI.BORDER_COLOR);
        JLabel screenLabel = new JLabel("S C R E E N    T H I S    W A Y", SwingConstants.CENTER);
        ThemeUI.styleSubtitle(screenLabel);
        screenPanel.add(screenLabel);

        JPanel centerContainer = new JPanel(new BorderLayout());
        centerContainer.setBackground(ThemeUI.BACKGROUND);
        centerContainer.add(screenPanel, BorderLayout.NORTH);

        // Generate Buttons
        for (int i = 1; i <= capacity; i++) {
            // Assign a row letter A, B, C...
            char rowChar = (char) ('A' + ((i - 1) / cols));
            int seatNum = ((i - 1) % cols) + 1;
            String seatName = rowChar + String.valueOf(seatNum);

            JButton seatBtn = new JButton(seatName);
            seatBtn.setFont(new Font("Arial", Font.BOLD, 12));
            seatBtn.setFocusPainted(false);
            seatBtn.setBorder(BorderFactory.createLineBorder(ThemeUI.BORDER_COLOR, 1, true));

            if (bookedSeats.contains(seatName)) {
                seatBtn.setBackground(ThemeUI.DANGER_BTN); // RED (Booked)
                seatBtn.setForeground(Color.WHITE);
                seatBtn.setEnabled(false);
            } else {
                seatBtn.setBackground(ThemeUI.SUCCESS_BTN); // GREEN (Available)
                seatBtn.setForeground(Color.WHITE);
                seatBtn.addActionListener(new SeatClickListener(seatBtn, seatName));
            }
            gridPanel.add(seatBtn);
        }

        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setBorder(null);
        centerContainer.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(centerContainer, BorderLayout.CENTER);

        // Bottom Panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        bottomPanel.setBackground(ThemeUI.PANEL_BG);
        bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ThemeUI.BORDER_COLOR));

        JLabel legendGreen = new JLabel("Available: ■ ");
        legendGreen.setForeground(ThemeUI.SUCCESS_BTN);
        JLabel legendRed = new JLabel("Booked: ■ ");
        legendRed.setForeground(ThemeUI.DANGER_BTN);
        JLabel legendBlue = new JLabel("Selected: ■ ");
        legendBlue.setForeground(ThemeUI.PRIMARY_BTN);

        bottomPanel.add(legendGreen);
        bottomPanel.add(legendRed);
        bottomPanel.add(legendBlue);

        JButton btnBook = new JButton("Book Selected Seats");
        ThemeUI.styleButton(btnBook);
        btnBook.setPreferredSize(new Dimension(250, 40));
        btnBook.addActionListener(e -> finalizeBooking());

        bottomPanel.add(btnBook);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        frame.add(mainPanel, new GridBagConstraints());
        frame.setVisible(true);
    }

    private void fetchShowData() {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/movies_booking", "root",
                "root")) {
            // Get capacity and screenid from screens table
            String query = "SELECT sc.id as screen_id, sc.capacity, s.price FROM shows s JOIN screens sc ON s.screen_id = sc.id WHERE s.show_id = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, showId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                this.screenId = rs.getInt("screen_id");
                this.capacity = rs.getInt("capacity");
                if (rs.getObject("price") != null) {
                    this.ticketPrice = rs.getDouble("price");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Safety fallback if no capacity found
        if (this.capacity <= 0)
            this.capacity = 50;
    }

    private void fetchBookedSeats() {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/movies_booking", "root",
                "root")) {
            String query = "SELECT seat_numbers FROM bookings WHERE show_id = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, showId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String seatsString = rs.getString("seat_numbers");
                if (seatsString != null && !seatsString.isEmpty()) {
                    String[] seatsArray = seatsString.split(",");
                    for (String s : seatsArray) {
                        bookedSeats.add(s.trim());
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void fetchDisabledSeats() {
        if (screenId == -1)
            return; // If screenId not found, skip

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/movies_booking", "root",
                "root")) {
            String query = "SELECT seat_number FROM disabled_seats WHERE screen_id = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, screenId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String seatNum = rs.getString("seat_number");
                if (seatNum != null && !seatNum.isEmpty()) {
                    bookedSeats.add(seatNum.trim());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private class SeatClickListener implements ActionListener {
        private JButton btn;
        private String seatName;

        public SeatClickListener(JButton btn, String seatName) {
            this.btn = btn;
            this.seatName = seatName;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (selectedSeats.contains(seatName)) {
                // Deselect
                selectedSeats.remove(seatName);
                btn.setBackground(ThemeUI.SUCCESS_BTN);
            } else {
                // Select
                selectedSeats.add(seatName);
                btn.setBackground(ThemeUI.PRIMARY_BTN);
            }
        }
    }

    private void finalizeBooking() {
        if (selectedSeats.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please select at least one seat.", "No Seats Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        double totalAmount = selectedSeats.size() * ticketPrice;
        String allSeats = String.join(",", selectedSeats);

        int confirm = JOptionPane.showConfirmDialog(frame,
                "Are you sure you want to book these seats?\n" +
                        "Seats: " + allSeats + "\n" +
                        "Total Price: Rs. " + totalAmount,
                "Confirm Booking", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/movies_booking", "root",
                    "root")) {

                // 2. Insert into bookings
                String bookingId = "BOK-" + System.currentTimeMillis();
                String insertQ = "INSERT INTO bookings (id, user_email, show_id, seat_numbers, total_amount, status) VALUES (?, ?, ?, ?, ?, 'CONFIRMED')";
                PreparedStatement insertPst = con.prepareStatement(insertQ);
                insertPst.setString(1, bookingId);
                insertPst.setString(2, userEmail);
                insertPst.setInt(3, showId);
                insertPst.setString(4, allSeats);
                insertPst.setDouble(5, totalAmount);
                insertPst.executeUpdate();

                JOptionPane.showMessageDialog(frame, "Booking Successful!\nBooking ID: " + bookingId, "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();
                new UserBookings(userEmail); // Redirect to My Bookings

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Database error: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
