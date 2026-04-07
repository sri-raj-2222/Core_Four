import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class ThemeUI {

    // --- Cinematic Dark Theme Colors ---
    public static final Color BACKGROUND = new Color(15, 23, 42); // Dark Slate / Navy Desktop
    public static final Color PANEL_BG = new Color(30, 41, 59); // Elevated Panel
    public static final Color PRIMARY_TEXT = new Color(248, 250, 252); // Near White
    public static final Color SECONDARY_TEXT = new Color(148, 163, 184); // Slate Gray

    // Primary Accents (Gold/Amber for premium cinema feel)
    public static final Color PRIMARY_BTN = new Color(245, 158, 11); // Amber 500
    public static final Color PRIMARY_BTN_HOVER = new Color(217, 119, 6); // Amber 600

    // Danger/Cancel Accents (Red)
    public static final Color DANGER_BTN = new Color(225, 29, 72); // Rose 600
    public static final Color DANGER_BTN_HOVER = new Color(190, 18, 60); // Rose 700

    // Other Utility Accents
    public static final Color SUCCESS_BTN = new Color(16, 185, 129); // Emerald 500
    public static final Color BORDER_COLOR = new Color(51, 65, 85); // Slate 700
    public static final Color INPUT_BG = new Color(15, 23, 42);

    // --- Fonts ---
    public static final Font HEADING_FONT = new Font("Segoe UI", Font.BOLD, 26);
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    static {
        // Global Popup / JOptionPane Styling
        UIManager.put("OptionPane.background", PANEL_BG);
        UIManager.put("Panel.background", PANEL_BG);
        UIManager.put("OptionPane.messageForeground", PRIMARY_TEXT);
        UIManager.put("OptionPane.messageFont", BODY_FONT);
        UIManager.put("Button.background", PRIMARY_BTN);
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.font", BUTTON_FONT);
        UIManager.put("Button.border", new EmptyBorder(8, 15, 8, 15));
    }

    // --- Styling Methods ---

    /** Styles the base JFrame */
    public static void styleFrame(JFrame frame) {
        frame.getContentPane().setBackground(BACKGROUND);
    }

    /** Styles an elevated panel like a login box or dashboard card */
    public static void stylePanel(JPanel panel) {
        panel.setBackground(PANEL_BG);
        panel.setBorder(new LineBorder(BORDER_COLOR, 1, true));
    }

    /** Styles a standard text label */
    public static void styleLabel(JLabel label) {
        label.setForeground(PRIMARY_TEXT);
        label.setFont(BODY_FONT);
    }

    /** Styles a heading label */
    public static void styleHeading(JLabel label) {
        label.setForeground(PRIMARY_BTN); // Gold accent for headings
        label.setFont(HEADING_FONT);
        label.setHorizontalAlignment(SwingConstants.CENTER);
    }

    /** Styles a subtitle label */
    public static void styleSubtitle(JLabel label) {
        label.setForeground(PRIMARY_TEXT);
        label.setFont(TITLE_FONT);
    }

    /** Styles a standard primary action button */
    public static void styleButton(JButton btn) {
        btn.setBackground(PRIMARY_BTN);
        btn.setForeground(Color.WHITE);
        btn.setFont(BUTTON_FONT);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(8, 15, 8, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect listener
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(PRIMARY_BTN_HOVER);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(PRIMARY_BTN);
            }
        });
    }

    /** Styles a secondary or danger button (like logout/delete) */
    public static void styleDangerButton(JButton btn) {
        btn.setBackground(DANGER_BTN);
        btn.setForeground(Color.WHITE);
        btn.setFont(BUTTON_FONT);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(8, 15, 8, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(DANGER_BTN_HOVER);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(DANGER_BTN);
            }
        });
    }

    /** Styles Text Fields */
    public static void styleTextField(JTextField txt) {
        txt.setBackground(INPUT_BG);
        txt.setForeground(PRIMARY_TEXT);
        txt.setCaretColor(PRIMARY_TEXT);
        txt.setFont(BODY_FONT);
        txt.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1),
                new EmptyBorder(5, 8, 5, 8)));
    }

    /** Styles Password Fields */
    public static void stylePasswordField(JPasswordField txt) {
        txt.setBackground(INPUT_BG);
        txt.setForeground(PRIMARY_TEXT);
        txt.setCaretColor(PRIMARY_TEXT);
        txt.setFont(BODY_FONT);
        txt.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1),
                new EmptyBorder(5, 8, 5, 8)));
    }

    /** Styles a ComboBox */
    public static void styleComboBox(JComboBox<?> box) {
        box.setBackground(INPUT_BG);
        box.setForeground(PRIMARY_TEXT);
        box.setFont(BODY_FONT);
        box.setBorder(new LineBorder(BORDER_COLOR, 1));
    }

    /** Styles Tables */
    public static void styleTable(JTable table, JScrollPane scrollPane) {
        table.setBackground(PANEL_BG);
        table.setForeground(PRIMARY_TEXT);
        table.setFont(BODY_FONT);
        table.setGridColor(BORDER_COLOR);
        table.setRowHeight(30);
        table.setSelectionBackground(PRIMARY_BTN_HOVER);
        table.setSelectionForeground(Color.WHITE);

        table.getTableHeader().setBackground(BACKGROUND);
        table.getTableHeader().setForeground(PRIMARY_BTN); // Gold headers
        table.getTableHeader().setFont(BUTTON_FONT);

        if (scrollPane != null) {
            scrollPane.getViewport().setBackground(BACKGROUND);
            scrollPane.setBorder(new LineBorder(BORDER_COLOR, 1));
        }
    }
}
