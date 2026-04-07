import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;

public class TheaterManagerSeats {
    public static Connection con;
    private JFrame frame;
    private JComboBox<String> screenDropdown;
    private JPanel seatGridPanel;
    private JScrollPane scrollPane;
    private String theaterId;
    private Set<String> disabledSeats = new HashSet<>();

    private ArrayList<ScreenData> screensList = new ArrayList<>();

    public TheaterManagerSeats(String tId) {
        this.theaterId = tId;
        try {
            String dbURL = "jdbc:mysql://localhost:3306/movies_booking";
            String dbUser = "root";
            String dbPassword = "root";
            con = DriverManager.getConnection(dbURL, dbUser, dbPassword);
            System.out.println("Connected Successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 1. Frame Setup for Full Screen
        frame = new JFrame("Manager - Seat Layout Visualization (" + theaterId + ")");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Full screen
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());
        ThemeUI.styleFrame(frame);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setPreferredSize(new Dimension(900, 700));
        ThemeUI.stylePanel(mainPanel);

        // 2. Top Header Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(ThemeUI.PANEL_BG);
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JButton back = new JButton("← Back");
        ThemeUI.styleDangerButton(back);
        topPanel.add(back, BorderLayout.WEST);

        JLabel header = new JLabel("Screen Seat Visualization", SwingConstants.CENTER);
        ThemeUI.styleHeading(header);
        topPanel.add(header, BorderLayout.CENTER);

        // Control Panel
        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.setBackground(ThemeUI.PANEL_BG);

        JLabel lblSelect = new JLabel("Select Screen:");
        ThemeUI.styleSubtitle(lblSelect);
        controlPanel.add(lblSelect);

        screenDropdown = new JComboBox<>();
        screenDropdown.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        loadScreensIntoDropdown();
        controlPanel.add(screenDropdown);

        JButton btnView = new JButton("View Seats");
        ThemeUI.styleButton(btnView);
        controlPanel.add(btnView);

        JButton btnSaveLayout = new JButton("Save Layout");
        ThemeUI.styleButton(btnSaveLayout);
        btnSaveLayout.setBackground(new Color(245, 158, 11)); // Amber override for save
        controlPanel.add(btnSaveLayout);

        topPanel.add(controlPanel, BorderLayout.SOUTH);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // 3. Seat Grid Panel setup
        seatGridPanel = new JPanel();
        seatGridPanel.setLayout(new GridLayout(0, 20, 8, 8));
        seatGridPanel.setBackground(ThemeUI.BACKGROUND);
        seatGridPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        scrollPane = new JScrollPane(seatGridPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Add a "Screen Here" label at the bottom of the grid
        JPanel screenMarkerPanel = new JPanel(new BorderLayout());
        screenMarkerPanel.setBackground(Color.DARK_GRAY);
        JLabel screenLabel = new JLabel("SCREEN THIS WAY", SwingConstants.CENTER);
        screenLabel.setForeground(Color.WHITE);
        screenLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        screenLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        mainPanel.add(screenMarkerPanel, BorderLayout.SOUTH);

        frame.add(mainPanel, new GridBagConstraints());

        // --- Action Listeners ---
        back.addActionListener(e -> {
            frame.dispose();
            new TheaterManagerDash(theaterId);
        });

        btnView.addActionListener(e -> {
            int selectedIndex = screenDropdown.getSelectedIndex();
            if (selectedIndex >= 0) {
                ScreenData sd = screensList.get(selectedIndex);
                loadDisabledSeats(sd.id);
                renderSeats(sd.capacity);
            }
        });

        btnSaveLayout.addActionListener(e -> saveLayout());

        // Initially render if items exist
        if (screensList.size() > 0) {
            screenDropdown.setSelectedIndex(0);
            loadDisabledSeats(screensList.get(0).id);
            renderSeats(screensList.get(0).capacity);
        }

        frame.setVisible(true);
    }

    private void loadScreensIntoDropdown() {
        try {
            if (con == null)
                return;
            String query = "SELECT id, screen_number, capacity FROM screens WHERE theater_id = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, theaterId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String num = rs.getString("screen_number");
                int cap = rs.getInt("capacity");

                screensList.add(new ScreenData(id, num, cap));
                screenDropdown.addItem("Screen " + num + " (" + cap + " seats)");
            }
        } catch (Exception ex) {
            System.out.println("Error loading screens: " + ex.getMessage());
        }
    }

    private void renderSeats(int capacity) {
        seatGridPanel.removeAll(); // Clear existing seats

        // Use 10 columns per row to match User SeatSelection
        int cols = 10;
        int rows = (int) Math.ceil((double) capacity / cols);
        if (rows == 0)
            rows = 1;

        seatGridPanel.setLayout(new GridLayout(rows, cols, 10, 10));

        int seatCount = 0;

        for (int r = 0; r < rows; r++) {
            for (int c = 1; c <= cols; c++) {
                if (seatCount >= capacity) {
                    JPanel empty = new JPanel();
                    empty.setOpaque(false);
                    seatGridPanel.add(empty);
                } else {
                    char rowChar = (char) ('A' + r);
                    String seatLabelText = String.valueOf(rowChar) + c;
                    JButton seatBtn = new JButton(seatLabelText);
                    seatBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    seatBtn.setFocusPainted(false);
                    seatBtn.setForeground(Color.WHITE);

                    if (disabledSeats.contains(seatLabelText)) {
                        seatBtn.setBackground(ThemeUI.DANGER_BTN); // Red for disabled
                    } else {
                        seatBtn.setBackground(ThemeUI.SUCCESS_BTN); // Green for available
                    }

                    seatBtn.addActionListener(e -> {
                        Color current = seatBtn.getBackground();
                        if (current.equals(ThemeUI.SUCCESS_BTN)) {
                            seatBtn.setBackground(ThemeUI.DANGER_BTN);
                            disabledSeats.add(seatLabelText);
                        } else {
                            seatBtn.setBackground(ThemeUI.SUCCESS_BTN);
                            disabledSeats.remove(seatLabelText);
                        }
                    });

                    seatGridPanel.add(seatBtn);
                    seatCount++;
                }
            }
        }

        seatGridPanel.revalidate();
        seatGridPanel.repaint();
    }

    private void loadDisabledSeats(int screenId) {
        disabledSeats.clear();
        try {
            String query = "SELECT seat_number FROM disabled_seats WHERE screen_id = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, screenId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                disabledSeats.add(rs.getString("seat_number"));
            }
        } catch (Exception ex) {
            System.out.println("Error loading disabled seats: " + ex.getMessage());
        }
    }

