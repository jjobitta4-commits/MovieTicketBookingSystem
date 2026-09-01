package ui.common;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A dark, fixed-width vertical navigation bar. Buttons highlight on hover
 * and the active item is visually distinguished - all painted with plain
 * Swing/Java2D (no CSS).
 */
public class Sidebar extends JPanel {

    private final Map<String, JButton> buttons = new LinkedHashMap<>();
    private String activeKey;

    public Sidebar(String appName, String roleLabel) {
        setLayout(new BorderLayout());
        setBackground(UITheme.PRIMARY_DARK);
        setPreferredSize(new Dimension(230, 0));

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(BorderFactory.createEmptyBorder(26, 20, 26, 20));

        JLabel title = new JLabel(appName);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel role = new JLabel(roleLabel);
        role.setFont(UITheme.FONT_SUBTITLE);
        role.setForeground(new Color(0xC7, 0xD2, 0xFE));
        role.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(title);
        header.add(Box.createVerticalStrut(4));
        header.add(role);

        add(header, BorderLayout.NORTH);

        itemsPanel.setOpaque(false);
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        add(itemsPanel, BorderLayout.CENTER);
    }

    private final JPanel itemsPanel = new JPanel();

    public void addNavItem(String key, String label, Consumer<String> onClick) {
        JButton btn = new JButton(label);
        btn.setFont(UITheme.FONT_BOLD);
        btn.setForeground(Color.WHITE);
        btn.setBackground(UITheme.PRIMARY_DARK);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(14, 22, 14, 12));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!key.equals(activeKey)) btn.setBackground(new Color(0x27, 0x3A, 0x7A));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!key.equals(activeKey)) btn.setBackground(UITheme.PRIMARY_DARK);
            }
        });

        btn.addActionListener(e -> {
            setActive(key);
            onClick.accept(key);
        });

        buttons.put(key, btn);
        itemsPanel.add(btn);
    }

    public void addSeparatorGap() {
        itemsPanel.add(Box.createVerticalStrut(14));
    }

    public void addFooterButton(String label, Runnable onClick) {
        add(Box.createVerticalGlue(), BorderLayout.CENTER);
        JButton btn = new JButton(label);
        btn.setFont(UITheme.FONT_BOLD);
        btn.setForeground(Color.WHITE);
        btn.setBackground(UITheme.DANGER);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> onClick.run());

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.setBorder(BorderFactory.createEmptyBorder(10, 16, 20, 16));
        wrap.add(btn, BorderLayout.CENTER);
        add(wrap, BorderLayout.SOUTH);
    }

    public void setActive(String key) {
        this.activeKey = key;
        for (Map.Entry<String, JButton> entry : buttons.entrySet()) {
            boolean isActive = entry.getKey().equals(key);
            entry.getValue().setBackground(isActive ? UITheme.ACCENT : UITheme.PRIMARY_DARK);
            entry.getValue().setForeground(isActive ? UITheme.TEXT_DARK : Color.WHITE);
        }
    }
}
