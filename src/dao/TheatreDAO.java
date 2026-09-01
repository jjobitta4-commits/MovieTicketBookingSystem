package dao;

import db.DBConnection;
import model.Theatre;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TheatreDAO {

    public boolean addTheatre(Theatre t) {
        String sql = "INSERT INTO theatres (name, location, total_seats) VALUES (?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, t.getName());
            ps.setString(2, t.getLocation());
            ps.setInt(3, t.getTotalSeats());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateTheatre(Theatre t) {
        String sql = "UPDATE theatres SET name = ?, location = ?, total_seats = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, t.getName());
            ps.setString(2, t.getLocation());
            ps.setInt(3, t.getTotalSeats());
            ps.setInt(4, t.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteTheatre(int id) {
        String sql = "DELETE FROM theatres WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Theatre> getAllTheatres() {
        List<Theatre> list = new ArrayList<>();
        String sql = "SELECT * FROM theatres ORDER BY id DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Theatre(rs.getInt("id"), rs.getString("name"),
                        rs.getString("location"), rs.getInt("total_seats")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
