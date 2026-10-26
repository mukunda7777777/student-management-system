package com.sms.service;

import com.sms.dao.AttendanceDAO;
import com.sms.model.Attendance;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Business logic for attendance management.
 */
public class AttendanceService {

    private final AttendanceDAO attendanceDAO;

    public AttendanceService() throws SQLException {
        this.attendanceDAO = new AttendanceDAO();
    }

    public void markAttendance(Attendance attendance) throws SQLException {
        attendanceDAO.saveOrUpdate(attendance);
    }

    public void saveBulkAttendance(List<Attendance> records) throws SQLException {
        if (records != null && !records.isEmpty()) {
            attendanceDAO.saveBulk(records);
        }
    }

    public List<Attendance> getAttendanceByStudentAndSubject(int studentId, int subjectId) throws SQLException {
        return attendanceDAO.findByStudentAndSubject(studentId, subjectId);
    }

    public List<Attendance> getAttendanceByDateAndSubject(LocalDate date, int subjectId) throws SQLException {
        return attendanceDAO.findByDateAndSubject(date, subjectId);
    }

    public List<Object[]> getAttendanceSummary(int studentId) throws SQLException {
        return attendanceDAO.getAttendanceSummary(studentId);
    }
}
