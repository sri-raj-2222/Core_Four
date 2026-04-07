import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class AdminTheaters {
    public static Connection con;
    private JFrame frame;
    private JTable table;
    private DefaultTableModel model;

    public AdminTheaters() {
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
        frame = new JFrame("Admin - Theaters Management");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Full screen
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Use GridBagLayout to center the inner content
        frame.setLayout(new GridBagLayout());
        ThemeUI.styleFrame(frame);

        // 2. Main Panel (The "Screen Box")
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setPreferredSize(new Dimension(1000, 650));
        ThemeUI.stylePanel(mainPanel);

        // Top Panel for Header and Back Button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(ThemeUI.PANEL_BG);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton back = new JButton("←");
        ThemeUI.styleDangerButton(back);
        back.setPreferredSize(new Dimension(60, 30));
        topPanel.add(back, BorderLayout.WEST);

        JLabel header = new JLabel("Registered Theaters", SwingConstants.CENTER);
        ThemeUI.styleHeading(header);
        topPanel.add(header, BorderLayout.CENTER);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // 3. Table Setup
        String[] columns = { "ID", "Theater ID", "Name", "Num Screens", "Status" };
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        ThemeUI.styleTable(table, scrollPane);

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Add Theater Panel
        JPanel addPanel = new JPanel(new BorderLayout());
        addPanel.setBackground(ThemeUI.PANEL_BG);
        addPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ThemeUI.BORDER_COLOR),
                "Add New Theater (Manager Account)",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                ThemeUI.BUTTON_FONT, ThemeUI.PRIMARY_TEXT));

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        inputPanel.setBackground(ThemeUI.PANEL_BG);

        JLabel lblTid = new JLabel("Theater ID:");
        ThemeUI.styleSubtitle(lblTid);
        JTextField txtTid = new JTextField(10);
        ThemeUI.styleTextField(txtTid);

        JLabel lblName = new JLabel("Name:");
        ThemeUI.styleSubtitle(lblName);
        JTextField txtName = new JTextField(15);
        ThemeUI.styleTextField(txtName);

        JLabel lblPass = new JLabel("Password:");
        ThemeUI.styleSubtitle(lblPass);
        JPasswordField txtPass = new JPasswordField(10);
        ThemeUI.stylePasswordField(txtPass);

        inputPanel.add(lblTid);
        inputPanel.add(txtTid);
        inputPanel.add(lblName);
        inputPanel.add(txtName);
        inputPanel.add(lblPass);
        inputPanel.add(txtPass);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnPanel.setBackground(ThemeUI.PANEL_BG);

        JButton btnAdd = new JButton("Add Theater");
        ThemeUI.styleButton(btnAdd);
        btnAdd.setBackground(ThemeUI.SUCCESS_BTN);
        btnPanel.add(btnAdd);

        JButton btnDelete = new JButton("Delete Selected Theater");
        ThemeUI.styleDangerButton(btnDelete);
        btnPanel.add(btnDelete);

        addPanel.add(inputPanel, BorderLayout.CENTER);
        addPanel.add(btnPanel, BorderLayout.SOUTH);

        mainPanel.add(addPanel, BorderLayout.SOUTH);

        // Fetch Data from database
        loadTheaters();

        // 4. Add the mainPanel to the Frame
        frame.add(mainPanel, new GridBagConstraints());

        // --- Action Listeners ---
        back.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new AdminDash("admin"); // Assuming default placeholder value for dashboard
            }
        });

        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String tid = txtTid.getText().trim();
                String name = txtName.getText().trim();
                String pass = new String(txtPass.getPassword()).trim();

                if (tid.isEmpty() || name.isEmpty() || pass.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "All fields are required!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    String query = "INSERT INTO theater_managers (theater_id, name, password, num_screens) VALUES (?, ?, ?, 0)";
                    PreparedStatement ps = con.prepareStatement(query);
                    ps.setString(1, tid);
                    ps.setString(2, name);
                    ps.setString(3, pass);
                    ps.executeUpdate();

                    JOptionPane.showMessageDialog(frame, "Theater added successfully!");
                    txtTid.setText("");
                    txtName.setText("");
                    txtPass.setText("");
                    loadTheaters();

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Error adding theater: " + ex.getMessage(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    String tid = (String) model.getValueAt(selectedRow, 1);
                    String tName = (String) model.getValueAt(selectedRow, 2);

                    int confirm = JOptionPane.showConfirmDialog(frame,
                            "Are you sure you want to delete theater: " + tName
                                    + "?\nAll associated screens and shows might be affected.",
                            "Confirm Delete", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        try {
                            PreparedStatement ps = con
                                    .prepareStatement("DELETE FROM theater_managers WHERE theater_id = ?");
                            ps.setString(1, tid);
                            ps.executeUpdate();
                            JOptionPane.showMessageDialog(frame, "Theater deleted successfully!");
                            loadTheaters();
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(frame,
                                    "Cannot delete theater. It may have existing dependencies (screens/shows/bookings).",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select a theater from the list to delete.");
                }
            }
        });

        frame.setVisible(true);
    }

    private void loadTheaters() {
        try {
            if (con == null)
                return;
            model.setRowCount(0); // Clear existing rows
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(
                    "SELECT id, theater_id, name, (SELECT COUNT(*) FROM screens s WHERE s.theater_id = tm.theater_id) AS actual_screens, status FROM theater_managers tm");

            while (rs.next()) {
                String id = String.valueOf(rs.getInt("id"));
                String loginId = rs.getString("theater_id");
                String name = rs.getString("name");
                int numScreens = rs.getInt("actual_screens");
                String status = rs.getString("status");

                model.addRow(new Object[] { id, loginId, name, numScreens, status });
            }
        } catch (Exception ex) {
            System.out.println("Error fetching theaters: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new AdminTheaters();
    }
}