    private void saveLayout() {
        int selectedIndex = screenDropdown.getSelectedIndex();
        if (selectedIndex < 0)
            return;

        ScreenData sd = screensList.get(selectedIndex);
        try {
            con.setAutoCommit(false);

            String deleteQuery = "DELETE FROM disabled_seats WHERE screen_id = ?";
            PreparedStatement delPs = con.prepareStatement(deleteQuery);
            delPs.setInt(1, sd.id);
            delPs.executeUpdate();

            String insertQuery = "INSERT INTO disabled_seats (screen_id, seat_number) VALUES (?, ?)";
            PreparedStatement insPs = con.prepareStatement(insertQuery);
            for (String seat : disabledSeats) {
                insPs.setInt(1, sd.id);
                insPs.setString(2, seat);
                insPs.addBatch();
            }
            insPs.executeBatch();

            con.commit();
            con.setAutoCommit(true);
            JOptionPane.showMessageDialog(frame, "Seat layout saved successfully!");
        } catch (Exception ex) {
            try {
                con.rollback();
            } catch (SQLException ignore) {
            }
            JOptionPane.showMessageDialog(frame, "Error saving layout: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private class ScreenData {
        int id;
        String screenNum;
        int capacity;

        ScreenData(int id, String num, int cap) {
            this.id = id;
            this.screenNum = num;
            this.capacity = cap;
        }
    }

    public static void main(String[] args) {
        new TheaterManagerSeats("TM001");
    }
}
