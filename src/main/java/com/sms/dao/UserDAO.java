package com.sms.dao;

import com.sms.model.User;

import java.sql.*;

/**
 * Data-access operations for the users table.
 */
public class UserDAO {

    private final Connection conn;

    public UserDAO() throws SQLException {
        this.conn = DBConnection.getInstance().getConnection();
    }

    /**
     * Finds a user by username. Returns null if not found.
     */
    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT id, username, password_hash, role, student_id, active " +
                     "FROM users WHERE username = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    /**
     * Finds a user by their linked student ID.
     */
    public User findByStudentId(int studentId) throws SQLException {
        String sql = "SELECT id, username, password_hash, role, student_id, active " +
                     "FROM users WHERE student_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    /**
     * Creates a new user account.
     */
    public boolean create(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password_hash, role, student_id) VALUES (?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getRole());
            if (user.getStudentId() != null) ps.setInt(4, user.getStudentId());
            else ps.setNull(4, Types.INTEGER);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Updates password hash for a user.
     */
    public boolean updatePassword(int userId, String newHash) throws SQLException {
        String sql = "UPDATE users SET password_hash = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newHash);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Deletes the user account for a given student.
     */
    public boolean deleteByStudentId(int studentId) throws SQLException {
        String sql = "DELETE FROM users WHERE student_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            return ps.executeUpdate() > 0;
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setRole(rs.getString("role"));
        int sid = rs.getInt("student_id");
        u.setStudentId(rs.wasNull() ? null : sid);
        u.setActive(rs.getBoolean("active"));
        return u;
    }
}
