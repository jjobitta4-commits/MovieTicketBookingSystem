package dao;

import db.DBConnection;
import model.Movie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovieDAO {

    public boolean addMovie(Movie m) {
        String sql = "INSERT INTO movies (title, genre, language, duration_minutes, description, release_date, rating) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, m.getTitle());
            ps.setString(2, m.getGenre());
            ps.setString(3, m.getLanguage());
            ps.setInt(4, m.getDurationMinutes());
            ps.setString(5, m.getDescription());
            ps.setDate(6, m.getReleaseDate() != null ? Date.valueOf(m.getReleaseDate()) : null);
            ps.setString(7, m.getRating());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteMovie(int id) {
        String sql = "DELETE FROM movies WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Movie> getAllMovies() {
        List<Movie> list = new ArrayList<>();
        String sql = "SELECT * FROM movies ORDER BY id DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Movie> searchMovies(String keyword) {
        List<Movie> list = new ArrayList<>();
        String sql = "SELECT * FROM movies WHERE title LIKE ? OR genre LIKE ? OR language LIKE ? ORDER BY id DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            String like = "%" + keyword + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Movie mapRow(ResultSet rs) throws SQLException {
        Movie m = new Movie();
        m.setId(rs.getInt("id"));
        m.setTitle(rs.getString("title"));
        m.setGenre(rs.getString("genre"));
        m.setLanguage(rs.getString("language"));
        m.setDurationMinutes(rs.getInt("duration_minutes"));
        m.setDescription(rs.getString("description"));
        Date d = rs.getDate("release_date");
        if (d != null) m.setReleaseDate(d.toLocalDate());
        m.setRating(rs.getString("rating"));
        return m;
    }
}
