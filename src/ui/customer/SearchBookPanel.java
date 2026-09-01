package ui.customer;

import dao.BookingDAO;
import dao.ShowDAO;
import model.Booking;
import model.Show;
import model.User;
import ui.common.RoundedButton;
import ui.common.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SearchBookPanel extends JPanel {

    private final ShowDAO showDAO = new ShowDAO();
    private final BookingDAO bookingDAO = new BookingDAO();
    private final User currentUser;
    private final Runnable onBookingComplete;

    private final JTextField searchField = UITheme.styledTextField();
    private final JSpinner seatsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
    private final JLabel selectionLabel = UITheme.label("No show selected");
    private final JLabel statusLabel = new JLabel(" ");

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"ID", "Movie", "Theatre", "Date", "Time", "Price", "Available Seats"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);
    private List<Show> currentShows;

    public SearchBookPanel(User user, Runnable onBookingComplete) {
        this.currentUser = user;
        this.onBookingComplete = onBookingComplete;

        setLayout(new BorderLayout(20, 18));
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        setBackground(UITheme.BG_LIGHT);

        JLabel heading = new JLabel("Search Movies & Book Tickets");
        heading.setFont(UITheme.FONT_TITLE);
        heading.setForeground(UITheme.TEXT_DARK);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(heading, BorderLayout.NORTH);
        top.add(buildSearchBar(), BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);

        table.setFont(UITheme.FONT_FIELD);
        table.setRowHeight(30);
        table.getTableHeader().setFont(UITheme.FONT_BOLD);
        table.setSelectionBackground(new Color(0xE0, 0xE7, 0xFF));
        table.getSelectionModel().addListSelectionListener(e -> updateSelection());
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(UITheme.BORDER, 1, true));
        add(sp, BorderLayout.CENTER);

        add(buildBookingBar(), BorderLayout.SOUTH);

        loadShows(null);
    }

    private JPanel buildSearchBar() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(14, 0, 14, 0));
        panel.add(searchField, BorderLayout.CENTER);
        RoundedButton searchBtn = new RoundedButton("Search");
        panel.add(searchBtn, BorderLayout.EAST);
        searchBtn.addActionListener(e -> loadShows(searchField.getText().trim()));
        searchField.addActionListener(e -> loadShows(searchField.getText().trim()));
        return panel;
    }

    private JPanel buildBookingBar() {
        JPanel bar = UITheme.card();
        bar.setLayout(new FlowLayout(FlowLayout.LEFT, 14, 10));

        bar.add(selectionLabel);
        bar.add(UITheme.label("Seats:"));
        seatsSpinner.setPreferredSize(new Dimension(60, 32));
        bar.add(seatsSpinner);

        RoundedButton bookBtn = new RoundedButton("Book Tickets", UITheme.SUCCESS, new Color(0x0F, 0x7A, 0x35), Color.WHITE);
        bar.add(bookBtn);
        bookBtn.addActionListener(e -> bookSelected());

        statusLabel.setFont(UITheme.FONT_LABEL);
        bar.add(statusLabel);
        return bar;
    }

    private void loadShows(String keyword) {
        currentShows = (keyword == null || keyword.isEmpty())
                ? showDAO.getAllShows()
                : showDAO.searchShowsByMovieTitle(keyword);

        tableModel.setRowCount(0);
        for (Show s : currentShows) {
            tableModel.addRow(new Object[]{
                    s.getId(), s.getMovieTitle(), s.getTheatreName(),
                    s.getShowDate(), s.getShowTime(), "\u20B9" + s.getPrice(), s.getAvailableSeats()
            });
        }
        selectionLabel.setText("No show selected");
    }

    private void updateSelection() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            Show s = currentShows.get(row);
            selectionLabel.setText("<html><b>" + s.getMovieTitle() + "</b> @ " + s.getTheatreName()
                    + " on " + s.getShowDate() + " " + s.getShowTime() + "</html>");
            int maxSeats = Math.max(1, s.getAvailableSeats());
            seatsSpinner.setModel(new SpinnerNumberModel(1, 1, maxSeats, 1));
        }
    }

    private void bookSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            statusLabel.setForeground(UITheme.DANGER);
            statusLabel.setText("Select a show from the table first.");
            return;
        }
        Show show = currentShows.get(row);
        int seats = (int) seatsSpinner.getValue();

        if (seats > show.getAvailableSeats()) {
            statusLabel.setForeground(UITheme.DANGER);
            statusLabel.setText("Only " + show.getAvailableSeats() + " seat(s) left.");
            return;
        }

        Booking booking = bookingDAO.bookTicket(currentUser.getId(), show.getId(), seats, show.getPrice());
        if (booking != null) {
            statusLabel.setForeground(UITheme.SUCCESS);
            statusLabel.setText("Booked " + seats + " seat(s)! Total: \u20B9" + booking.getTotalAmount());
            loadShows(searchField.getText().trim());
            if (onBookingComplete != null) onBookingComplete.run();
            JOptionPane.showMessageDialog(this,
                    "Booking confirmed for " + show.getMovieTitle() + "!\nSeats: " + seats
                            + "\nTotal: \u20B9" + booking.getTotalAmount()
                            + "\n\nView and download your ticket from 'My Bookings'.",
                    "Booking Confirmed", JOptionPane.INFORMATION_MESSAGE);
        } else {
            statusLabel.setForeground(UITheme.DANGER);
            statusLabel.setText("Booking failed - seats may no longer be available.");
            loadShows(searchField.getText().trim());
        }
    }
}
