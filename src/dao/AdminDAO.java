package dao;

import db.DBConnection;
import model.Admin;
import util.PasswordUtil;

import java.sql.*;

public class AdminDAO {

    public Admin authenticate(String username, String password) {
        String sql = "SELECT * FROM admin WHERE username = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password");
                    if (PasswordUtil.verify(password, storedHash)) {
                        return new Admin(rs.getInt("id"), rs.getString("username"), storedHash);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
