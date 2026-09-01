package ui.admin;

import dao.BookingDAO;
import model.Booking;
import ui.common.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ViewBookingsPanel extends JPanel {

    private final BookingDAO bookingDAO = new BookingDAO();

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Booking ID", "Customer", "Movie", "Theatre", "Date", "Time", "Seats", "Amount", "Status"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);

    public ViewBookingsPanel() {
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        setBackground(UITheme.BG_LIGHT);

        JLabel heading = new JLabel("All Bookings");
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

        refresh();
    }

    public void refresh() {
        tableModel.setRowCount(0);
        List<Booking> bookings = bookingDAO.getAllBookings();
        for (Booking b : bookings) {
            tableModel.addRow(new Object[]{
                    b.getId(), b.getUserName(), b.getMovieTitle(), b.getTheatreName(),
                    b.getShowDate(), b.getShowTime(), b.getSeatsBooked(),
                    "\u20B9" + b.getTotalAmount(), b.getStatus()
            });
        }
    }
}
