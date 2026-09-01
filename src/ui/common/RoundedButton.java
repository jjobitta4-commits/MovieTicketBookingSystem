package ui.common;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * A flat, rounded button painted with Java2D so the app has a modern look
 * without relying on any external UI library or CSS.
 */
public class RoundedButton extends JButton {

    private Color baseColor;
    private Color hoverColor;
    private final Color textColor;
    private boolean hovering = false;

    public RoundedButton(String text) {
        this(text, UITheme.PRIMARY, UITheme.PRIMARY_DARK, Color.WHITE);
    }

    public RoundedButton(String text, Color base, Color hover, Color textColor) {
        super(text);
        this.baseColor = base;
        this.hoverColor = hover;
        this.textColor = textColor;
        setFont(UITheme.FONT_BOLD);
        setForeground(textColor);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovering = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovering = false;
                repaint();
            }
        });
    }

    public void setColors(Color base, Color hover) {
        this.baseColor = base;
        this.hoverColor = hover;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(hovering ? hoverColor : baseColor);
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 16, 16));
        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    public boolean contains(int x, int y) {
        return new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16).contains(x, y);
    }
}
