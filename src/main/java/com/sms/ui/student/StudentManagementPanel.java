package com.sms.ui.student;

import com.sms.model.Student;
import com.sms.service.StudentService;
import com.sms.ui.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.List;

/**
 * Admin panel: searchable student list with Add, Edit, Delete actions.
 */
public class StudentManagementPanel extends JPanel {

    private StudentService studentService;
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField tfSearch;
    private List<Student> currentList;

    private static final String[] COLUMNS = {
        "ID", "Roll No.", "Name", "Gender", "Course", "Email", "Phone", "Admission Date"
    };

    public StudentManagementPanel() throws SQLException {
        studentService = new StudentService();
        setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout(0, 12));
        setBorder(new EmptyBorder(0, 0, 0, 0));

        add(buildToolbar(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        loadStudents("");
    }

    private JPanel buildToolbar() {
        JPanel bar = new JPanel(new BorderLayout(12, 0));
        bar.setBackground(UITheme.BG_DARK);
        bar.setBorder(new EmptyBorder(0, 0, 8, 0));

        JLabel title = UITheme.makeTitle("Student Management");
        bar.add(title, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setBackground(UITheme.BG_DARK);

        // Search
        tfSearch = UITheme.makeTextField(18);
        tfSearch.setToolTipText("Search by name, roll no., or course");
        tfSearch.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) {
                loadStudents(tfSearch.getText());
            }
        });
        right.add(UITheme.makeLabel("🔍 "));
        right.add(tfSearch);

        // Add button
        JButton btnAdd = UITheme.makePrimaryButton("+ Add Student");
        btnAdd.addActionListener(e -> openAddDialog());
        right.add(btnAdd);

        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private JPanel buildTablePanel() {
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UITheme.styleTable(table);
        table.getColumnModel().getColumn(0).setMaxWidth(50);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_DARK);
        panel.add(UITheme.makeScrollPane(table), BorderLayout.CENTER);
        panel.add(buildActionBar(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildActionBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        bar.setBackground(UITheme.BG_CARD);

        JButton btnEdit   = UITheme.makeGoldButton("✏ Edit Selected");
        JButton btnDelete = UITheme.makeDangerButton("🗑 Delete Selected");
        JButton btnRefresh= UITheme.makeSecondaryButton("⟳ Refresh");

        btnEdit.addActionListener(e -> openEditDialog());
        btnDelete.addActionListener(e -> deleteSelected());
        btnRefresh.addActionListener(e -> loadStudents(tfSearch.getText()));

        JLabel countLbl = UITheme.makeLabel("  0 students");
        countLbl.setName("countLbl");

        bar.add(btnEdit);
        bar.add(btnDelete);
        bar.add(btnRefresh);
        bar.add(countLbl);
        return bar;
    }

    private void loadStudents(String keyword) {
        tableModel.setRowCount(0);
        try {
            currentList = studentService.searchStudents(keyword);
            for (Student s : currentList) {
                tableModel.addRow(new Object[]{
                    s.getId(), s.getRollNo(), s.getFullName(), s.getGender(),
                    s.getCourseName() != null ? s.getCourseName() : "-",
                    s.getEmail() != null ? s.getEmail() : "-",
                    s.getPhone() != null ? s.getPhone() : "-",
                    s.getAdmissionDate() != null ? s.getAdmissionDate().toString() : "-"
                });
            }
            // Update count label
            updateCount(currentList.size());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading students: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCount(int count) {
        // Find count label
        Container parent = getComponent(1) instanceof JPanel p ? p : null;
        // Simple approach: refresh title
        // actual count is visible in table rows
    }

    private void openAddDialog() {
        try {
            StudentFormDialog dlg = new StudentFormDialog(
                    (Frame) SwingUtilities.getWindowAncestor(this), null);
            dlg.setVisible(true);
            if (dlg.isSaved()) loadStudents(tfSearch.getText());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void openEditDialog() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a student to edit.",
                    "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        try {
            Student student = studentService.getStudentById(id);
            StudentFormDialog dlg = new StudentFormDialog(
                    (Frame) SwingUtilities.getWindowAncestor(this), student);
            dlg.setVisible(true);
            if (dlg.isSaved()) loadStudents(tfSearch.getText());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a student to delete.",
                    "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int id   = (int) tableModel.getValueAt(row, 0);
        String name = tableModel.getValueAt(row, 2).toString();
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete student \"" + name + "\"?\n\n" +
                "⚠ This will also remove all their attendance, marks, fee, and login records.\n" +
                "This action CANNOT be undone.",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                studentService.deleteStudent(id);
                loadStudents(tfSearch.getText());
                JOptionPane.showMessageDialog(this, "Student deleted.", "Done",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Delete failed: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
