package com.sms.ui.course;

import com.sms.model.Course;
import com.sms.model.Subject;
import com.sms.service.CourseService;
import com.sms.ui.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Course and Subject Management Panel for Administrator.
 */
public class CourseManagementPanel extends JPanel {

    private final CourseService courseService;

    // Course table components
    private DefaultTableModel courseTableModel;
    private JTable courseTable;
    private List<Course> currentCourses;

    // Subject table components
    private DefaultTableModel subjectTableModel;
    private JTable subjectTable;
    private List<Subject> currentSubjects;
    private Course selectedCourse = null;
    private JLabel lblSelectedCourse;

    public CourseManagementPanel() throws SQLException {
        this.courseService = new CourseService();
        setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout(0, 12));
        setBorder(new EmptyBorder(0, 0, 0, 0));

        add(buildToolbar(), BorderLayout.NORTH);
        add(buildSplitContent(), BorderLayout.CENTER);

        loadCourses();
    }

    private JPanel buildToolbar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(UITheme.BG_DARK);
        bar.setBorder(new EmptyBorder(0, 0, 8, 0));

        JLabel title = UITheme.makeTitle("Course & Subject Management");
        bar.add(title, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setBackground(UITheme.BG_DARK);

        JButton btnAddCourse = UITheme.makePrimaryButton("+ Add Course");
        btnAddCourse.addActionListener(e -> openCourseDialog(null));
        right.add(btnAddCourse);

        JButton btnAddSubject = UITheme.makeGoldButton("+ Add Subject");
        btnAddSubject.addActionListener(e -> openSubjectDialog(null));
        right.add(btnAddSubject);

        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private JSplitPane buildSplitContent() {
        // Left: Courses
        JPanel leftPanel = new JPanel(new BorderLayout(0, 8));
        leftPanel.setBackground(UITheme.BG_DARK);

        JLabel leftTitle = UITheme.makeSubtitle("Courses");
        leftPanel.add(leftTitle, BorderLayout.NORTH);

        String[] courseCols = {"ID", "Course Name", "Duration", "Description"};
        courseTableModel = new DefaultTableModel(courseCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        courseTable = new JTable(courseTableModel);
        UITheme.styleTable(courseTable);
        courseTable.getColumnModel().getColumn(0).setMaxWidth(50);
        courseTable.getColumnModel().getColumn(2).setPreferredWidth(90);

        courseTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = courseTable.getSelectedRow();
                if (row >= 0 && row < currentCourses.size()) {
                    selectedCourse = currentCourses.get(row);
                    lblSelectedCourse.setText("Subjects for: " + selectedCourse.getName());
                    loadSubjectsForSelectedCourse();
                }
            }
        });

        leftPanel.add(UITheme.makeScrollPane(courseTable), BorderLayout.CENTER);
        leftPanel.add(buildCourseActions(), BorderLayout.SOUTH);

        // Right: Subjects for selected course
        JPanel rightPanel = new JPanel(new BorderLayout(0, 8));
        rightPanel.setBackground(UITheme.BG_DARK);

        lblSelectedCourse = UITheme.makeSubtitle("Subjects (Select a course on the left)");
        rightPanel.add(lblSelectedCourse, BorderLayout.NORTH);

        String[] subjectCols = {"ID", "Subject Name", "Max Marks", "Course"};
        subjectTableModel = new DefaultTableModel(subjectCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        subjectTable = new JTable(subjectTableModel);
        UITheme.styleTable(subjectTable);
        subjectTable.getColumnModel().getColumn(0).setMaxWidth(50);
        subjectTable.getColumnModel().getColumn(2).setPreferredWidth(90);

        rightPanel.add(UITheme.makeScrollPane(subjectTable), BorderLayout.CENTER);
        rightPanel.add(buildSubjectActions(), BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerSize(6);
        splitPane.setBackground(UITheme.BG_DARK);
        return splitPane;
    }

    private JPanel buildCourseActions() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        bar.setBackground(UITheme.BG_CARD);

        JButton btnEdit = UITheme.makeSecondaryButton("✏ Edit Course");
        JButton btnDel  = UITheme.makeDangerButton("🗑 Delete Course");
        JButton btnRef  = UITheme.makeSecondaryButton("⟳ Refresh");

        btnEdit.addActionListener(e -> {
            int row = courseTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a course to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            openCourseDialog(currentCourses.get(row));
        });

        btnDel.addActionListener(e -> deleteSelectedCourse());
        btnRef.addActionListener(e -> loadCourses());

        bar.add(btnEdit);
        bar.add(btnDel);
        bar.add(btnRef);
        return bar;
    }

    private JPanel buildSubjectActions() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        bar.setBackground(UITheme.BG_CARD);

        JButton btnEdit = UITheme.makeSecondaryButton("✏ Edit Subject");
        JButton btnDel  = UITheme.makeDangerButton("🗑 Delete Subject");
        JButton btnShowAll = UITheme.makeSecondaryButton("View All Subjects");

        btnEdit.addActionListener(e -> {
            int row = subjectTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a subject to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            openSubjectDialog(currentSubjects.get(row));
        });

        btnDel.addActionListener(e -> deleteSelectedSubject());
        btnShowAll.addActionListener(e -> loadAllSubjects());

        bar.add(btnEdit);
        bar.add(btnDel);
        bar.add(btnShowAll);
        return bar;
    }

    private void loadCourses() {
        try {
            currentCourses = courseService.getAllCourses();
            courseTableModel.setRowCount(0);
            for (Course c : currentCourses) {
                courseTableModel.addRow(new Object[]{
                    c.getId(), c.getName(), c.getDuration(), c.getDescription()
                });
            }
            if (selectedCourse != null) {
                loadSubjectsForSelectedCourse();
            } else {
                loadAllSubjects();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load courses: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadSubjectsForSelectedCourse() {
        if (selectedCourse == null) return;
        try {
            currentSubjects = courseService.getSubjectsByCourse(selectedCourse.getId());
            displaySubjects(currentSubjects);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load subjects: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadAllSubjects() {
        try {
            selectedCourse = null;
            lblSelectedCourse.setText("All Subjects (All Courses)");
            currentSubjects = courseService.getAllSubjects();
            displaySubjects(currentSubjects);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load subjects: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displaySubjects(List<Subject> list) {
        subjectTableModel.setRowCount(0);
        for (Subject s : list) {
            subjectTableModel.addRow(new Object[]{
                s.getId(), s.getName(), s.getMaxMarks(), s.getCourseName() != null ? s.getCourseName() : "Course #" + s.getCourseId()
            });
        }
    }

    private void deleteSelectedCourse() {
        int row = courseTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a course to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Course c = currentCourses.get(row);
        int opt = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete course '" + c.getName() + "'?\nWarning: This may affect enrolled students and associated subjects.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (opt == JOptionPane.YES_OPTION) {
            try {
                boolean ok = courseService.deleteCourse(c.getId());
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Course deleted successfully.");
                    selectedCourse = null;
                    loadCourses();
                } else {
                    JOptionPane.showMessageDialog(this, "Could not delete course.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting course: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedSubject() {
        int row = subjectTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a subject to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Subject s = currentSubjects.get(row);
        int opt = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete subject '" + s.getName() + "'?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (opt == JOptionPane.YES_OPTION) {
            try {
                boolean ok = courseService.deleteSubject(s.getId());
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Subject deleted successfully.");
                    if (selectedCourse != null) loadSubjectsForSelectedCourse();
                    else loadAllSubjects();
                } else {
                    JOptionPane.showMessageDialog(this, "Could not delete subject.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting subject: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openCourseDialog(Course existing) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            existing == null ? "Add New Course" : "Edit Course", true);
        dialog.getContentPane().setBackground(UITheme.BG_DARK);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UITheme.BG_CARD);
        form.setBorder(new EmptyBorder(20, 24, 20, 24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        JTextField tfName = UITheme.makeTextField(20);
        JTextField tfDuration = UITheme.makeTextField(20);
        JTextArea taDesc = new JTextArea(3, 20);
        taDesc.setBackground(UITheme.BG_INPUT);
        taDesc.setForeground(UITheme.TEXT_PRIMARY);
        taDesc.setCaretColor(Color.WHITE);
        taDesc.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));

        if (existing != null) {
            tfName.setText(existing.getName());
            tfDuration.setText(existing.getDuration());
            taDesc.setText(existing.getDescription());
        }

        gbc.gridx = 0; gbc.gridy = 0;
        form.add(UITheme.makeLabel("Course Name: *"), gbc);
        gbc.gridx = 1;
        form.add(tfName, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        form.add(UITheme.makeLabel("Duration (e.g. 4 Years):"), gbc);
        gbc.gridx = 1;
        form.add(tfDuration, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        form.add(UITheme.makeLabel("Description:"), gbc);
        gbc.gridx = 1;
        form.add(new JScrollPane(taDesc), gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnPanel.setBackground(UITheme.BG_CARD);

        JButton btnSave = UITheme.makePrimaryButton("Save");
        JButton btnCancel = UITheme.makeSecondaryButton("Cancel");

        btnCancel.addActionListener(e -> dialog.dispose());
        btnSave.addActionListener(e -> {
            try {
                String name = tfName.getText().trim();
                String dur  = tfDuration.getText().trim();
                String desc = taDesc.getText().trim();

                if (existing == null) {
                    Course c = new Course(0, name, dur, desc);
                    String err = courseService.addCourse(c);
                    if (err != null) {
                        JOptionPane.showMessageDialog(dialog, err, "Validation", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                } else {
                    existing.setName(name);
                    existing.setDuration(dur);
                    existing.setDescription(desc);
                    String err = courseService.updateCourse(existing);
                    if (err != null) {
                        JOptionPane.showMessageDialog(dialog, err, "Validation", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
                dialog.dispose();
                loadCourses();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void openSubjectDialog(Subject existing) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            existing == null ? "Add New Subject" : "Edit Subject", true);
        dialog.getContentPane().setBackground(UITheme.BG_DARK);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UITheme.BG_CARD);
        form.setBorder(new EmptyBorder(20, 24, 20, 24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        JComboBox<Course> cbCourse = new JComboBox<>();
        try {
            for (Course c : courseService.getAllCourses()) {
                cbCourse.addItem(c);
                if (existing != null && existing.getCourseId() == c.getId()) {
                    cbCourse.setSelectedItem(c);
                } else if (existing == null && selectedCourse != null && selectedCourse.getId() == c.getId()) {
                    cbCourse.setSelectedItem(c);
                }
            }
        } catch (SQLException ignored) {}

        JTextField tfName = UITheme.makeTextField(20);
        JTextField tfMaxMarks = UITheme.makeTextField("100", 10);

        if (existing != null) {
            tfName.setText(existing.getName());
            tfMaxMarks.setText(String.valueOf(existing.getMaxMarks()));
        }

        gbc.gridx = 0; gbc.gridy = 0;
        form.add(UITheme.makeLabel("Course: *"), gbc);
        gbc.gridx = 1;
        form.add(cbCourse, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        form.add(UITheme.makeLabel("Subject Name: *"), gbc);
        gbc.gridx = 1;
        form.add(tfName, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        form.add(UITheme.makeLabel("Max Marks: *"), gbc);
        gbc.gridx = 1;
        form.add(tfMaxMarks, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnPanel.setBackground(UITheme.BG_CARD);

        JButton btnSave = UITheme.makePrimaryButton("Save");
        JButton btnCancel = UITheme.makeSecondaryButton("Cancel");

        btnCancel.addActionListener(e -> dialog.dispose());
        btnSave.addActionListener(e -> {
            try {
                Course c = (Course) cbCourse.getSelectedItem();
                if (c == null) {
                    JOptionPane.showMessageDialog(dialog, "Please select or create a course first.", "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String name = tfName.getText().trim();
                int maxM;
                try {
                    maxM = Integer.parseInt(tfMaxMarks.getText().trim());
                } catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(dialog, "Max marks must be a valid integer.", "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (existing == null) {
                    Subject s = new Subject(0, c.getId(), name, maxM);
                    String err = courseService.addSubject(s);
                    if (err != null) {
                        JOptionPane.showMessageDialog(dialog, err, "Validation", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                } else {
                    existing.setCourseId(c.getId());
                    existing.setName(name);
                    existing.setMaxMarks(maxM);
                    String err = courseService.updateSubject(existing);
                    if (err != null) {
                        JOptionPane.showMessageDialog(dialog, err, "Validation", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
                dialog.dispose();
                if (selectedCourse != null && selectedCourse.getId() == c.getId()) {
                    loadSubjectsForSelectedCourse();
                } else {
                    loadAllSubjects();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}
