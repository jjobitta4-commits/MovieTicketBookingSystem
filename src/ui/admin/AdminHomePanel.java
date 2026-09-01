package ui.admin;

import model.Admin;
import ui.AppFrame;
import ui.common.Sidebar;
import ui.common.UITheme;

import javax.swing.*;
import java.awt.*;

public class AdminHomePanel extends JPanel {

    private static final String ADD_MOVIES = "ADD_MOVIES";
    private static final String THEATRES = "THEATRES";
    private static final String SHOWS = "SHOWS";
    private static final String BOOKINGS = "BOOKINGS";

    private final CardLayout contentLayout = new CardLayout();
    private final JPanel content = new JPanel(contentLayout);

    private ManageShowsPanel manageShowsPanel;
    private ViewBookingsPanel viewBookingsPanel;

    public AdminHomePanel(AppFrame app, Admin admin) {
        setLayout(new BorderLayout());

        Sidebar sidebar = new Sidebar("CineBook", "Admin Panel  \u00B7  " + admin.getUsername());
        sidebar.addNavItem(ADD_MOVIES, "\uD83C\uDFAC  Add Movies", k -> contentLayout.show(content, ADD_MOVIES));
        sidebar.addNavItem(THEATRES, "\uD83C\uDFDB  Manage Theatres", k -> contentLayout.show(content, THEATRES));
        sidebar.addNavItem(SHOWS, "\uD83D\uDCFD  Manage Shows", k -> {
            manageShowsPanel.refresh();
            contentLayout.show(content, SHOWS);
        });
        sidebar.addNavItem(BOOKINGS, "\uD83C\uDFAB  View Bookings", k -> {
            viewBookingsPanel.refresh();
            contentLayout.show(content, BOOKINGS);
        });
        sidebar.addFooterButton("Logout", app::showLogin);
        sidebar.setActive(ADD_MOVIES);

        content.setBackground(UITheme.BG_LIGHT);
        AddMoviePanel addMoviePanel = new AddMoviePanel();
        ManageTheatresPanel manageTheatresPanel = new ManageTheatresPanel();
        manageShowsPanel = new ManageShowsPanel();
        viewBookingsPanel = new ViewBookingsPanel();

        content.add(addMoviePanel, ADD_MOVIES);
        content.add(manageTheatresPanel, THEATRES);
        content.add(manageShowsPanel, SHOWS);
        content.add(viewBookingsPanel, BOOKINGS);

        add(sidebar, BorderLayout.WEST);
        add(content, BorderLayout.CENTER);
    }
}
