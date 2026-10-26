package com.sms.ui.attendance;

import com.sms.model.Attendance;
import com.sms.model.Course;
import com.sms.model.Student;
import com.sms.model.Subject;
import com.sms.service.AttendanceService;
import com.sms.service.CourseService;
import com.sms.service.StudentService;
import com.sms.ui.util.PrintUtils;
import com.sms.ui.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Attendance marking and summary panel for Administrator.
 */
public class MarkAttendancePanel extends JPanel {

    private final AttendanceService attendanceService;
    private final CourseService courseService;
    private final StudentService studentService;

    private JComboBox<Course> cbCourse;
    private JComboBox<Subject> cbSubject;
    private JTextField tfDate;
    private DefaultTableModel tableModel;
    private JTable table;
    private List<Student> loadedStudents = new ArrayList<>();

    private static final String[] COLS = {"Roll No.", "Student Name", "Status (P/A)"};

    public MarkAttendancePanel() throws SQLException {
        this.attendanceService = new AttendanceService();
        this.courseService = new CourseService();
        this.studentService = new StudentService();

        setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout(0, 12));
        setBorder(new EmptyBorder(0, 0, 0, 0));

        add(buildTopBar(), BorderLayout.NORTH);
        add(buildCenterPanel(), BorderLayout.CENTER);
        add(buildBottomBar(), BorderLayout.SOUTH);

