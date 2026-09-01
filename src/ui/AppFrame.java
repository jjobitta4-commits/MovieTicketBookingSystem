package ui;

import model.Admin;
import model.User;
import ui.admin.AdminHomePanel;
import ui.customer.CustomerHomePanel;

import javax.swing.*;
import java.awt.*;

/**
 * Root application window. Uses a single CardLayout to switch between the
 * Login, Register, Admin and Customer sections instead of opening many
 * separate windows - gives the app a cohesive "single system" feel.
 */
public class AppFrame extends JFrame {

    public static final String LOGIN = "LOGIN";
    public static final String REGISTER = "REGISTER";
    public static final String ADMIN_HOME = "ADMIN_HOME";
    public static final String CUSTOMER_HOME = "CUSTOMER_HOME";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel root = new JPanel(cardLayout);

    private User currentUser;
    private Admin currentAdmin;

    private AdminHomePanel adminHomePanel;
    private CustomerHomePanel customerHomePanel;

    public AppFrame() {
        setTitle("CineBook - Movie Ticket Booking System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1024, 680));
        setLocationRelativeTo(null);

        root.add(new LoginPanel(this), LOGIN);
        root.add(new RegisterPanel(this), REGISTER);

        add(root);
    }

    public void showLogin() {
        currentUser = null;
        currentAdmin = null;
        cardLayout.show(root, LOGIN);
    }

    public void showRegister() {
        cardLayout.show(root, REGISTER);
    }

    public void loginAsCustomer(User user) {
        this.currentUser = user;
        if (adminHomePanel != null) {
            root.remove(adminHomePanel);
            adminHomePanel = null;
        }
        if (customerHomePanel != null) root.remove(customerHomePanel);
        customerHomePanel = new CustomerHomePanel(this, user);
        root.add(customerHomePanel, CUSTOMER_HOME);
        cardLayout.show(root, CUSTOMER_HOME);
    }

    public void loginAsAdmin(Admin admin) {
        this.currentAdmin = admin;
        if (customerHomePanel != null) {
            root.remove(customerHomePanel);
            customerHomePanel = null;
        }
        if (adminHomePanel != null) root.remove(adminHomePanel);
        adminHomePanel = new AdminHomePanel(this, admin);
        root.add(adminHomePanel, ADMIN_HOME);
        cardLayout.show(root, ADMIN_HOME);
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public Admin getCurrentAdmin() {
        return currentAdmin;
    }
}
