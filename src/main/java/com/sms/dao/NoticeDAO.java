package com.sms.dao;

import com.sms.model.Notice;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data-access operations for the notices table.
 */
public class NoticeDAO {

    private final Connection conn;

    public NoticeDAO() throws SQLException {
        this.conn = DBConnection.getInstance().getConnection();
    }

    public int create(Notice n) throws SQLException {
        String sql = "INSERT INTO notices (title, content, posted_by) VALUES (?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, n.getTitle());
            ps.setString(2, n.getContent());
            ps.setString(3, n.getPostedBy());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    public List<Notice> findAll() throws SQLException {
        String sql = "SELECT id, title, content, posted_by, posted_date, active " +
                     "FROM notices ORDER BY posted_date DESC";
        List<Notice> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<Notice> findActive() throws SQLException {
        String sql = "SELECT id, title, content, posted_by, posted_date, active " +
                     "FROM notices WHERE active=1 ORDER BY posted_date DESC";
        List<Notice> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public boolean update(Notice n) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE notices SET title=?, content=?, active=? WHERE id=?")) {
            ps.setString (1, n.getTitle());
            ps.setString (2, n.getContent());
            ps.setBoolean(3, n.isActive());
            ps.setInt    (4, n.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM notices WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public int countActive() throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT COUNT(*) FROM notices WHERE active=1");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private Notice mapRow(ResultSet rs) throws SQLException {
        Notice n = new Notice();
        n.setId(rs.getInt("id"));
        n.setTitle(rs.getString("title"));
        n.setContent(rs.getString("content"));
        n.setPostedBy(rs.getString("posted_by"));
        Timestamp ts = rs.getTimestamp("posted_date");
        n.setPostedDate(ts != null ? ts.toLocalDateTime() : null);
        n.setActive(rs.getBoolean("active"));
        return n;
    }
}
