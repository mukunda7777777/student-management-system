package com.sms.dao;

import com.sms.model.Marks;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data-access operations for the marks table.
 */
public class MarksDAO {

    private final Connection conn;

    public MarksDAO() throws SQLException {
        this.conn = DBConnection.getInstance().getConnection();
    }

    public boolean saveOrUpdate(Marks m) throws SQLException {
        String sql = "INSERT INTO marks (student_id, subject_id, exam_type, marks_obtained, max_marks, exam_date) " +
                     "VALUES (?,?,?,?,?,?) " +
                     "ON DUPLICATE KEY UPDATE marks_obtained=VALUES(marks_obtained), " +
                     "max_marks=VALUES(max_marks), exam_date=VALUES(exam_date)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt   (1, m.getStudentId());
            ps.setInt   (2, m.getSubjectId());
            ps.setString(3, m.getExamType());
            ps.setDouble(4, m.getMarksObtained());
            ps.setInt   (5, m.getMaxMarks());
            ps.setDate  (6, m.getExamDate() != null ? Date.valueOf(m.getExamDate()) : null);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Returns all marks for a student in a specific exam type.
     */
    public List<Marks> findByStudentAndExamType(int studentId, String examType) throws SQLException {
        String sql = "SELECT m.id, m.student_id, m.subject_id, m.exam_type, " +
                     "m.marks_obtained, m.max_marks, m.exam_date, " +
                     "sub.name AS subject_name " +
                     "FROM marks m JOIN subjects sub ON m.subject_id = sub.id " +
                     "WHERE m.student_id=? AND m.exam_type=? ORDER BY sub.name";
        List<Marks> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt   (1, studentId);
            ps.setString(2, examType);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    /**
     * Returns all marks for a student (all exam types).
     */
    public List<Marks> findByStudent(int studentId) throws SQLException {
        String sql = "SELECT m.id, m.student_id, m.subject_id, m.exam_type, " +
                     "m.marks_obtained, m.max_marks, m.exam_date, " +
                     "sub.name AS subject_name " +
                     "FROM marks m JOIN subjects sub ON m.subject_id = sub.id " +
                     "WHERE m.student_id=? ORDER BY sub.name, m.exam_type";
        List<Marks> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    /**
     * Result summary: total marks obtained, total max marks, percentage.
     * Returns Object[]: {totalObtained, totalMax, percentage}
     */
    public Object[] getResultSummary(int studentId, String examType) throws SQLException {
        String sql = "SELECT SUM(marks_obtained) AS total_obt, SUM(max_marks) AS total_max " +
                     "FROM marks WHERE student_id=? AND exam_type=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt   (1, studentId);
            ps.setString(2, examType);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double obt = rs.getDouble("total_obt");
                    double max = rs.getDouble("total_max");
                    double pct = max > 0 ? (obt / max * 100) : 0;
                    return new Object[]{obt, max, pct};
                }
            }
        }
        return new Object[]{0.0, 0.0, 0.0};
    }

    public boolean delete(int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM marks WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Marks mapRow(ResultSet rs) throws SQLException {
        Marks m = new Marks();
        m.setId(rs.getInt("id"));
        m.setStudentId(rs.getInt("student_id"));
        m.setSubjectId(rs.getInt("subject_id"));
        m.setSubjectName(rs.getString("subject_name"));
        m.setExamType(rs.getString("exam_type"));
        m.setMarksObtained(rs.getDouble("marks_obtained"));
        m.setMaxMarks(rs.getInt("max_marks"));
        Date d = rs.getDate("exam_date");
        m.setExamDate(d != null ? d.toLocalDate() : null);
        return m;
    }
}
