package ui.customer;

import model.User;
import ui.AppFrame;
import ui.common.Sidebar;
import ui.common.UITheme;

import javax.swing.*;
import java.awt.*;

public class CustomerHomePanel extends JPanel {

    private static final String SEARCH_BOOK = "SEARCH_BOOK";
    private static final String MY_BOOKINGS = "MY_BOOKINGS";

    private final CardLayout contentLayout = new CardLayout();
    private final JPanel content = new JPanel(contentLayout);
    private MyBookingsPanel myBookingsPanel;

    public CustomerHomePanel(AppFrame app, User user) {
        setLayout(new BorderLayout());

        Sidebar sidebar = new Sidebar("CineBook", "Hi, " + user.getName());
        sidebar.addNavItem(SEARCH_BOOK, "\uD83C\uDFA5  Search & Book", k -> contentLayout.show(content, SEARCH_BOOK));
        sidebar.addNavItem(MY_BOOKINGS, "\uD83C\uDFAB  My Bookings", k -> {
            myBookingsPanel.refresh();
            contentLayout.show(content, MY_BOOKINGS);
        });
        sidebar.addFooterButton("Logout", app::showLogin);
        sidebar.setActive(SEARCH_BOOK);

        content.setBackground(UITheme.BG_LIGHT);
        SearchBookPanel searchBookPanel = new SearchBookPanel(user, () -> {
            myBookingsPanel.refresh();
        });
        myBookingsPanel = new MyBookingsPanel(user);

        content.add(searchBookPanel, SEARCH_BOOK);
        content.add(myBookingsPanel, MY_BOOKINGS);

        add(sidebar, BorderLayout.WEST);
        add(content, BorderLayout.CENTER);
    }
}
