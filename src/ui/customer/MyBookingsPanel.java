package ui.customer;

import dao.BookingDAO;
import model.Booking;
import model.User;
import ui.common.RoundedButton;
import ui.common.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class MyBookingsPanel extends JPanel {

    private final BookingDAO bookingDAO = new BookingDAO();
    private final User currentUser;
    private final JLabel statusLabel = new JLabel(" ");

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Booking ID", "Movie", "Theatre", "Date", "Time", "Seats", "Amount", "Status"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);
    private List<Booking> currentBookings;

    public MyBookingsPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(20, 18));
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        setBackground(UITheme.BG_LIGHT);

        JLabel heading = new JLabel("My Bookings");
        heading.setFont(UITheme.FONT_TITLE);
        heading.setForeground(UITheme.TEXT_DARK);
        add(heading, BorderLayout.NORTH);

        table.setFont(UITheme.FONT_FIELD);
        table.setRowHeight(30);
        table.getTableHeader().setFont(UITheme.FONT_BOLD);
        table.setSelectionBackground(new Color(0xE0, 0xE7, 0xFF));
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(UITheme.BORDER, 1, true));
        add(sp, BorderLayout.CENTER);

        add(buildActionBar(), BorderLayout.SOUTH);

        refresh();
    }

    private JPanel buildActionBar() {
        JPanel bar = UITheme.card();
        bar.setLayout(new FlowLayout(FlowLayout.LEFT, 14, 10));

        RoundedButton cancelBtn = new RoundedButton("Cancel Selected Booking", UITheme.DANGER, new Color(0xB9, 0x1C, 0x1C), Color.WHITE);
        RoundedButton downloadBtn = new RoundedButton("Download Ticket", UITheme.ACCENT, UITheme.ACCENT_DARK, UITheme.TEXT_DARK);

        bar.add(cancelBtn);
        bar.add(downloadBtn);
        statusLabel.setFont(UITheme.FONT_LABEL);
        bar.add(statusLabel);

        cancelBtn.addActionListener(e -> cancelSelected());
        downloadBtn.addActionListener(e -> downloadSelectedTicket());
        return bar;
    }

    private void cancelSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            statusLabel.setForeground(UITheme.DANGER);
            statusLabel.setText("Select a booking to cancel.");
            return;
        }
        Booking b = currentBookings.get(row);
        if ("CANCELLED".equals(b.getStatus())) {
            statusLabel.setForeground(UITheme.DANGER);
            statusLabel.setText("This booking is already cancelled.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Cancel this booking for \"" + b.getMovieTitle() + "\"?",
                "Confirm Cancellation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (bookingDAO.cancelBooking(b.getId())) {
                statusLabel.setForeground(UITheme.SUCCESS);
                statusLabel.setText("Booking cancelled.");
                refresh();
            } else {
                statusLabel.setForeground(UITheme.DANGER);
                statusLabel.setText("Failed to cancel booking.");
            }
        }
    }

    private void downloadSelectedTicket() {
        int row = table.getSelectedRow();
        if (row < 0) {
            statusLabel.setForeground(UITheme.DANGER);
            statusLabel.setText("Select a booking to download.");
            return;
        }
        Booking b = currentBookings.get(row);

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("Ticket_" + b.getId() + ".txt"));
        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) return;

        String ticketText = buildTicketText(b);
        try (FileWriter fw = new FileWriter(chooser.getSelectedFile())) {
            fw.write(ticketText);
            statusLabel.setForeground(UITheme.SUCCESS);
            statusLabel.setText("Ticket saved to " + chooser.getSelectedFile().getName());
        } catch (IOException ex) {
            statusLabel.setForeground(UITheme.DANGER);
            statusLabel.setText("Failed to save ticket file.");
        }
    }

    private String buildTicketText(Booking b) {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("           CINEBOOK  E-TICKET            \n");
        sb.append("========================================\n\n");
        sb.append("Booking ID   : ").append(b.getId()).append("\n");
        sb.append("Customer     : ").append(currentUser.getName()).append("\n");
        sb.append("Movie        : ").append(b.getMovieTitle()).append("\n");
        sb.append("Theatre      : ").append(b.getTheatreName()).append("\n");
        sb.append("Show Date    : ").append(b.getShowDate()).append("\n");
        sb.append("Show Time    : ").append(b.getShowTime()).append("\n");
        sb.append("Seats Booked : ").append(b.getSeatsBooked()).append("\n");
        sb.append("Total Amount : Rs. ").append(b.getTotalAmount()).append("\n");
        sb.append("Status       : ").append(b.getStatus()).append("\n");
        sb.append("Booked On    : ").append(b.getBookingDate()).append("\n\n");
        sb.append("----------------------------------------\n");
        sb.append("Please arrive 15 minutes before showtime.\n");
        sb.append("Present this ticket (printed or on your\n");
        sb.append("phone) at the theatre entrance.\n");
        sb.append("========================================\n");
        return sb.toString();
    }

    public void refresh() {
        tableModel.setRowCount(0);
        currentBookings = bookingDAO.getBookingsByUser(currentUser.getId());
        for (Booking b : currentBookings) {
            tableModel.addRow(new Object[]{
                    b.getId(), b.getMovieTitle(), b.getTheatreName(),
                    b.getShowDate(), b.getShowTime(), b.getSeatsBooked(),
                    "\u20B9" + b.getTotalAmount(), b.getStatus()
            });
        }
    }
}
