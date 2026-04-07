import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class TheaterManagerLog {
    public static Connection con;
    private JFrame frame;

    TheaterManagerLog() {
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
        frame = new JFrame("Theater Manager Access");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Full screen
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Use GridBagLayout to center the inner content
        frame.setLayout(new GridBagLayout());
        ThemeUI.styleFrame(frame);

        // 2. Main Panel (The "Form Box")
        JPanel tmPanel = new JPanel();
        tmPanel.setLayout(null); // Absolute positioning inside the box
        tmPanel.setPreferredSize(new Dimension(600, 500));
        ThemeUI.stylePanel(tmPanel);

        // 3. Labels
        JLabel TMHeader = new JLabel("Theater Manager Login", SwingConstants.CENTER);
        TMHeader.setBounds(100, 60, 400, 40);
        ThemeUI.styleHeading(TMHeader);
        tmPanel.add(TMHeader);

        JLabel TMIDLabel = new JLabel("Theater ID: ");
        TMIDLabel.setBounds(100, 180, 150, 30);
        ThemeUI.styleLabel(TMIDLabel);
        tmPanel.add(TMIDLabel);

        JLabel TMPassLabel = new JLabel("Password: ");
        TMPassLabel.setBounds(100, 230, 150, 30);
        ThemeUI.styleLabel(TMPassLabel);
        tmPanel.add(TMPassLabel);

        // 4. Input Fields
        JTextField tmidField = new JTextField();
        tmidField.setBounds(260, 180, 250, 35);
        ThemeUI.styleTextField(tmidField);
        tmPanel.add(tmidField);

        // Password masking
        JPasswordField tmpassField = new JPasswordField();
        tmpassField.setBounds(260, 230, 250, 35);
        ThemeUI.stylePasswordField(tmpassField);
        tmPanel.add(tmpassField);

        // 5. Buttons
        JButton login = new JButton("LogIn");
        login.setBounds(150, 330, 120, 40);
        ThemeUI.styleButton(login);
        tmPanel.add(login);

        JButton reset = new JButton("Clear");
        reset.setBounds(300, 330, 120, 40);
        ThemeUI.styleButton(reset);
        tmPanel.add(reset);

        JButton back = new JButton("←");
        back.setBounds(10, 10, 60, 30);
        ThemeUI.styleDangerButton(back);
        tmPanel.add(back);

        // 6. Add the box to the center of the frame
        frame.add(tmPanel, new GridBagConstraints());

        // --- Action Listeners ---
        back.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new homePage();
            }
        });

        login.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String tmId = tmidField.getText().trim();
                String tmps = new String(tmpassField.getPassword()).trim();

                if (tmId.isEmpty() || tmps.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Required data missing", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (check(tmId, tmps)) {
                    frame.dispose();
                    new TheaterManagerDash(tmId);
                    JOptionPane.showMessageDialog(null, "Welcome Theater Manager!");
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid Theater Manager Credentials", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
                tmidField.setText("");
                tmpassField.setText("");
            }
        });

        reset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tmidField.setText("");
                tmpassField.setText("");
            }
        });

        frame.setVisible(true);
    }

    public static boolean check(String tmId, String tmps) {
        try {
            String query = "SELECT theater_id, password FROM theater_managers WHERE theater_id=? AND password=?";
            PreparedStatement p = con.prepareStatement(query);
            p.setString(1, tmId);
            p.setString(2, tmps);
            ResultSet rs = p.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) throws Exception {
        new TheaterManagerLog();
    }
}
