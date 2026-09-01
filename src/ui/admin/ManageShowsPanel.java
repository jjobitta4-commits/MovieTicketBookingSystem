package ui.admin;

import dao.MovieDAO;
import dao.ShowDAO;
import dao.TheatreDAO;
import model.Movie;
import model.Show;
import model.Theatre;
import ui.common.RoundedButton;
import ui.common.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ManageShowsPanel extends JPanel {

    private final ShowDAO showDAO = new ShowDAO();
    private final MovieDAO movieDAO = new MovieDAO();
    private final TheatreDAO theatreDAO = new TheatreDAO();

    private final JComboBox<Movie> movieCombo = new JComboBox<>();
    private final JComboBox<Theatre> theatreCombo = new JComboBox<>();
    private final JTextField dateField = UITheme.styledTextField();
    private final JTextField timeField = UITheme.styledTextField();
    private final JTextField priceField = UITheme.styledTextField();
    private final JLabel statusLabel = new JLabel(" ");

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"ID", "Movie", "Theatre", "Date", "Time", "Price", "Available Seats"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);

    public ManageShowsPanel() {
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        setBackground(UITheme.BG_LIGHT);

        JLabel heading = new JLabel("Manage Shows");
        heading.setFont(UITheme.FONT_TITLE);
        heading.setForeground(UITheme.TEXT_DARK);
        add(heading, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 18));
        center.setOpaque(false);
        center.add(buildForm(), BorderLayout.NORTH);
        center.add(buildTable(), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        refresh();
    }

    private JPanel buildForm() {
        JPanel form = UITheme.card();
        form.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 8, 6, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0;
        c.gridy = 0;
        form.add(UITheme.label("Movie"), c);
        c.gridx = 1;
        form.add(UITheme.label("Theatre"), c);
        c.gridx = 2;
        form.add(UITheme.label("Date (yyyy-mm-dd)"), c);
        c.gridx = 3;
        form.add(UITheme.label("Time (HH:mm)"), c);

        c.gridy = 1;
        c.gridx = 0;
        form.add(movieCombo, c);
        c.gridx = 1;
        form.add(theatreCombo, c);
        c.gridx = 2;
        dateField.setText(LocalDate.now().toString());
        form.add(dateField, c);
        c.gridx = 3;
        timeField.setText("18:00");
        form.add(timeField, c);

        c.gridy = 2;
        c.gridx = 0;
        form.add(UITheme.label("Ticket Price"), c);
        c.gridy = 3;
        form.add(priceField, c);

        RoundedButton addBtn = new RoundedButton("+ Schedule Show");
        c.gridy = 3;
        c.gridx = 3;
        form.add(addBtn, c);

        RoundedButton deleteBtn = new RoundedButton("Delete Selected", UITheme.DANGER, new Color(0xB9, 0x1C, 0x1C), Color.WHITE);
        c.gridy = 4;
        c.gridx = 3;
        c.insets = new Insets(10, 8, 4, 8);
        form.add(deleteBtn, c);

        statusLabel.setFont(UITheme.FONT_LABEL);
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 3;
        form.add(statusLabel, c);

        addBtn.addActionListener(e -> addShow());
        deleteBtn.addActionListener(e -> deleteSelected());
        return form;
    }

    private JScrollPane buildTable() {
        table.setFont(UITheme.FONT_FIELD);
        table.setRowHeight(30);
        table.getTableHeader().setFont(UITheme.FONT_BOLD);
        table.setSelectionBackground(new Color(0xE0, 0xE7, 0xFF));
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(UITheme.BORDER, 1, true));
        return sp;
    }

    private void addShow() {
        Movie movie = (Movie) movieCombo.getSelectedItem();
        Theatre theatre = (Theatre) theatreCombo.getSelectedItem();
        String dateTxt = dateField.getText().trim();
        String timeTxt = timeField.getText().trim();
        String priceTxt = priceField.getText().trim();

        if (movie == null || theatre == null) {
            statusLabel.setForeground(UITheme.DANGER);
            statusLabel.setText("Add a movie and theatre first.");
            return;
        }

        LocalDate date;
        LocalTime time;
        BigDecimal price;
        try {
            date = LocalDate.parse(dateTxt, DateTimeFormatter.ISO_LOCAL_DATE);
            time = LocalTime.parse(timeTxt, DateTimeFormatter.ofPattern("HH:mm"));
            price = new BigDecimal(priceTxt);
        } catch (Exception ex) {
            statusLabel.setForeground(UITheme.DANGER);
            statusLabel.setText("Check date (yyyy-mm-dd), time (HH:mm) and price formats.");
            return;
        }

        Show s = new Show();
        s.setMovieId(movie.getId());
        s.setTheatreId(theatre.getId());
        s.setShowDate(date);
        s.setShowTime(time);
        s.setPrice(price);
        s.setAvailableSeats(theatre.getTotalSeats());

        if (showDAO.addShow(s)) {
            statusLabel.setForeground(UITheme.SUCCESS);
            statusLabel.setText("Show scheduled successfully.");
            priceField.setText("");
            refresh();
        } else {
            statusLabel.setForeground(UITheme.DANGER);
            statusLabel.setText("Failed to schedule show.");
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            statusLabel.setForeground(UITheme.DANGER);
            statusLabel.setText("Select a show in the table first.");
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete this show?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (showDAO.deleteShow(id)) {
                refresh();
            } else {
                statusLabel.setForeground(UITheme.DANGER);
                statusLabel.setText("Failed to delete show.");
            }
        }
    }

    public void refresh() {
        movieCombo.removeAllItems();
        for (Movie m : movieDAO.getAllMovies()) movieCombo.addItem(m);

        theatreCombo.removeAllItems();
        for (Theatre t : theatreDAO.getAllTheatres()) theatreCombo.addItem(t);

        tableModel.setRowCount(0);
        List<Show> shows = showDAO.getAllShows();
        for (Show s : shows) {
            tableModel.addRow(new Object[]{
                    s.getId(), s.getMovieTitle(), s.getTheatreName(),
                    s.getShowDate(), s.getShowTime(), "\u20B9" + s.getPrice(), s.getAvailableSeats()
            });
        }
    }
}
