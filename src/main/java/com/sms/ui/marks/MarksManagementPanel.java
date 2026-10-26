package com.sms.ui.marks;

import com.sms.model.Course;
import com.sms.model.Marks;
import com.sms.model.Student;
import com.sms.model.Subject;
import com.sms.service.CourseService;
import com.sms.service.MarksService;
import com.sms.service.StudentService;
import com.sms.ui.util.PrintUtils;
import com.sms.ui.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Marks Entry & Result Management Panel for Administrator.
 */
public class MarksManagementPanel extends JPanel {

    private final MarksService marksService;
    private final StudentService studentService;
    private final CourseService courseService;

    private JComboBox<Student> cbStudent;
    private JComboBox<String> cbExamType;
    private DefaultTableModel tableModel;
    private JTable table;
    private JLabel lblSummary;

    private Student currentStudent = null;
    private List<Subject> currentSubjects = new ArrayList<>();
    private boolean isUpdatingTable = false;

    private static final String[] EXAM_TYPES = {
        "Final Exam", "Mid Term", "Unit Test 1", "Unit Test 2", "Internal Assessment"
    };
    private static final String[] COLS = {
        "Subject Name", "Max Marks", "Marks Obtained", "Percentage", "Grade"
    };

    public MarksManagementPanel() throws SQLException {
        this.marksService = new MarksService();
        this.studentService = new StudentService();
        this.courseService = new CourseService();

        setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout(0, 12));
        setBorder(new EmptyBorder(0, 0, 0, 0));

        add(buildTopBar(), BorderLayout.NORTH);
        add(buildCenterPanel(), BorderLayout.CENTER);
        add(buildBottomBar(), BorderLayout.SOUTH);

