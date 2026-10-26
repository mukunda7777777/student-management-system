package com.sms.dao;

import com.sms.model.Fee;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data-access operations for the fees table.
 */
public class FeeDAO {

    private final Connection conn;

    public FeeDAO() throws SQLException {
        this.conn = DBConnection.getInstance().getConnection();
    }

    public int create(Fee f) throws SQLException {
        String sql = "INSERT INTO fees (student_id, receipt_no, amount_paid, due_amount, " +
                     "paid_date, fee_type, remarks) VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt   (1, f.getStudentId());
            ps.setString(2, f.getReceiptNo());
            ps.setDouble(3, f.getAmountPaid());
            ps.setDouble(4, f.getDueAmount());
            ps.setDate  (5, f.getPaidDate() != null ? Date.valueOf(f.getPaidDate()) : null);
            ps.setString(6, f.getFeeType());
            ps.setString(7, f.getRemarks());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    public List<Fee> findByStudent(int studentId) throws SQLException {
        String sql = "SELECT f.*, s.first_name, s.last_name, s.roll_no, c.name AS course_name " +
                     "FROM fees f " +
                     "JOIN students s ON f.student_id = s.id " +
                     "LEFT JOIN courses c ON s.course_id = c.id " +
                     "WHERE f.student_id=? ORDER BY f.paid_date DESC";
        List<Fee> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public List<Fee> findAll() throws SQLException {
        String sql = "SELECT f.*, s.first_name, s.last_name, s.roll_no, c.name AS course_name " +
                     "FROM fees f " +
                     "JOIN students s ON f.student_id = s.id " +
                     "LEFT JOIN courses c ON s.course_id = c.id " +
                     "ORDER BY f.paid_date DESC";
        List<Fee> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    /**
     * Returns fee records where due_amount > 0 (unpaid / partially paid).
     */
    public List<Fee> findDueStudents() throws SQLException {
        String sql = "SELECT f.*, s.first_name, s.last_name, s.roll_no, c.name AS course_name " +
                     "FROM fees f " +
                     "JOIN students s ON f.student_id = s.id " +
                     "LEFT JOIN courses c ON s.course_id = c.id " +
                     "WHERE f.due_amount > 0 ORDER BY f.due_amount DESC";
        List<Fee> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public boolean update(Fee f) throws SQLException {
        String sql = "UPDATE fees SET amount_paid=?, due_amount=?, paid_date=?, " +
                     "fee_type=?, remarks=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, f.getAmountPaid());
            ps.setDouble(2, f.getDueAmount());
            ps.setDate  (3, f.getPaidDate() != null ? Date.valueOf(f.getPaidDate()) : null);
            ps.setString(4, f.getFeeType());
            ps.setString(5, f.getRemarks());
            ps.setInt   (6, f.getId());
            return ps.executeUpdate() > 0;
        }
    }

    /** Count of students with pending dues */
    public int countDueStudents() throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT COUNT(DISTINCT student_id) FROM fees WHERE due_amount > 0");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private Fee mapRow(ResultSet rs) throws SQLException {
        Fee f = new Fee();
        f.setId(rs.getInt("id"));
        f.setStudentId(rs.getInt("student_id"));
        f.setStudentName(rs.getString("first_name") + " " + rs.getString("last_name"));
        f.setRollNo(rs.getString("roll_no"));
        f.setCourseName(rs.getString("course_name"));
        f.setReceiptNo(rs.getString("receipt_no"));
        f.setAmountPaid(rs.getDouble("amount_paid"));
        f.setDueAmount(rs.getDouble("due_amount"));
        Date d = rs.getDate("paid_date");
        f.setPaidDate(d != null ? d.toLocalDate() : null);
        f.setFeeType(rs.getString("fee_type"));
        f.setRemarks(rs.getString("remarks"));
        return f;
    }
}
