package com.sms.service;

import com.sms.dao.CourseDAO;
import com.sms.model.Course;
import com.sms.model.Subject;
import com.sms.util.ValidationUtils;

import java.sql.SQLException;
import java.util.List;

/**
 * Business service for managing courses and subjects.
 */
public class CourseService {

    private final CourseDAO courseDAO;

    public CourseService() throws SQLException {
        this.courseDAO = new CourseDAO();
    }

    public List<Course> getAllCourses() throws SQLException {
        return courseDAO.findAllCourses();
    }

    public Course getCourseById(int id) throws SQLException {
        return courseDAO.findCourseById(id);
    }

    public String addCourse(Course course) throws SQLException {
        String err = ValidationUtils.requireNonBlank(course.getName(), "Course name");
        if (err != null) return err;

        int id = courseDAO.createCourse(course);
        if (id <= 0) return "Failed to save course.";
        course.setId(id);
        return null;
    }

    public String updateCourse(Course course) throws SQLException {
        String err = ValidationUtils.requireNonBlank(course.getName(), "Course name");
        if (err != null) return err;

        boolean success = courseDAO.updateCourse(course);
        return success ? null : "Failed to update course.";
    }

    public boolean deleteCourse(int id) throws SQLException {
        return courseDAO.deleteCourse(id);
    }

    public List<Subject> getSubjectsByCourse(int courseId) throws SQLException {
        return courseDAO.findSubjectsByCourse(courseId);
    }

    public List<Subject> getAllSubjects() throws SQLException {
        return courseDAO.findAllSubjects();
    }

    public String addSubject(Subject subject) throws SQLException {
        String err = ValidationUtils.requireNonBlank(subject.getName(), "Subject name");
        if (err != null) return err;
        if (subject.getMaxMarks() <= 0) return "Maximum marks must be greater than 0.";

        int id = courseDAO.createSubject(subject);
        if (id <= 0) return "Failed to add subject.";
        subject.setId(id);
        return null;
    }

    public String updateSubject(Subject subject) throws SQLException {
        String err = ValidationUtils.requireNonBlank(subject.getName(), "Subject name");
        if (err != null) return err;
        if (subject.getMaxMarks() <= 0) return "Maximum marks must be greater than 0.";

        boolean success = courseDAO.updateSubject(subject);
        return success ? null : "Failed to update subject.";
    }

    public boolean deleteSubject(int id) throws SQLException {
        return courseDAO.deleteSubject(id);
    }
}
