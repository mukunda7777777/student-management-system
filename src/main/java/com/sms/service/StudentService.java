package com.sms.service;

import com.sms.dao.StudentDAO;
import com.sms.dao.UserDAO;
import com.sms.model.Student;
import com.sms.model.User;
import com.sms.util.PasswordUtils;
import com.sms.util.ValidationUtils;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Business logic for student management.
 */
public class StudentService {

    private final StudentDAO studentDAO;
    private final UserDAO userDAO;

    public StudentService() throws SQLException {
        this.studentDAO = new StudentDAO();
        this.userDAO    = new UserDAO();
    }

    /** Add a new student and create their login account. */
    public String addStudent(Student student) throws SQLException {
        // Validate
        List<String> errors = validate(student);
        if (!errors.isEmpty()) return String.join("\n", errors);

        // Insert student
        int newId = studentDAO.create(student);
        if (newId <= 0) return "Failed to save student record.";

        // Create student user account (username = roll_no, password = Student@123)
        User user = new User();
        user.setUsername(student.getRollNo());
        user.setPasswordHash(PasswordUtils.hash("Student@123"));
        user.setRole("STUDENT");
        user.setStudentId(newId);
        user.setActive(true);
        userDAO.create(user);

        return null; // null = success
    }

    /** Update existing student. */
    public String updateStudent(Student student) throws SQLException {
        List<String> errors = validate(student);
        if (!errors.isEmpty()) return String.join("\n", errors);
        return studentDAO.update(student) ? null : "Update failed.";
    }

    /** Delete student and their user account. */
    public boolean deleteStudent(int studentId) throws SQLException {
        userDAO.deleteByStudentId(studentId);
        return studentDAO.delete(studentId);
    }

    public List<Student> getAllStudents() throws SQLException {
        return studentDAO.findAll();
    }

    public Student getStudentById(int id) throws SQLException {
        return studentDAO.findById(id);
    }

    public List<Student> searchStudents(String keyword) throws SQLException {
        if (ValidationUtils.isNullOrBlank(keyword)) return studentDAO.findAll();
        return studentDAO.search(keyword);
    }

    public List<Student> getStudentsByCourse(int courseId) throws SQLException {
        return studentDAO.findByCourse(courseId);
    }

    public int getTotalCount() throws SQLException {
        return studentDAO.count();
    }

    // ── Internal validation ───────────────────────────────────
    private List<String> validate(Student s) {
        List<String> errors = new ArrayList<>();
        String err;
        if ((err = ValidationUtils.validateRollNo(s.getRollNo())) != null)   errors.add(err);
        if ((err = ValidationUtils.requireNonBlank(s.getFirstName(), "First Name")) != null) errors.add(err);
        if ((err = ValidationUtils.requireNonBlank(s.getLastName(),  "Last Name"))  != null) errors.add(err);
        if ((err = ValidationUtils.validateEmail(s.getEmail()))   != null) errors.add(err);
        if ((err = ValidationUtils.validatePhone(s.getPhone()))   != null) errors.add(err);
        if (s.getAdmissionDate() == null) s.setAdmissionDate(LocalDate.now());
        return errors;
    }
}
