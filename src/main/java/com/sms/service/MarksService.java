package com.sms.service;

import com.sms.dao.MarksDAO;
import com.sms.model.Marks;

import java.sql.SQLException;
import java.util.List;

/**
 * Business logic for examination marks including grade computation.
 */
public class MarksService {

    private final MarksDAO marksDAO;

    // Grade thresholds (percentage-based, O/A+/A/B+/B/C/F)
    private static final double O_MIN  = 90;
    private static final double AP_MIN = 80;
    private static final double A_MIN  = 70;
    private static final double BP_MIN = 60;
    private static final double B_MIN  = 50;
    private static final double C_MIN  = 40;

    public MarksService() throws SQLException {
        this.marksDAO = new MarksDAO();
    }

    public String saveOrUpdateMarks(Marks m) throws SQLException {
        String err = com.sms.util.ValidationUtils.validateMarks(m.getMarksObtained(), m.getMaxMarks());
        if (err != null) return err;
        marksDAO.saveOrUpdate(m);
        return null;
    }

    public List<Marks> getMarksByStudent(int studentId) throws SQLException {
        return marksDAO.findByStudent(studentId);
    }

    public List<Marks> getMarksByStudentAndExam(int studentId, String examType) throws SQLException {
        return marksDAO.findByStudentAndExamType(studentId, examType);
    }

    /**
     * Returns result summary: {totalObtained, totalMax, percentage, grade}
     */
    public Object[] getResultSummary(int studentId, String examType) throws SQLException {
        Object[] raw = marksDAO.getResultSummary(studentId, examType);
        double pct   = (double) raw[2];
        String grade = computeGrade(pct);
        return new Object[]{raw[0], raw[1], pct, grade};
    }

    /**
     * Computes letter grade from percentage.
     */
    public static String computeGrade(double percentage) {
        if (percentage >= O_MIN)  return "O (Outstanding)";
        if (percentage >= AP_MIN) return "A+ (Excellent)";
        if (percentage >= A_MIN)  return "A (Very Good)";
        if (percentage >= BP_MIN) return "B+ (Good)";
        if (percentage >= B_MIN)  return "B (Average)";
        if (percentage >= C_MIN)  return "C (Pass)";
        return "F (Fail)";
    }

    public static String computeShortGrade(double percentage) {
        if (percentage >= O_MIN)  return "O";
        if (percentage >= AP_MIN) return "A+";
        if (percentage >= A_MIN)  return "A";
        if (percentage >= BP_MIN) return "B+";
        if (percentage >= B_MIN)  return "B";
        if (percentage >= C_MIN)  return "C";
        return "F";
    }

    public boolean deleteMarks(int marksId) throws SQLException {
        return marksDAO.delete(marksId);
    }
}
