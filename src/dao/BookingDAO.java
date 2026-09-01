package dao;

import db.DBConnection;
import model.Booking;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    private static final String BASE_SELECT =
            "SELECT b.*, u.name AS user_name, m.title AS movie_title, t.name AS theatre_name, " +
            "sh.show_date, sh.show_time " +
            "FROM bookings b " +
            "JOIN users u ON b.user_id = u.id " +
            "JOIN shows sh ON b.show_id = sh.id " +
            "JOIN movies m ON sh.movie_id = m.id " +
            "JOIN theatres t ON sh.theatre_id = t.id ";

    /**
     * Books tickets transactionally: inserts a booking row and decrements
     * available_seats on the show, guarding against overbooking.
     */
    public Booking bookTicket(int userId, int showId, int seats, BigDecimal pricePerSeat) {
        String checkSql = "SELECT available_seats FROM shows WHERE id = ? FOR UPDATE";
        String updateSql = "UPDATE shows SET available_seats = available_seats - ? WHERE id = ?";
        String insertSql = "INSERT INTO bookings (user_id, show_id, seats_booked, total_amount, status) " +
                "VALUES (?, ?, ?, ?, 'CONFIRMED')";

        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            int available;
            try (PreparedStatement ps = con.prepareStatement(checkSql)) {
                ps.setInt(1, showId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        con.rollback();
                        return null;
                    }
                    available = rs.getInt("available_seats");
                }
            }

            if (available < seats) {
                con.rollback();
                return null; // not enough seats
            }

            try (PreparedStatement ps = con.prepareStatement(updateSql)) {
                ps.setInt(1, seats);
                ps.setInt(2, showId);
                ps.executeUpdate();
            }

            BigDecimal total = pricePerSeat.multiply(BigDecimal.valueOf(seats));
            int newBookingId;
            try (PreparedStatement ps = con.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, userId);
                ps.setInt(2, showId);
                ps.setInt(3, seats);
                ps.setBigDecimal(4, total);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    newBookingId = keys.getInt(1);
                }
            }

            con.commit();
            return getBookingById(newBookingId);

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (con != null) con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return null;
        } finally {
            try {
                if (con != null) con.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean cancelBooking(int bookingId) {
        String selectSql = "SELECT show_id, seats_booked, status FROM bookings WHERE id = ?";
        String updateBookingSql = "UPDATE bookings SET status = 'CANCELLED' WHERE id = ?";
        String updateShowSql = "UPDATE shows SET available_seats = available_seats + ? WHERE id = ?";

        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            int showId;
            int seats;
            try (PreparedStatement ps = con.prepareStatement(selectSql)) {
                ps.setInt(1, bookingId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        con.rollback();
                        return false;
                    }
                    if ("CANCELLED".equals(rs.getString("status"))) {
                        con.rollback();
                        return false;
                    }
                    showId = rs.getInt("show_id");
                    seats = rs.getInt("seats_booked");
                }
            }

            try (PreparedStatement ps = con.prepareStatement(updateBookingSql)) {
                ps.setInt(1, bookingId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = con.prepareStatement(updateShowSql)) {
                ps.setInt(1, seats);
                ps.setInt(2, showId);
                ps.executeUpdate();
            }

            con.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (con != null) con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (con != null) con.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Booking> getBookingsByUser(int userId) {
        List<Booking> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE b.user_id = ? ORDER BY b.booking_date DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Booking> getAllBookings() {
        List<Booking> list = new ArrayList<>();
        String sql = BASE_SELECT + "ORDER BY b.booking_date DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Booking getBookingById(int id) {
        String sql = BASE_SELECT + "WHERE b.id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Booking mapRow(ResultSet rs) throws SQLException {
        Booking b = new Booking();
        b.setId(rs.getInt("id"));
        b.setUserId(rs.getInt("user_id"));
        b.setShowId(rs.getInt("show_id"));
        b.setSeatsBooked(rs.getInt("seats_booked"));
        b.setTotalAmount(rs.getBigDecimal("total_amount"));
        Timestamp ts = rs.getTimestamp("booking_date");
        if (ts != null) b.setBookingDate(ts.toLocalDateTime());
        b.setStatus(rs.getString("status"));
        b.setUserName(rs.getString("user_name"));
        b.setMovieTitle(rs.getString("movie_title"));
        b.setTheatreName(rs.getString("theatre_name"));
        b.setShowDate(rs.getDate("show_date").toLocalDate());
        b.setShowTime(rs.getTime("show_time").toLocalTime());
        return b;
    }
}
