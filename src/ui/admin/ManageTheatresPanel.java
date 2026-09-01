package ui.admin;

import dao.TheatreDAO;
import model.Theatre;
import ui.common.RoundedButton;
import ui.common.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageTheatresPanel extends JPanel {

    private final TheatreDAO theatreDAO = new TheatreDAO();

    private final JTextField nameField = UITheme.styledTextField();
    private final JTextField locationField = UITheme.styledTextField();
    private final JTextField seatsField = UITheme.styledTextField();
    private final JLabel statusLabel = new JLabel(" ");

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"ID", "Name", "Location", "Total Seats"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);

    public ManageTheatresPanel() {
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        setBackground(UITheme.BG_LIGHT);

        JLabel heading = new JLabel("Manage Theatres");
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
        form.add(UITheme.label("Theatre Name"), c);
        c.gridx = 1;
        form.add(UITheme.label("Location"), c);
        c.gridx = 2;
        form.add(UITheme.label("Total Seats"), c);

        c.gridy = 1;
        c.gridx = 0;
        form.add(nameField, c);
        c.gridx = 1;
        form.add(locationField, c);
        c.gridx = 2;
        seatsField.setColumns(6);
        form.add(seatsField, c);

        RoundedButton addBtn = new RoundedButton("+ Add Theatre");
        c.gridx = 3;
        form.add(addBtn, c);

        RoundedButton deleteBtn = new RoundedButton("Delete Selected", UITheme.DANGER, new Color(0xB9, 0x1C, 0x1C), Color.WHITE);
        c.gridy = 2;
        c.gridx = 3;
        c.insets = new Insets(10, 8, 6, 8);
        form.add(deleteBtn, c);

        statusLabel.setFont(UITheme.FONT_LABEL);
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 3;
        form.add(statusLabel, c);

        addBtn.addActionListener(e -> addTheatre());
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

    private void addTheatre() {
        String name = nameField.getText().trim();
        String location = locationField.getText().trim();
        String seatsTxt = seatsField.getText().trim();

        if (name.isEmpty() || seatsTxt.isEmpty()) {
            statusLabel.setForeground(UITheme.DANGER);
            statusLabel.setText("Name and total seats are required.");
            return;
        }
        int seats;
        try {
            seats = Integer.parseInt(seatsTxt);
        } catch (NumberFormatException ex) {
            statusLabel.setForeground(UITheme.DANGER);
            statusLabel.setText("Total seats must be a number.");
            return;
        }

        Theatre t = new Theatre();
        t.setName(name);
        t.setLocation(location);
        t.setTotalSeats(seats);

        if (theatreDAO.addTheatre(t)) {
            statusLabel.setForeground(UITheme.SUCCESS);
            statusLabel.setText("Theatre added successfully.");
            nameField.setText("");
            locationField.setText("");
            seatsField.setText("");
            refresh();
        } else {
            statusLabel.setForeground(UITheme.DANGER);
            statusLabel.setText("Failed to add theatre.");
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            statusLabel.setForeground(UITheme.DANGER);
            statusLabel.setText("Select a theatre in the table first.");
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete this theatre? This may also remove related shows.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (theatreDAO.deleteTheatre(id)) {
                refresh();
            } else {
                statusLabel.setForeground(UITheme.DANGER);
                statusLabel.setText("Failed to delete theatre.");
            }
        }
    }

    public void refresh() {
        tableModel.setRowCount(0);
        List<Theatre> theatres = theatreDAO.getAllTheatres();
        for (Theatre t : theatres) {
            tableModel.addRow(new Object[]{t.getId(), t.getName(), t.getLocation(), t.getTotalSeats()});
        }
    }
}
