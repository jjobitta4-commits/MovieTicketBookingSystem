package ui.admin;

import dao.MovieDAO;
import model.Movie;
import ui.common.RoundedButton;
import ui.common.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AddMoviePanel extends JPanel {

    private final MovieDAO movieDAO = new MovieDAO();

    private final JTextField titleField = UITheme.styledTextField();
    private final JTextField genreField = UITheme.styledTextField();
    private final JTextField languageField = UITheme.styledTextField();
    private final JTextField durationField = UITheme.styledTextField();
    private final JTextField releaseDateField = UITheme.styledTextField();
    private final JTextField ratingField = UITheme.styledTextField();
    private final JTextArea descriptionArea = new JTextArea(3, 20);
    private final JLabel statusLabel = new JLabel(" ");

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"ID", "Title", "Genre", "Language", "Duration", "Release Date", "Rating"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);

    public AddMoviePanel() {
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        setBackground(UITheme.BG_LIGHT);

        JLabel heading = new JLabel("Add Movies");
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

        addLabeledField(form, c, 0, 0, "Title", titleField, 2);
        addLabeledField(form, c, 0, 2, "Genre", genreField, 1);
        addLabeledField(form, c, 2, 2, "Language", languageField, 1);

        addLabeledField(form, c, 0, 4, "Duration (min)", durationField, 1);
        addLabeledField(form, c, 2, 4, "Release Date (yyyy-mm-dd)", releaseDateField, 1);
        addLabeledField(form, c, 0, 6, "Rating (e.g. UA)", ratingField, 1);

        c.gridx = 0;
        c.gridy = 8;
        c.gridwidth = 4;
        form.add(UITheme.label("Description"), c);
        c.gridy = 9;
        descriptionArea.setFont(UITheme.FONT_FIELD);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        form.add(new JScrollPane(descriptionArea), c);

        RoundedButton addBtn = new RoundedButton("+ Add Movie");
        c.gridy = 10;
        c.gridwidth = 2;
        c.insets = new Insets(16, 8, 4, 8);
        form.add(addBtn, c);

        statusLabel.setFont(UITheme.FONT_LABEL);
        c.gridx = 2;
        c.gridwidth = 2;
        form.add(statusLabel, c);

        addBtn.addActionListener(e -> addMovie());
        return form;
    }

    private void addLabeledField(JPanel form, GridBagConstraints c, int gx, int gy, String label, JTextField field, int width) {
        c.gridx = gx;
        c.gridy = gy;
        c.gridwidth = width;
        form.add(UITheme.label(label), c);
        c.gridy = gy + 1;
        form.add(field, c);
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

    private void addMovie() {
        String title = titleField.getText().trim();
        String genre = genreField.getText().trim();
        String language = languageField.getText().trim();
        String durationTxt = durationField.getText().trim();
        String releaseTxt = releaseDateField.getText().trim();
        String rating = ratingField.getText().trim();
        String description = descriptionArea.getText().trim();

        if (title.isEmpty() || durationTxt.isEmpty()) {
            statusLabel.setForeground(UITheme.DANGER);
            statusLabel.setText("Title and duration are required.");
            return;
        }

        int duration;
        try {
            duration = Integer.parseInt(durationTxt);
        } catch (NumberFormatException ex) {
            statusLabel.setForeground(UITheme.DANGER);
            statusLabel.setText("Duration must be a number.");
            return;
        }

        LocalDate releaseDate = null;
        if (!releaseTxt.isEmpty()) {
            try {
                releaseDate = LocalDate.parse(releaseTxt, DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (Exception ex) {
                statusLabel.setForeground(UITheme.DANGER);
                statusLabel.setText("Release date must be in yyyy-mm-dd format.");
                return;
            }
        }

        Movie movie = new Movie();
        movie.setTitle(title);
        movie.setGenre(genre);
        movie.setLanguage(language);
        movie.setDurationMinutes(duration);
        movie.setDescription(description);
        movie.setReleaseDate(releaseDate);
        movie.setRating(rating);

        if (movieDAO.addMovie(movie)) {
            statusLabel.setForeground(UITheme.SUCCESS);
            statusLabel.setText("Movie added successfully.");
            clearForm();
            refresh();
        } else {
            statusLabel.setForeground(UITheme.DANGER);
            statusLabel.setText("Failed to add movie. Check the database connection.");
        }
    }

    private void clearForm() {
        titleField.setText("");
        genreField.setText("");
        languageField.setText("");
        durationField.setText("");
        releaseDateField.setText("");
        ratingField.setText("");
        descriptionArea.setText("");
    }

    public void refresh() {
        tableModel.setRowCount(0);
        List<Movie> movies = movieDAO.getAllMovies();
        for (Movie m : movies) {
            tableModel.addRow(new Object[]{
                    m.getId(), m.getTitle(), m.getGenre(), m.getLanguage(),
                    m.getDurationMinutes() + " min",
                    m.getReleaseDate() != null ? m.getReleaseDate().toString() : "-",
                    m.getRating()
            });
        }
    }
}