        loadCourses();
    }

    private JPanel buildTopBar() {
        JPanel container = new JPanel(new BorderLayout(0, 8));
        container.setBackground(UITheme.BG_DARK);

        JLabel title = UITheme.makeTitle("Mark Student Attendance");
        container.add(title, BorderLayout.NORTH);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        controls.setBackground(UITheme.BG_CARD);
        controls.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));

        cbCourse = new JComboBox<>();
        cbCourse.setPreferredSize(new Dimension(160, 32));
        cbCourse.addActionListener(e -> onCourseSelected());

        cbSubject = new JComboBox<>();
        cbSubject.setPreferredSize(new Dimension(180, 32));

        tfDate = UITheme.makeTextField(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE), 10);
        tfDate.setToolTipText("Date (YYYY-MM-DD)");

        JButton btnLoad = UITheme.makePrimaryButton("Load Students");
        btnLoad.addActionListener(e -> loadAttendanceSheet());

        controls.add(UITheme.makeLabel("Course:"));
        controls.add(cbCourse);
        controls.add(UITheme.makeLabel("Subject:"));
        controls.add(cbSubject);
        controls.add(UITheme.makeLabel("Date:"));
        controls.add(tfDate);
        controls.add(btnLoad);

        container.add(controls, BorderLayout.CENTER);
        return container;
    }

    private JPanel buildCenterPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(UITheme.BG_DARK);

        tableModel = new DefaultTableModel(COLS, 0) {
            @Override public boolean isCellEditable(int r, int c) {
                return c == 2; // only status column is editable
            }
        };
        table = new JTable(tableModel);
        UITheme.styleTable(table);

        // Set combo editor for Status (Present / Absent)
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"P", "A"});
        table.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(statusCombo));
        table.getColumnModel().getColumn(0).setPreferredWidth(120);
        table.getColumnModel().getColumn(1).setPreferredWidth(280);
        table.getColumnModel().getColumn(2).setPreferredWidth(120);

        p.add(UITheme.makeScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildBottomBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(UITheme.BG_CARD);
        bar.setBorder(new EmptyBorder(8, 12, 8, 12));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setBackground(UITheme.BG_CARD);

        JButton btnAllP = UITheme.makeSecondaryButton("✓ Mark All Present");
        JButton btnAllA = UITheme.makeSecondaryButton("✗ Mark All Absent");
        btnAllP.addActionListener(e -> setAllStatus("P"));
        btnAllA.addActionListener(e -> setAllStatus("A"));

        left.add(btnAllP);
        left.add(btnAllA);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setBackground(UITheme.BG_CARD);

        JButton btnViewSummary = UITheme.makeGoldButton("📊 View Student Summary / Report");
        btnViewSummary.addActionListener(e -> viewStudentSummaryDialog());

        JButton btnSave = UITheme.makePrimaryButton("💾 Save Attendance");
        btnSave.addActionListener(e -> saveAttendance());

        right.add(btnViewSummary);
        right.add(btnSave);

        bar.add(left, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private void loadCourses() {
        cbCourse.removeAllItems();
        try {
            List<Course> courses = courseService.getAllCourses();
            for (Course c : courses) cbCourse.addItem(c);
            if (!courses.isEmpty()) cbCourse.setSelectedIndex(0);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load courses: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onCourseSelected() {
        cbSubject.removeAllItems();
        Course c = (Course) cbCourse.getSelectedItem();
        if (c == null) return;
        try {
            List<Subject> subjects = courseService.getSubjectsByCourse(c.getId());
            for (Subject s : subjects) cbSubject.addItem(s);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load subjects: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadAttendanceSheet() {
        Course c = (Course) cbCourse.getSelectedItem();
        Subject s = (Subject) cbSubject.getSelectedItem();
        if (c == null || s == null) {
            JOptionPane.showMessageDialog(this, "Please select both Course and Subject.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate date;
        try {
            date = LocalDate.parse(tfDate.getText().trim());
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid date in YYYY-MM-DD format.", "Invalid Date", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Get all students for this course
            List<Student> allStudents = studentService.getAllStudents();
            loadedStudents.clear();
            for (Student stu : allStudents) {
                if (stu.getCourseId() == c.getId()) {
                    loadedStudents.add(stu);
                }
            }

            // Check if attendance is already recorded for this date and subject
            List<Attendance> existing = attendanceService.getAttendanceByDateAndSubject(date, s.getId());

            tableModel.setRowCount(0);
            for (Student stu : loadedStudents) {
                String status = "P"; // default Present
                for (Attendance att : existing) {
                    if (att.getStudentId() == stu.getId()) {
                        status = att.getStatus();
                        break;
                    }
                }
                tableModel.addRow(new Object[]{stu.getRollNo(), stu.getFullName(), status});
            }

            if (loadedStudents.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No students enrolled in course: " + c.getName(), "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading students: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setAllStatus(String status) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            tableModel.setValueAt(status, i, 2);
        }
    }

    private void saveAttendance() {
        Subject s = (Subject) cbSubject.getSelectedItem();
        if (s == null) {
            JOptionPane.showMessageDialog(this, "Subject must be selected.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (loadedStudents.isEmpty() || tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No student attendance to save.", "Info", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate date;
        try {
            date = LocalDate.parse(tfDate.getText().trim());
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Attendance> records = new ArrayList<>();
        for (int i = 0; i < loadedStudents.size(); i++) {
            Student stu = loadedStudents.get(i);
            String status = (String) tableModel.getValueAt(i, 2);
            if (status == null || (!status.equalsIgnoreCase("P") && !status.equalsIgnoreCase("A"))) {
                status = "P";
            }
            records.add(new Attendance(0, stu.getId(), s.getId(), date, status.toUpperCase()));
        }

        try {
            attendanceService.saveBulkAttendance(records);
            JOptionPane.showMessageDialog(this, "Attendance saved successfully for " + records.size() + " students!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to save attendance: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewStudentSummaryDialog() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= loadedStudents.size()) {
            JOptionPane.showMessageDialog(this, "Please select a student row from the table first.", "Selection Required", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Student student = loadedStudents.get(row);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Attendance Summary — " + student.getFullName(), true);
        dialog.getContentPane().setBackground(UITheme.BG_DARK);
        dialog.setLayout(new BorderLayout(0, 10));

        JPanel header = new JPanel(new GridLayout(2, 1, 4, 4));
        header.setBackground(UITheme.BG_CARD);
        header.setBorder(new EmptyBorder(12, 16, 12, 16));

        header.add(UITheme.makeTitle("Student: " + student.getFullName() + " (" + student.getRollNo() + ")"));
        header.add(UITheme.makeLabel("Course: " + (student.getCourseName() != null ? student.getCourseName() : "-")));

        String[] cols = {"Subject", "Total Classes", "Present", "Attendance %"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable sumTable = new JTable(model);
        UITheme.styleTable(sumTable);

        List<Object[]> summaryRows = new ArrayList<>();
        try {
            summaryRows = attendanceService.getAttendanceSummary(student.getId());
            for (Object[] r : summaryRows) model.addRow(r);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(dialog, "Error loading summary: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnPanel.setBackground(UITheme.BG_CARD);

        JButton btnPrint = UITheme.makeGoldButton("🖨 Print / Export Report");
        final List<Object[]> finalSummaryRows = summaryRows;
        btnPrint.addActionListener(e -> PrintUtils.printAttendanceReport(student, finalSummaryRows));

        JButton btnClose = UITheme.makeSecondaryButton("Close");
        btnClose.addActionListener(e -> dialog.dispose());

        btnPanel.add(btnPrint);
        btnPanel.add(btnClose);

        dialog.add(header, BorderLayout.NORTH);
        dialog.add(UITheme.makeScrollPane(sumTable), BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setSize(550, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}
