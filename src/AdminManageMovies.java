import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class AdminManageMovies {
    public static Connection con;
    private JFrame frame;

    public AdminManageMovies() {
        try {
            String dbURL = "jdbc:mysql://localhost:3306/movies_booking";
            String dbUser = "root";
            String dbPassword = "root";
            con = DriverManager.getConnection(dbURL, dbUser, dbPassword);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 1. Frame Setup
        frame = new JFrame("Admin - Manage Movies");
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

        JLabel lblHeader = new JLabel("Manage Database Movies", SwingConstants.CENTER);
        ThemeUI.styleHeading(lblHeader);
        topPanel.add(lblHeader, BorderLayout.CENTER);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // 4. Center Table Panel (View Movies)
        String[] columnNames = { "Movie ID", "Name", "Genre", "Duration (m)", "Language" };
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable movieTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(movieTable);
        ThemeUI.styleTable(movieTable, scrollPane);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // 5. Bottom Control Panel (Add, Update, Delete)
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 15));
        bottomPanel.setBackground(ThemeUI.PANEL_BG);

        JButton btnAdd = new JButton("Add Movie");
        ThemeUI.styleButton(btnAdd);
        btnAdd.setBackground(ThemeUI.SUCCESS_BTN);

        JButton btnUpdate = new JButton("Update Movie");
        ThemeUI.styleButton(btnUpdate);

        JButton btnDelete = new JButton("Delete Movie");
        ThemeUI.styleDangerButton(btnDelete);

        JButton btnRefresh = new JButton("Refresh List");
        ThemeUI.styleButton(btnRefresh);

        bottomPanel.add(btnAdd);
        bottomPanel.add(btnUpdate);
        bottomPanel.add(btnDelete);
        bottomPanel.add(btnRefresh);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        frame.add(mainPanel, new GridBagConstraints());

        // --- Action Listeners ---

        btnBack.addActionListener(e -> {
            frame.dispose();
            new AdminDash("admin"); // Assumes single admin generic return
        });

        btnAdd.addActionListener(e -> {
            frame.dispose();
            new AdminAddMovie(); // Uses previously written Add Movie page
        });

        btnRefresh.addActionListener(e -> {
            loadMovies(tableModel);
        });

        btnUpdate.addActionListener(e -> {
            int selectedRow = movieTable.getSelectedRow();
            if (selectedRow >= 0) {
                int movieId = (int) tableModel.getValueAt(selectedRow, 0);
                String movieName = (String) tableModel.getValueAt(selectedRow, 1);

                String movieGenre = (String) tableModel.getValueAt(selectedRow, 2);
                int movieDuration = (int) tableModel.getValueAt(selectedRow, 3);
                String movieLang = (String) tableModel.getValueAt(selectedRow, 4);

                // Fetch the Poster Path as it's not in the table
                String moviePath = "";
                try {
                    PreparedStatement getPath = con
                            .prepareStatement("SELECT POSTER_PATH FROM MOVIES WHERE MOVIE_ID = ?");
                    getPath.setInt(1, movieId);
                    ResultSet rs = getPath.executeQuery();
                    if (rs.next()) {
                        moviePath = rs.getString("POSTER_PATH");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                JTextField nameField = new JTextField(movieName);
                JTextField genreField = new JTextField(movieGenre);
                JTextField durField = new JTextField(String.valueOf(movieDuration));
                JTextField langField = new JTextField(movieLang);
                JTextField pathField = new JTextField(moviePath);

                Object[] message = {
                        "Movie Name:", nameField,
                        "Genre:", genreField,
                        "Duration (in mins):", durField,
                        "Language:", langField,
                        "Poster Path:", pathField
                };

                int option = JOptionPane.showConfirmDialog(frame, message, "Update Movie Details",
                        JOptionPane.OK_CANCEL_OPTION);

                if (option == JOptionPane.OK_OPTION) {
                    try {
                        String newName = nameField.getText().trim();
                        String newGenre = genreField.getText().trim();
                        int newDur = Integer.parseInt(durField.getText().trim());
                        String newLang = langField.getText().trim();
                        String newPath = pathField.getText().trim().replace("\\", "/");

                        if (newName.isEmpty() || newPath.isEmpty()) {
                            JOptionPane.showMessageDialog(frame, "Name and Path cannot be empty.");
                            return;
                        }

                        PreparedStatement pst = con.prepareStatement(
                                "UPDATE MOVIES SET MOVIE_NAME=?, MOVIE_GENRE=?, MOVIE_DURATION=?, MOVIE_LANGUAGE=?, POSTER_PATH=? WHERE MOVIE_ID=?");
                        pst.setString(1, newName);
                        pst.setString(2, newGenre);
                        pst.setString(3, String.valueOf(newDur));
                        pst.setString(4, newLang);
                        pst.setString(5, newPath);
                        pst.setInt(6, movieId);

                        pst.executeUpdate();
                        JOptionPane.showMessageDialog(frame, "Movie Updated successfully.");
                        loadMovies(tableModel); // Refresh table
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(frame, "Duration must be an integer.");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frame, "Error updating movie: " + ex.getMessage());
                    }
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a movie to update.");
            }
        });

        btnDelete.addActionListener(e -> {
            int selectedRow = movieTable.getSelectedRow();
            if (selectedRow >= 0) {
                int movieId = (int) tableModel.getValueAt(selectedRow, 0);
                String movieName = (String) tableModel.getValueAt(selectedRow, 1);

                int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete " + movieName + "?",
                        "Confirm Delete", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        PreparedStatement pst = con.prepareStatement("DELETE FROM MOVIES WHERE MOVIE_ID = ?");
                        pst.setInt(1, movieId);
                        pst.executeUpdate();
                        JOptionPane.showMessageDialog(frame, "Movie Deleted successfully.");
                        loadMovies(tableModel); // Refresh table
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frame, "Error deleting movie: " + ex.getMessage());
                    }
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a movie to delete.");
            }
        });

        // Initial Load
        loadMovies(tableModel);

        frame.setVisible(true);
    }

    private void loadMovies(DefaultTableModel tableModel) {
        tableModel.setRowCount(0); // Clear existing data
        try {
            if (con != null) {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(
                        "SELECT MOVIE_ID, MOVIE_NAME, MOVIE_GENRE, MOVIE_DURATION, MOVIE_LANGUAGE FROM MOVIES");
                while (rs.next()) {
                    tableModel.addRow(new Object[] {
                            rs.getInt("MOVIE_ID"),
                            rs.getString("MOVIE_NAME"),
                            rs.getString("MOVIE_GENRE"),
                            rs.getInt("MOVIE_DURATION"),
                            rs.getString("MOVIE_LANGUAGE")
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new AdminManageMovies();
    }
}
