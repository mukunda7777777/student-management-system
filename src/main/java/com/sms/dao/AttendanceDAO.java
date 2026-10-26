package com.sms.dao;

import com.sms.model.Attendance;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data-access operations for the attendance table.
 */
public class AttendanceDAO {

    private final Connection conn;

    public AttendanceDAO() throws SQLException {
        this.conn = DBConnection.getInstance().getConnection();
    }

    /**
     * Inserts or updates an attendance record (UPSERT based on unique constraint).
     */
    public boolean saveOrUpdate(Attendance a) throws SQLException {
        String sql = "INSERT INTO attendance (student_id, subject_id, att_date, status) " +
                     "VALUES (?,?,?,?) " +
                     "ON DUPLICATE KEY UPDATE status = VALUES(status)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt   (1, a.getStudentId());
            ps.setInt   (2, a.getSubjectId());
            ps.setDate  (3, Date.valueOf(a.getAttDate()));
            ps.setString(4, a.getStatus());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Bulk save attendance records for a date and subject.
     */
    public void saveBulk(List<Attendance> records) throws SQLException {
        String sql = "INSERT INTO attendance (student_id, subject_id, att_date, status) " +
                     "VALUES (?,?,?,?) " +
                     "ON DUPLICATE KEY UPDATE status = VALUES(status)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Attendance a : records) {
                ps.setInt   (1, a.getStudentId());
                ps.setInt   (2, a.getSubjectId());
                ps.setDate  (3, Date.valueOf(a.getAttDate()));
                ps.setString(4, a.getStatus());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    /**
     * Returns all attendance for a student in a given subject.
     */
    public List<Attendance> findByStudentAndSubject(int studentId, int subjectId) throws SQLException {
        String sql = "SELECT a.id, a.student_id, a.subject_id, a.att_date, a.status, " +
                     "sub.name AS subject_name " +
                     "FROM attendance a JOIN subjects sub ON a.subject_id = sub.id " +
                     "WHERE a.student_id=? AND a.subject_id=? ORDER BY a.att_date";
        List<Attendance> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, subjectId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    /**
     * Returns all attendance records for a specific date and subject.
     */
    public List<Attendance> findByDateAndSubject(LocalDate date, int subjectId) throws SQLException {
        String sql = "SELECT a.id, a.student_id, a.subject_id, a.att_date, a.status, " +
                     "s.first_name, s.last_name, s.roll_no, sub.name AS subject_name " +
                     "FROM attendance a " +
                     "JOIN students s ON a.student_id = s.id " +
                     "JOIN subjects sub ON a.subject_id = sub.id " +
                     "WHERE a.att_date=? AND a.subject_id=? " +
                     "ORDER BY s.roll_no";
        List<Attendance> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            ps.setInt (2, subjectId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Attendance att = mapRow(rs);
                    att.setStudentName(rs.getString("first_name") + " " + rs.getString("last_name"));
                    att.setRollNo(rs.getString("roll_no"));
                    list.add(att);
                }
            }
        }
        return list;
    }

    /**
     * Calculates attendance percentage for a student per subject.
     * Returns List of Object[]: {subjectName, totalClasses, presentCount, percentage}
     */
    public List<Object[]> getAttendanceSummary(int studentId) throws SQLException {
        String sql = "SELECT sub.name, " +
                     "COUNT(*) AS total, " +
                     "SUM(CASE WHEN a.status='P' THEN 1 ELSE 0 END) AS present " +
                     "FROM attendance a " +
                     "JOIN subjects sub ON a.subject_id = sub.id " +
                     "WHERE a.student_id=? " +
                     "GROUP BY sub.id, sub.name ORDER BY sub.name";
        List<Object[]> result = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int total   = rs.getInt("total");
                    int present = rs.getInt("present");
                    double pct  = total > 0 ? (present * 100.0 / total) : 0;
                    result.add(new Object[]{rs.getString("name"), total, present,
                            String.format("%.1f%%", pct)});
                }
            }
        }
        return result;
    }

    private Attendance mapRow(ResultSet rs) throws SQLException {
        Attendance a = new Attendance();
        a.setId(rs.getInt("id"));
        a.setStudentId(rs.getInt("student_id"));
        a.setSubjectId(rs.getInt("subject_id"));
        a.setAttDate(rs.getDate("att_date").toLocalDate());
        a.setStatus(rs.getString("status"));
        try { a.setSubjectName(rs.getString("subject_name")); } catch (SQLException ignored) {}
        return a;
    }
}
