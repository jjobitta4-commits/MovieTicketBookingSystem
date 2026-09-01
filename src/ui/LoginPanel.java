package ui;

import dao.AdminDAO;
import dao.UserDAO;
import model.Admin;
import model.User;
import ui.common.RoundedButton;
import ui.common.UITheme;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {

    private final AppFrame app;
    private final JTextField identifierField = UITheme.styledTextField();
    private final JPasswordField passwordField = UITheme.styledPasswordField();
    private final JToggleButton adminToggle = new JToggleButton("Admin");
    private final JToggleButton customerToggle = new JToggleButton("Customer");
    private final JLabel identifierLabel = UITheme.label("Email");
    private final JLabel statusLabel = new JLabel(" ");
    private final JLabel registerLink = new JLabel("<html><u>New here? Create an account</u></html>");

    public LoginPanel(AppFrame app) {
        this.app = app;
        setLayout(new GridBagLayout());
        setBackground(UITheme.BG_LIGHT);

        JPanel card = UITheme.card();
        card.setLayout(new GridBagLayout());
        card.setPreferredSize(new Dimension(420, 480));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(6, 4, 6, 4);

        JLabel title = new JLabel("CineBook");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.PRIMARY);
        c.gridy = 0;
        card.add(title, c);

        JLabel subtitle = UITheme.label("Sign in to book your next movie experience");
        c.gridy = 1;
        c.insets = new Insets(0, 4, 18, 4);
        card.add(subtitle, c);

        // Role toggle
        JPanel togglePanel = new JPanel(new GridLayout(1, 2, 8, 0));
        togglePanel.setOpaque(false);
        ButtonGroup group = new ButtonGroup();
        group.add(customerToggle);
        group.add(adminToggle);
        customerToggle.setSelected(true);
        styleToggle(customerToggle);
        styleToggle(adminToggle);
        togglePanel.add(customerToggle);
        togglePanel.add(adminToggle);
        c.gridy = 2;
        c.insets = new Insets(0, 4, 16, 4);
        card.add(togglePanel, c);

        customerToggle.addActionListener(e -> identifierLabel.setText("Email"));
        adminToggle.addActionListener(e -> identifierLabel.setText("Username"));

        c.insets = new Insets(6, 4, 2, 4);
        c.gridy = 3;
        card.add(identifierLabel, c);
        c.gridy = 4;
        card.add(identifierField, c);

        c.gridy = 5;
        card.add(UITheme.label("Password"), c);
        c.gridy = 6;
        card.add(passwordField, c);

        RoundedButton loginBtn = new RoundedButton("Login");
        loginBtn.setPreferredSize(new Dimension(100, 42));
        c.gridy = 7;
        c.insets = new Insets(22, 4, 6, 4);
        card.add(loginBtn, c);

        statusLabel.setForeground(UITheme.DANGER);
        statusLabel.setFont(UITheme.FONT_LABEL);
        c.gridy = 8;
        c.insets = new Insets(4, 4, 4, 4);
        card.add(statusLabel, c);

        registerLink.setForeground(UITheme.PRIMARY);
        registerLink.setFont(UITheme.FONT_LABEL);
        registerLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerLink.setHorizontalAlignment(SwingConstants.CENTER);
        c.gridy = 9;
        c.insets = new Insets(10, 4, 4, 4);
        card.add(registerLink, c);
        registerLink.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                statusLabel.setText(" ");
                app.showRegister();
            }
        });

        JLabel hint = UITheme.label("Default admin -> admin / admin123");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        c.gridy = 10;
        card.add(hint, c);

        add(card);

        loginBtn.addActionListener(e -> doLogin());
        passwordField.addActionListener(e -> doLogin());
    }

    private void styleToggle(JToggleButton b) {
        b.setFont(UITheme.FONT_BOLD);
        b.setFocusPainted(false);
        b.setBackground(Color.WHITE);
    }

    private void doLogin() {
        String id = identifierField.getText().trim();
        String pw = new String(passwordField.getPassword());

        if (id.isEmpty() || pw.isEmpty()) {
            statusLabel.setText("Please fill in all fields.");
            return;
        }

        if (adminToggle.isSelected()) {
            Admin admin = new AdminDAO().authenticate(id, pw);
            if (admin != null) {
                clearAndReset();
                app.loginAsAdmin(admin);
            } else {
                statusLabel.setText("Invalid admin credentials.");
            }
        } else {
            User user = new UserDAO().authenticate(id, pw);
            if (user != null) {
                clearAndReset();
                app.loginAsCustomer(user);
            } else {
                statusLabel.setText("Invalid email or password.");
            }
        }
    }

    private void clearAndReset() {
        identifierField.setText("");
        passwordField.setText("");
        statusLabel.setText(" ");
    }
}
