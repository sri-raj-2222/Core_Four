import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class TheaterManagerScreens {
    public static Connection con;
    private JFrame frame;
    private JTable table;
    private DefaultTableModel model;
    private JTextField screenNumField, capacityField;
    private String theaterId;

    public TheaterManagerScreens(String tId) {
        this.theaterId = tId;
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
        frame = new JFrame("Manager - Screens (" + theaterId + ")");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Full screen
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Use GridBagLayout to center the inner content
        frame.setLayout(new GridBagLayout());
        ThemeUI.styleFrame(frame);

        // 2. Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setPreferredSize(new Dimension(800, 600));
        ThemeUI.stylePanel(mainPanel);

        // Top Header Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(ThemeUI.PANEL_BG);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton back = new JButton("←");
        ThemeUI.styleDangerButton(back);
        back.setPreferredSize(new Dimension(60, 30));
        topPanel.add(back, BorderLayout.WEST);

        JLabel header = new JLabel("Add/Edit Screens", SwingConstants.CENTER);
        ThemeUI.styleHeading(header);
        topPanel.add(header, BorderLayout.CENTER);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // 3. Table Setup
        String[] columns = { "Screen ID", "Screen Number", "Seat Capacity" };
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        ThemeUI.styleTable(table, scrollPane);

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Fetch Data from database
        loadScreens();

        // 4. Action Area (Bottom Panel)
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        bottomPanel.setBackground(ThemeUI.PANEL_BG);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        JLabel lblNum = new JLabel("Screen #:");
        ThemeUI.styleSubtitle(lblNum);
        bottomPanel.add(lblNum);

        screenNumField = new JTextField(5);
        ThemeUI.styleTextField(screenNumField);
        bottomPanel.add(screenNumField);

        JLabel lblCap = new JLabel("Capacity:");
        ThemeUI.styleSubtitle(lblCap);
        bottomPanel.add(lblCap);

        capacityField = new JTextField(5);
        ThemeUI.styleTextField(capacityField);
        bottomPanel.add(capacityField);

        JButton btnAdd = new JButton("Add Screen");
        ThemeUI.styleButton(btnAdd);
        bottomPanel.add(btnAdd);

        JButton btnDelete = new JButton("Delete Selected");
        ThemeUI.styleDangerButton(btnDelete);
        bottomPanel.add(btnDelete);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Add mainPanel to frame
        frame.add(mainPanel, new GridBagConstraints());

        // --- Action Listeners ---
        back.addActionListener(e -> {
            frame.dispose();
            new TheaterManagerDash(theaterId);
        });
        btnAdd.addActionListener(e -> addScreen());
        btnDelete.addActionListener(e -> deleteScreen());

        frame.setVisible(true);
    }

    private void loadScreens() {
        try {
            if (con == null)
                return;
            model.setRowCount(0);

            String query = "SELECT id, screen_number, capacity FROM screens WHERE theater_id = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, theaterId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String num = rs.getString("screen_number");
                int cap = rs.getInt("capacity");

                model.addRow(new Object[] { id, num, cap });
            }
        } catch (Exception ex) {
            System.out.println("Error fetching screens: " + ex.getMessage());
        }
    }

    private void addScreen() {
        String sNum = screenNumField.getText().trim();
        String sCap = capacityField.getText().trim();

        if (sNum.isEmpty() || sCap.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter both Screen Number and Capacity.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int capacity = Integer.parseInt(sCap);

            String query = "INSERT INTO screens (screen_number, capacity, theater_id) VALUES (?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, sNum);
            ps.setInt(2, capacity);
            ps.setString(3, theaterId);

            int rows = ps.executeUpdate();

            screenNumField.setText("");
            capacityField.setText("");
            loadScreens(); // Refresh table
            JOptionPane.showMessageDialog(frame, "Screen added successfully.");
        } catch (SQLIntegrityConstraintViolationException dup) {
            JOptionPane.showMessageDialog(frame, "Screen Number already exists!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(frame, "Capacity must be a number.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteScreen() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a screen from the table to delete.", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int screenId = (int) model.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete this screen?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String query = "DELETE FROM screens WHERE id = ? AND theater_id = ?";
                PreparedStatement ps = con.prepareStatement(query);
                ps.setInt(1, screenId);
                ps.setString(2, theaterId);
                ps.executeUpdate();

                loadScreens(); // Refresh table
                JOptionPane.showMessageDialog(frame, "Screen deleted successfully.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error deleting screen: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        new TheaterManagerScreens("TM001");
    }
}
