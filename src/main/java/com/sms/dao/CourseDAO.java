package com.sms.dao;

import com.sms.model.Course;
import com.sms.model.Subject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data-access operations for courses and subjects.
 */
public class CourseDAO {

    private final Connection conn;

    public CourseDAO() throws SQLException {
        this.conn = DBConnection.getInstance().getConnection();
    }

    // ══════════════════════════════════════════════════════════
    //  COURSE operations
    // ══════════════════════════════════════════════════════════

    public int createCourse(Course c) throws SQLException {
        String sql = "INSERT INTO courses (name, duration, description) VALUES (?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getName());
            ps.setString(2, c.getDuration());
            ps.setString(3, c.getDescription());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    public List<Course> findAllCourses() throws SQLException {
        List<Course> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id, name, duration, description FROM courses ORDER BY name");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Course(rs.getInt("id"), rs.getString("name"),
                        rs.getString("duration"), rs.getString("description")));
            }
        }
        return list;
    }

    public Course findCourseById(int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id, name, duration, description FROM courses WHERE id=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Course(rs.getInt("id"), rs.getString("name"),
                            rs.getString("duration"), rs.getString("description"));
                }
            }
        }
        return null;
    }

    public boolean updateCourse(Course c) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE courses SET name=?, duration=?, description=? WHERE id=?")) {
            ps.setString(1, c.getName());
            ps.setString(2, c.getDuration());
            ps.setString(3, c.getDescription());
            ps.setInt   (4, c.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteCourse(int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM courses WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ══════════════════════════════════════════════════════════
    //  SUBJECT operations
    // ══════════════════════════════════════════════════════════

    public int createSubject(Subject s) throws SQLException {
        String sql = "INSERT INTO subjects (course_id, name, max_marks) VALUES (?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt   (1, s.getCourseId());
            ps.setString(2, s.getName());
            ps.setInt   (3, s.getMaxMarks());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    public List<Subject> findSubjectsByCourse(int courseId) throws SQLException {
        String sql = "SELECT s.id, s.course_id, s.name, s.max_marks, c.name AS course_name " +
                     "FROM subjects s JOIN courses c ON s.course_id = c.id " +
                     "WHERE s.course_id = ? ORDER BY s.name";
        List<Subject> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Subject sub = new Subject(rs.getInt("id"), rs.getInt("course_id"),
                            rs.getString("name"), rs.getInt("max_marks"));
                    sub.setCourseName(rs.getString("course_name"));
                    list.add(sub);
                }
            }
        }
        return list;
    }

    public List<Subject> findAllSubjects() throws SQLException {
        String sql = "SELECT s.id, s.course_id, s.name, s.max_marks, c.name AS course_name " +
                     "FROM subjects s JOIN courses c ON s.course_id = c.id ORDER BY c.name, s.name";
        List<Subject> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Subject sub = new Subject(rs.getInt("id"), rs.getInt("course_id"),
                        rs.getString("name"), rs.getInt("max_marks"));
                sub.setCourseName(rs.getString("course_name"));
                list.add(sub);
            }
        }
        return list;
    }

    public boolean updateSubject(Subject s) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE subjects SET name=?, max_marks=? WHERE id=?")) {
            ps.setString(1, s.getName());
            ps.setInt   (2, s.getMaxMarks());
            ps.setInt   (3, s.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteSubject(int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM subjects WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public int countCourses() throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM courses");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
}
