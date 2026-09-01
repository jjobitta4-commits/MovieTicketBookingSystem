package dao;

import db.DBConnection;
import model.Show;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShowDAO {

    private static final String BASE_SELECT =
            "SELECT s.*, m.title AS movie_title, t.name AS theatre_name " +
            "FROM shows s " +
            "JOIN movies m ON s.movie_id = m.id " +
            "JOIN theatres t ON s.theatre_id = t.id ";

    public boolean addShow(Show s) {
        String sql = "INSERT INTO shows (movie_id, theatre_id, show_date, show_time, price, available_seats) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, s.getMovieId());
            ps.setInt(2, s.getTheatreId());
            ps.setDate(3, Date.valueOf(s.getShowDate()));
            ps.setTime(4, Time.valueOf(s.getShowTime()));
            ps.setBigDecimal(5, s.getPrice());
            ps.setInt(6, s.getAvailableSeats());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteShow(int id) {
        String sql = "DELETE FROM shows WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateAvailableSeats(int showId, int newAvailable) {
        String sql = "UPDATE shows SET available_seats = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, newAvailable);
            ps.setInt(2, showId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Show> getAllShows() {
        List<Show> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(BASE_SELECT + "ORDER BY s.show_date, s.show_time");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Show> searchShowsByMovieTitle(String keyword) {
        List<Show> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE m.title LIKE ? ORDER BY s.show_date, s.show_time";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Show getShowById(int id) {
        String sql = BASE_SELECT + "WHERE s.id = ?";
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

    private Show mapRow(ResultSet rs) throws SQLException {
        Show s = new Show();
        s.setId(rs.getInt("id"));
        s.setMovieId(rs.getInt("movie_id"));
        s.setTheatreId(rs.getInt("theatre_id"));
        s.setMovieTitle(rs.getString("movie_title"));
        s.setTheatreName(rs.getString("theatre_name"));
        s.setShowDate(rs.getDate("show_date").toLocalDate());
        s.setShowTime(rs.getTime("show_time").toLocalTime());
        s.setPrice(rs.getBigDecimal("price"));
        s.setAvailableSeats(rs.getInt("available_seats"));
        return s;
    }
}
