package com.sms.ui.fee;

import com.sms.model.Fee;
import com.sms.model.Student;
import com.sms.service.FeeService;
import com.sms.service.StudentService;
import com.sms.ui.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Fee Management Panel for Administrator: record payments, track dues, view history.
 */
public class FeeManagementPanel extends JPanel {

    private final FeeService feeService;
    private final StudentService studentService;

    private DefaultTableModel tableModel;
    private JTable table;
    private List<Fee> currentList;
    private JCheckBox chkOnlyDues;
    private JLabel lblTotalCollected;
    private JLabel lblTotalDues;

    private static final String[] COLS = {
        "Receipt #", "Roll No.", "Student Name", "Course", "Amount Paid (Rs.)", "Due Amount (Rs.)", "Paid Date", "Fee Type", "Remarks"
    };

    public FeeManagementPanel() throws SQLException {
        this.feeService = new FeeService();
        this.studentService = new StudentService();

        setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout(0, 12));
        setBorder(new EmptyBorder(0, 0, 0, 0));

        add(buildTopBar(), BorderLayout.NORTH);
        add(buildCenterPanel(), BorderLayout.CENTER);
        add(buildBottomBar(), BorderLayout.SOUTH);

        loadFees();
    }

    private JPanel buildTopBar() {
        JPanel container = new JPanel(new BorderLayout(0, 8));
        container.setBackground(UITheme.BG_DARK);

        JLabel title = UITheme.makeTitle("Fee & Payment Management");
        container.add(title, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setBackground(UITheme.BG_DARK);

        chkOnlyDues = new JCheckBox("Show Only Pending Dues");
        chkOnlyDues.setForeground(UITheme.TEXT_SECONDARY);
        chkOnlyDues.setBackground(UITheme.BG_DARK);
        chkOnlyDues.addActionListener(e -> loadFees());

        JButton btnRecord = UITheme.makePrimaryButton("+ Record Payment");
        btnRecord.addActionListener(e -> openRecordFeeDialog(null));

        JButton btnRefresh = UITheme.makeSecondaryButton("⟳ Refresh");
        btnRefresh.addActionListener(e -> loadFees());

        right.add(chkOnlyDues);
        right.add(btnRecord);
        right.add(btnRefresh);

        container.add(right, BorderLayout.EAST);
        return container;
    }

    private JPanel buildCenterPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(UITheme.BG_DARK);

        tableModel = new DefaultTableModel(COLS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UITheme.styleTable(table);

        table.getColumnModel().getColumn(0).setPreferredWidth(120);
        table.getColumnModel().getColumn(1).setPreferredWidth(90);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(4).setPreferredWidth(110);
        table.getColumnModel().getColumn(5).setPreferredWidth(110);

        p.add(UITheme.makeScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildBottomBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(UITheme.BG_CARD);
        bar.setBorder(new EmptyBorder(8, 12, 8, 12));

        JPanel stats = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        stats.setBackground(UITheme.BG_CARD);

        lblTotalCollected = UITheme.makeSubtitle("Total Collected: Rs. 0.00");
        lblTotalCollected.setForeground(UITheme.ACCENT_TEAL);

        lblTotalDues = UITheme.makeSubtitle("Outstanding Dues: Rs. 0.00");
        lblTotalDues.setForeground(UITheme.ACCENT_DANGER);

        stats.add(lblTotalCollected);
        stats.add(lblTotalDues);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setBackground(UITheme.BG_CARD);

        JButton btnEdit = UITheme.makeSecondaryButton("Edit Record");
        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0 || row >= currentList.size()) {
                JOptionPane.showMessageDialog(this, "Please select a fee record to edit.", "Select Row", JOptionPane.WARNING_MESSAGE);
                return;
            }
            openRecordFeeDialog(currentList.get(row));
        });

        actions.add(btnEdit);

        bar.add(stats, BorderLayout.WEST);
        bar.add(actions, BorderLayout.EAST);
        return bar;
    }

    private void loadFees() {
        try {
            if (chkOnlyDues.isSelected()) {
                currentList = feeService.getDueStudents();
            } else {
                currentList = feeService.getAllFees();
            }

            tableModel.setRowCount(0);
            double totalPaid = 0;
            double totalDues = 0;

            for (Fee f : currentList) {
                totalPaid += f.getAmountPaid();
                totalDues += f.getDueAmount();
                tableModel.addRow(new Object[]{
                    f.getReceiptNo(),
                    f.getRollNo() != null ? f.getRollNo() : "-",
                    f.getStudentName() != null ? f.getStudentName() : "ID #" + f.getStudentId(),
                    f.getCourseName() != null ? f.getCourseName() : "-",
                    String.format("%.2f", f.getAmountPaid()),
                    String.format("%.2f", f.getDueAmount()),
                    f.getPaidDate() != null ? f.getPaidDate().toString() : "-",
                    f.getFeeType(),
                    f.getRemarks() != null ? f.getRemarks() : ""
                });
            }

            lblTotalCollected.setText(String.format("Total Collected: Rs. %,.2f", totalPaid));
            lblTotalDues.setText(String.format("Outstanding Dues: Rs. %,.2f", totalDues));

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading fee records: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openRecordFeeDialog(Fee existing) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            existing == null ? "Record New Fee Payment" : "Edit Fee Record", true);
        dialog.getContentPane().setBackground(UITheme.BG_DARK);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UITheme.BG_CARD);
        form.setBorder(new EmptyBorder(20, 24, 20, 24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        JComboBox<Student> cbStudent = new JComboBox<>();
        try {
            for (Student s : studentService.getAllStudents()) {
                cbStudent.addItem(s);
                if (existing != null && existing.getStudentId() == s.getId()) {
                    cbStudent.setSelectedItem(s);
                }
            }
        } catch (SQLException ignored) {}
        if (existing != null) cbStudent.setEnabled(false);

        JTextField tfReceipt = UITheme.makeTextField(existing != null ? existing.getReceiptNo() : "REC-" + System.currentTimeMillis() % 1000000, 15);
        JTextField tfAmountPaid = UITheme.makeTextField(existing != null ? String.valueOf(existing.getAmountPaid()) : "0.0", 12);
        JTextField tfDueAmount = UITheme.makeTextField(existing != null ? String.valueOf(existing.getDueAmount()) : "0.0", 12);

        String[] feeTypes = {"Tuition Fee", "Exam Fee", "Hostel Fee", "Library Fee", "Transport Fee", "Other"};
        JComboBox<String> cbType = new JComboBox<>(feeTypes);
        if (existing != null && existing.getFeeType() != null) cbType.setSelectedItem(existing.getFeeType());

        JTextField tfDate = UITheme.makeTextField(existing != null && existing.getPaidDate() != null ? existing.getPaidDate().toString() : LocalDate.now().toString(), 10);
        JTextField tfRemarks = UITheme.makeTextField(existing != null && existing.getRemarks() != null ? existing.getRemarks() : "", 20);

        gbc.gridx = 0; gbc.gridy = 0;
        form.add(UITheme.makeLabel("Student: *"), gbc);
        gbc.gridx = 1;
        form.add(cbStudent, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        form.add(UITheme.makeLabel("Receipt No.: *"), gbc);
        gbc.gridx = 1;
        form.add(tfReceipt, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        form.add(UITheme.makeLabel("Fee Type:"), gbc);
        gbc.gridx = 1;
        form.add(cbType, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        form.add(UITheme.makeLabel("Amount Paid (Rs.): *"), gbc);
        gbc.gridx = 1;
        form.add(tfAmountPaid, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        form.add(UITheme.makeLabel("Due Amount (Rs.):"), gbc);
        gbc.gridx = 1;
        form.add(tfDueAmount, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        form.add(UITheme.makeLabel("Payment Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        form.add(tfDate, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        form.add(UITheme.makeLabel("Remarks:"), gbc);
        gbc.gridx = 1;
        form.add(tfRemarks, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnPanel.setBackground(UITheme.BG_CARD);

        JButton btnSave = UITheme.makePrimaryButton("Save");
        JButton btnCancel = UITheme.makeSecondaryButton("Cancel");

        btnCancel.addActionListener(e -> dialog.dispose());
        btnSave.addActionListener(e -> {
            try {
                Student s = (Student) cbStudent.getSelectedItem();
                if (s == null) {
                    JOptionPane.showMessageDialog(dialog, "Please select a student.", "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                double paid, due;
                try {
                    paid = Double.parseDouble(tfAmountPaid.getText().trim());
                    due  = Double.parseDouble(tfDueAmount.getText().trim());
                } catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(dialog, "Paid and Due amounts must be valid numbers.", "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                LocalDate pDate = LocalDate.parse(tfDate.getText().trim());

                if (existing == null) {
                    Fee f = new Fee(0, s.getId(), tfReceipt.getText().trim(), paid, due, pDate,
                        (String) cbType.getSelectedItem(), tfRemarks.getText().trim());
                    String err = feeService.recordFee(f);
                    if (err != null) {
                        JOptionPane.showMessageDialog(dialog, err, "Validation", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                } else {
                    existing.setReceiptNo(tfReceipt.getText().trim());
                    existing.setAmountPaid(paid);
                    existing.setDueAmount(due);
                    existing.setPaidDate(pDate);
                    existing.setFeeType((String) cbType.getSelectedItem());
                    existing.setRemarks(tfRemarks.getText().trim());
                    String err = feeService.updateFee(existing);
                    if (err != null) {
                        JOptionPane.showMessageDialog(dialog, err, "Validation", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }

                dialog.dispose();
                loadFees();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
