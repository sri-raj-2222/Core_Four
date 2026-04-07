import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class AdminManageUsers {
    public static Connection con;
    private JFrame frame;

    public AdminManageUsers() {
        try {
            String dbURL = "jdbc:mysql://localhost:3306/movies_booking";
            String dbUser = "root";
            String dbPassword = "root";
            con = DriverManager.getConnection(dbURL, dbUser, dbPassword);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 1. Frame Setup
        frame = new JFrame("Admin - Manage Users");
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

        JLabel lblHeader = new JLabel("User Management", SwingConstants.CENTER);
        ThemeUI.styleHeading(lblHeader);
        topPanel.add(lblHeader, BorderLayout.CENTER);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // 4. Center Table Panel (View Users)
        String[] columnNames = { "Name", "Age", "Phone Number", "Email" };
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        ThemeUI.styleTable(table, scrollPane);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // 5. Bottom Control Panel (Delete / Block)
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setBackground(ThemeUI.PANEL_BG);

        JButton btnDelete = new JButton("Delete/Block Selected User");
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

        btnRefresh.addActionListener(e -> loadUsers(tableModel));

        btnDelete.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                String email = (String) tableModel.getValueAt(selectedRow, 3);

                int confirm = JOptionPane.showConfirmDialog(frame, "Block and Delete user: " + email + "?", "Confirm",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        PreparedStatement pst = con.prepareStatement("DELETE FROM USERS WHERE EMAIL = ?");
                        pst.setString(1, email);
                        pst.executeUpdate();
                        JOptionPane.showMessageDialog(frame, "User Deleted/Blocked.");
                        loadUsers(tableModel);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frame, "Error deleting user: " + ex.getMessage());
                    }
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a user to delete.");
            }
        });

        // Initial Load
        loadUsers(tableModel);

        frame.setVisible(true);
    }

    private void loadUsers(DefaultTableModel tableModel) {
        tableModel.setRowCount(0);
        try {
            if (con != null) {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("SELECT NAME, AGE, PHONE_NUMBER, EMAIL FROM USERS");
                while (rs.next()) {
                    tableModel.addRow(new Object[] {
                            rs.getString("NAME"),
                            rs.getInt("AGE"),
                            rs.getLong("PHONE_NUMBER"),
                            rs.getString("EMAIL")
                    });
                }
            }
        } catch (Exception e) {
            System.out.println("Nothing to load for users or error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new AdminManageUsers();
    }
}
