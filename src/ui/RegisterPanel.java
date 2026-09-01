package ui;

import dao.UserDAO;
import model.User;
import ui.common.RoundedButton;
import ui.common.UITheme;

import javax.swing.*;
import java.awt.*;
import java.util.regex.Pattern;

public class RegisterPanel extends JPanel {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");

    private final JTextField nameField = UITheme.styledTextField();
    private final JTextField emailField = UITheme.styledTextField();
    private final JTextField phoneField = UITheme.styledTextField();
    private final JPasswordField passwordField = UITheme.styledPasswordField();
    private final JPasswordField confirmField = UITheme.styledPasswordField();
    private final JLabel statusLabel = new JLabel(" ");

    public RegisterPanel(AppFrame app) {
        setLayout(new GridBagLayout());
        setBackground(UITheme.BG_LIGHT);

        JPanel card = UITheme.card();
        card.setLayout(new GridBagLayout());
        card.setPreferredSize(new Dimension(440, 560));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(6, 4, 6, 4);

        JLabel title = new JLabel("Create Your Account");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.PRIMARY);
        c.gridy = 0;
        card.add(title, c);

        JLabel subtitle = UITheme.label("Join CineBook and start booking in minutes");
        c.gridy = 1;
        c.insets = new Insets(0, 4, 16, 4);
        card.add(subtitle, c);

        int row = 2;
        row = addField(card, c, row, "Full Name", nameField);
        row = addField(card, c, row, "Email", emailField);
        row = addField(card, c, row, "Phone", phoneField);
        row = addField(card, c, row, "Password", passwordField);
        row = addField(card, c, row, "Confirm Password", confirmField);

        RoundedButton registerBtn = new RoundedButton("Register");
        registerBtn.setPreferredSize(new Dimension(120, 42));
        c.gridy = row++;
        c.insets = new Insets(20, 4, 6, 4);
        card.add(registerBtn, c);

        statusLabel.setForeground(UITheme.DANGER);
        statusLabel.setFont(UITheme.FONT_LABEL);
        c.gridy = row++;
        c.insets = new Insets(4, 4, 4, 4);
        card.add(statusLabel, c);

        JLabel backLink = new JLabel("<html><u>Already have an account? Login</u></html>");
        backLink.setForeground(UITheme.PRIMARY);
        backLink.setFont(UITheme.FONT_LABEL);
        backLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backLink.setHorizontalAlignment(SwingConstants.CENTER);
        c.gridy = row;
        c.insets = new Insets(8, 4, 4, 4);
        card.add(backLink, c);
        backLink.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                clearFields();
                app.showLogin();
            }
        });

        add(card);

        registerBtn.addActionListener(e -> doRegister(app));
    }

    private int addField(JPanel card, GridBagConstraints c, int row, String labelText, JTextField field) {
        c.insets = new Insets(6, 4, 2, 4);
        c.gridy = row++;
        card.add(UITheme.label(labelText), c);
        c.gridy = row++;
        card.add(field, c);
        return row;
    }

    private void doRegister(AppFrame app) {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String pw = new String(passwordField.getPassword());
        String confirm = new String(confirmField.getPassword());

        if (name.isEmpty() || email.isEmpty() || pw.isEmpty()) {
            statusLabel.setText("Please fill in the required fields.");
            return;
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            statusLabel.setText("Please enter a valid email address.");
            return;
        }
        if (pw.length() < 6) {
            statusLabel.setText("Password must be at least 6 characters.");
            return;
        }
        if (!pw.equals(confirm)) {
            statusLabel.setText("Passwords do not match.");
            return;
        }

        UserDAO dao = new UserDAO();
        if (dao.emailExists(email)) {
            statusLabel.setText("An account with this email already exists.");
            return;
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(pw);
        user.setPhone(phone);

        if (dao.register(user)) {
            JOptionPane.showMessageDialog(this,
                    "Registration successful! Please login with your new account.",
                    "Welcome to CineBook", JOptionPane.INFORMATION_MESSAGE);
            clearFields();
            app.showLogin();
        } else {
            statusLabel.setText("Registration failed. Please check the database connection.");
        }
    }

    private void clearFields() {
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        passwordField.setText("");
        confirmField.setText("");
        statusLabel.setText(" ");
    }
}
