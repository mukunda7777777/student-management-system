package com.sms.dao;

import com.sms.model.Student;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data-access operations for the students table.
 */
public class StudentDAO {

    private final Connection conn;

    public StudentDAO() throws SQLException {
        this.conn = DBConnection.getInstance().getConnection();
    }

    // ── CREATE ────────────────────────────────────────────────
    public int create(Student s) throws SQLException {
        String sql = "INSERT INTO students (roll_no, first_name, last_name, dob, gender, " +
                     "email, phone, address, course_id, photo_path, admission_date) " +
                     "VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, s.getRollNo());
            ps.setString(2, s.getFirstName());
            ps.setString(3, s.getLastName());
            ps.setDate  (4, s.getDob() != null ? Date.valueOf(s.getDob()) : null);
            ps.setString(5, s.getGender());
            ps.setString(6, s.getEmail());
            ps.setString(7, s.getPhone());
            ps.setString(8, s.getAddress());
            if (s.getCourseId() != null) ps.setInt(9, s.getCourseId());
            else ps.setNull(9, Types.INTEGER);
            ps.setString(10, s.getPhotoPath());
            ps.setDate  (11, s.getAdmissionDate() != null
                    ? Date.valueOf(s.getAdmissionDate())
                    : Date.valueOf(LocalDate.now()));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    // ── READ ALL ──────────────────────────────────────────────
    public List<Student> findAll() throws SQLException {
        String sql = "SELECT s.*, c.name AS course_name " +
                     "FROM students s LEFT JOIN courses c ON s.course_id = c.id " +
                     "ORDER BY s.id";
        List<Student> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    // ── READ BY ID ────────────────────────────────────────────
    public Student findById(int id) throws SQLException {
        String sql = "SELECT s.*, c.name AS course_name " +
                     "FROM students s LEFT JOIN courses c ON s.course_id = c.id " +
                     "WHERE s.id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    // ── SEARCH ────────────────────────────────────────────────
    public List<Student> search(String keyword) throws SQLException {
        String kw = "%" + keyword.trim() + "%";
        String sql = "SELECT s.*, c.name AS course_name " +
                     "FROM students s LEFT JOIN courses c ON s.course_id = c.id " +
                     "WHERE s.roll_no LIKE ? OR s.first_name LIKE ? OR s.last_name LIKE ? " +
                     "OR c.name LIKE ? ORDER BY s.id";
        List<Student> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kw);
            ps.setString(2, kw);
            ps.setString(3, kw);
            ps.setString(4, kw);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    // ── FIND BY COURSE ────────────────────────────────────────
    public List<Student> findByCourse(int courseId) throws SQLException {
        String sql = "SELECT s.*, c.name AS course_name " +
                     "FROM students s LEFT JOIN courses c ON s.course_id = c.id " +
                     "WHERE s.course_id = ? ORDER BY s.roll_no";
        List<Student> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    // ── UPDATE ────────────────────────────────────────────────
    public boolean update(Student s) throws SQLException {
        String sql = "UPDATE students SET roll_no=?, first_name=?, last_name=?, dob=?, gender=?, " +
                     "email=?, phone=?, address=?, course_id=?, photo_path=?, admission_date=? " +
                     "WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getRollNo());
            ps.setString(2, s.getFirstName());
            ps.setString(3, s.getLastName());
            ps.setDate  (4, s.getDob() != null ? Date.valueOf(s.getDob()) : null);
            ps.setString(5, s.getGender());
            ps.setString(6, s.getEmail());
            ps.setString(7, s.getPhone());
            ps.setString(8, s.getAddress());
            if (s.getCourseId() != null) ps.setInt(9, s.getCourseId());
            else ps.setNull(9, Types.INTEGER);
            ps.setString(10, s.getPhotoPath());
            ps.setDate  (11, s.getAdmissionDate() != null
                    ? Date.valueOf(s.getAdmissionDate())
                    : Date.valueOf(LocalDate.now()));
            ps.setInt   (12, s.getId());
            return ps.executeUpdate() > 0;
        }
    }

    // ── DELETE ────────────────────────────────────────────────
    public boolean delete(int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM students WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ── COUNT ─────────────────────────────────────────────────
    public int count() throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM students");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    // ── MAPPER ───────────────────────────────────────────────
    private Student mapRow(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setId(rs.getInt("id"));
        s.setRollNo(rs.getString("roll_no"));
        s.setFirstName(rs.getString("first_name"));
        s.setLastName(rs.getString("last_name"));
        Date dob = rs.getDate("dob");
        s.setDob(dob != null ? dob.toLocalDate() : null);
        s.setGender(rs.getString("gender"));
        s.setEmail(rs.getString("email"));
        s.setPhone(rs.getString("phone"));
        s.setAddress(rs.getString("address"));
        int cid = rs.getInt("course_id");
        s.setCourseId(rs.wasNull() ? null : cid);
        s.setCourseName(rs.getString("course_name"));
        s.setPhotoPath(rs.getString("photo_path"));
        Date ad = rs.getDate("admission_date");
        s.setAdmissionDate(ad != null ? ad.toLocalDate() : null);
        return s;
    }
}
