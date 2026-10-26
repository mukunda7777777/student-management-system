package com.sms.service;

import com.sms.dao.AttendanceDAO;
import com.sms.dao.CourseDAO;
import com.sms.dao.FeeDAO;
import com.sms.dao.NoticeDAO;

import java.sql.SQLException;

/**
 * Provides aggregated dashboard statistics for the Admin.
 */
public class DashboardService {

    private final com.sms.dao.StudentDAO studentDAO;
    private final CourseDAO courseDAO;
    private final FeeDAO feeDAO;
    private final NoticeDAO noticeDAO;

    public DashboardService() throws SQLException {
        this.studentDAO = new com.sms.dao.StudentDAO();
        this.courseDAO  = new CourseDAO();
        this.feeDAO     = new FeeDAO();
        this.noticeDAO  = new NoticeDAO();
    }

    public int getTotalStudents() throws SQLException {
        return studentDAO.count();
    }

    public int getTotalCourses() throws SQLException {
        return courseDAO.countCourses();
    }

    public int getDueStudentsCount() throws SQLException {
        return feeDAO.countDueStudents();
    }

    public int getActiveNoticesCount() throws SQLException {
        return noticeDAO.countActive();
    }
}