        loadStudents();
    }

    private JPanel buildTopBar() {
        JPanel container = new JPanel(new BorderLayout(0, 8));
        container.setBackground(UITheme.BG_DARK);

        JLabel title = UITheme.makeTitle("Examination Marks & Mark Sheet Management");
        container.add(title, BorderLayout.NORTH);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        controls.setBackground(UITheme.BG_CARD);
        controls.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));

        cbStudent = new JComboBox<>();
        cbStudent.setPreferredSize(new Dimension(260, 32));
        cbStudent.addActionListener(e -> onStudentSelected());

        cbExamType = new JComboBox<>(EXAM_TYPES);
        cbExamType.setPreferredSize(new Dimension(160, 32));
        cbExamType.addActionListener(e -> loadMarksForStudent());

        JButton btnLoad = UITheme.makePrimaryButton("Load Marks");
        btnLoad.addActionListener(e -> loadMarksForStudent());

        controls.add(UITheme.makeLabel("Student:"));
        controls.add(cbStudent);
        controls.add(UITheme.makeLabel("Exam Type:"));
        controls.add(cbExamType);
        controls.add(btnLoad);

        container.add(controls, BorderLayout.CENTER);
        return container;
    }

    private JPanel buildCenterPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(UITheme.BG_DARK);

        tableModel = new DefaultTableModel(COLS, 0) {
            @Override public boolean isCellEditable(int r, int c) {
                return c == 1 || c == 2; // Editable: Max Marks & Marks Obtained
            }
        };
        table = new JTable(tableModel);
        UITheme.styleTable(table);

        table.getModel().addTableModelListener(e -> {
            if (isUpdatingTable) return;
            int col = e.getColumn();
            int row = e.getFirstRow();
            if (row >= 0 && (col == 1 || col == 2)) {
                updateRowGrade(row);
                updateSummaryLabel();
            }
        });

        p.add(UITheme.makeScrollPane(table), BorderLayout.CENTER);

        lblSummary = new JLabel("Select a student and exam type to enter or view marks.");
        lblSummary.setFont(UITheme.FONT_SUBTITLE);
        lblSummary.setForeground(UITheme.ACCENT_TEAL);
        lblSummary.setBorder(new EmptyBorder(6, 4, 6, 4));
        p.add(lblSummary, BorderLayout.SOUTH);

        return p;
    }

    private JPanel buildBottomBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        bar.setBackground(UITheme.BG_CARD);

        JButton btnSave = UITheme.makePrimaryButton("💾 Save Marks");
        btnSave.addActionListener(e -> saveMarks());

        JButton btnPrint = UITheme.makeGoldButton("🖨 Print / Export Mark Sheet");
        btnPrint.addActionListener(e -> printMarkSheet());

        bar.add(btnSave);
        bar.add(btnPrint);
        return bar;
    }

    private void loadStudents() {
        cbStudent.removeAllItems();
        try {
            List<Student> students = studentService.getAllStudents();
            for (Student s : students) cbStudent.addItem(s);
            if (!students.isEmpty()) cbStudent.setSelectedIndex(0);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load students: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onStudentSelected() {
        currentStudent = (Student) cbStudent.getSelectedItem();
        loadMarksForStudent();
    }

    private void loadMarksForStudent() {
        currentStudent = (Student) cbStudent.getSelectedItem();
        if (currentStudent == null) return;
        String examType = (String) cbExamType.getSelectedItem();

        try {
            isUpdatingTable = true;
            // Load subjects for the student's course
            currentSubjects = courseService.getSubjectsByCourse(currentStudent.getCourseId());
            // Load existing marks
            List<Marks> existingMarks = marksService.getMarksByStudentAndExam(currentStudent.getId(), examType);

            tableModel.setRowCount(0);
            for (Subject sub : currentSubjects) {
                int maxM = sub.getMaxMarks();
                double obt = 0.0;
                boolean found = false;

                for (Marks m : existingMarks) {
                    if (m.getSubjectId() == sub.getId()) {
                        obt = m.getMarksObtained();
                        maxM = m.getMaxMarks();
                        found = true;
                        break;
                    }
                }

                double pct = maxM > 0 ? (obt / maxM * 100) : 0;
                String grade = found ? MarksService.computeShortGrade(pct) : "-";
                tableModel.addRow(new Object[]{sub.getName(), maxM, obt, String.format("%.1f%%", pct), grade});
            }

            updateSummaryLabel();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading marks: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            isUpdatingTable = false;
        }
    }

    private void updateRowGrade(int row) {
        try {
            isUpdatingTable = true;
            int max = Integer.parseInt(tableModel.getValueAt(row, 1).toString().trim());
            double obt = Double.parseDouble(tableModel.getValueAt(row, 2).toString().trim());
            double pct = max > 0 ? (obt / max * 100) : 0;
            String grade = MarksService.computeShortGrade(pct);

            tableModel.setValueAt(String.format("%.1f%%", pct), row, 3);
            tableModel.setValueAt(grade, row, 4);
        } catch (Exception ignored) {
        } finally {
            isUpdatingTable = false;
        }
    }

    private void updateSummaryLabel() {
        double totalObt = 0;
        double totalMax = 0;
        int rowCount = tableModel.getRowCount();
        if (rowCount == 0) {
            lblSummary.setText("No subjects found for this student's course.");
            return;
        }

        for (int i = 0; i < rowCount; i++) {
            try {
                int max = Integer.parseInt(tableModel.getValueAt(i, 1).toString().trim());
                double obt = Double.parseDouble(tableModel.getValueAt(i, 2).toString().trim());
                totalMax += max;
                totalObt += obt;
            } catch (Exception ignored) {}
        }

        double overallPct = totalMax > 0 ? (totalObt / totalMax * 100) : 0;
        String overallGrade = MarksService.computeGrade(overallPct);
        lblSummary.setText(String.format("Total: %.1f / %.1f  |  Overall: %.2f%%  |  Grade: %s",
            totalObt, totalMax, overallPct, overallGrade));
    }

    private void saveMarks() {
        if (currentStudent == null || currentSubjects.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No student or subjects loaded.", "Info", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String examType = (String) cbExamType.getSelectedItem();

        try {
            for (int i = 0; i < currentSubjects.size(); i++) {
                Subject sub = currentSubjects.get(i);
                int maxM;
                double obt;
                try {
                    maxM = Integer.parseInt(tableModel.getValueAt(i, 1).toString().trim());
                    obt = Double.parseDouble(tableModel.getValueAt(i, 2).toString().trim());
                } catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(this, "Invalid number for subject: " + sub.getName(), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Marks m = new Marks(0, currentStudent.getId(), sub.getId(), examType, obt, maxM, LocalDate.now());
                String err = marksService.saveOrUpdateMarks(m);
                if (err != null) {
                    JOptionPane.showMessageDialog(this, err + " (Subject: " + sub.getName() + ")", "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
            JOptionPane.showMessageDialog(this, "Marks saved successfully for " + currentStudent.getFullName() + "!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadMarksForStudent();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to save marks: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void printMarkSheet() {
        if (currentStudent == null) {
            JOptionPane.showMessageDialog(this, "Please select a student first.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String examType = (String) cbExamType.getSelectedItem();

        try {
            List<Marks> list = marksService.getMarksByStudentAndExam(currentStudent.getId(), examType);
            if (list.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No saved marks found to print. Please click 'Save Marks' first.", "No Data", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            Object[] summary = marksService.getResultSummary(currentStudent.getId(), examType);
            PrintUtils.printMarkSheet(currentStudent, list, summary, examType);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error preparing mark sheet: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
