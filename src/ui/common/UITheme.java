package ui.common;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Centralised color palette / fonts / small styling helpers so every screen
 * in the app looks consistent. Pure Java2D / Swing - no CSS, no external libs.
 */
public class UITheme {

    public static final Color PRIMARY      = new Color(0x1E, 0x3A, 0x8A); // deep indigo
    public static final Color PRIMARY_DARK = new Color(0x14, 0x28, 0x5C);
    public static final Color ACCENT       = new Color(0xF5, 0x9E, 0x0B); // amber
    public static final Color ACCENT_DARK  = new Color(0xD9, 0x82, 0x00);
    public static final Color BG_LIGHT     = new Color(0xF4, 0xF6, 0xFB);
    public static final Color CARD_BG      = Color.WHITE;
    public static final Color TEXT_DARK    = new Color(0x1F, 0x29, 0x37);
    public static final Color TEXT_MUTED   = new Color(0x6B, 0x72, 0x80);
    public static final Color BORDER       = new Color(0xE2, 0xE5, 0xEC);
    public static final Color SUCCESS      = new Color(0x16, 0xA3, 0x4A);
    public static final Color DANGER       = new Color(0xDC, 0x26, 0x26);

    public static final Font FONT_TITLE    = new Font("Segoe UI", Font.BOLD, 26);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_HEADING  = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_LABEL    = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BOLD     = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_FIELD    = new Font("Segoe UI", Font.PLAIN, 14);

    public static void applyNimbus() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
            // fall back to default look and feel
        }
        UIManager.put("control", BG_LIGHT);
        UIManager.put("nimbusBase", PRIMARY);
        UIManager.put("nimbusBlueGrey", new Color(0xCB, 0xD3, 0xE1));
        UIManager.put("text", TEXT_DARK);
    }

    public static JTextField styledTextField() {
        JTextField field = new JTextField();
        styleField(field);
        return field;
    }

    public static JPasswordField styledPasswordField() {
        JPasswordField field = new JPasswordField();
        styleField(field);
        return field;
    }

    public static void styleField(JTextField field) {
        field.setFont(FONT_FIELD);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                new EmptyBorder(8, 10, 8, 10)));
        field.setBackground(Color.WHITE);
    }

    public static JLabel heading(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_HEADING);
        l.setForeground(TEXT_DARK);
        return l;
    }

    public static JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_LABEL);
        l.setForeground(TEXT_MUTED);
        return l;
    }

    public static JPanel card() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                new EmptyBorder(18, 18, 18, 18)));
        return panel;
    }
}
