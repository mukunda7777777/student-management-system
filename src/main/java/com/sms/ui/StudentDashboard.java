package com.sms.ui;

import com.sms.dao.AttendanceDAO;
import com.sms.dao.FeeDAO;
import com.sms.dao.NoticeDAO;
import com.sms.model.Fee;
import com.sms.model.Notice;
import com.sms.model.Student;
import com.sms.service.AuthService;
import com.sms.service.StudentService;
import com.sms.ui.util.UITheme;
import com.sms.ui.util.PrintUtils;
import com.sms.service.MarksService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Student-facing dashboard — shows profile, attendance, marks, fees, notices.
 */
public class StudentDashboard extends JFrame {

    private final int studentId;
    private Student student;
    private JTabbedPane tabs;

    public StudentDashboard(int studentId) {
        this.studentId = studentId;
        try {
            StudentService svc = new StudentService();
            student = svc.getStudentById(studentId);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading student: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        initUI();
    }

    private void initUI() {
        String name = student != null ? student.getFullName() : "Student";
        setTitle("SMS — " + name);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 680);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 600));

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG_DARK);
        setContentPane(root);

        // ── Top bar ───────────────────────────────────────────
        root.add(buildTopBar(), BorderLayout.NORTH);

        // ── Tabs ──────────────────────────────────────────────
        tabs = new JTabbedPane();
        tabs.setBackground(UITheme.BG_DARK);
        tabs.setForeground(UITheme.TEXT_PRIMARY);
        tabs.setFont(UITheme.FONT_BODY);
        tabs.setBorder(new EmptyBorder(10, 10, 10, 10));

        tabs.addTab("🏠 Profile",    buildProfilePanel());
        tabs.addTab("📅 Attendance", buildAttendancePanel());
        tabs.addTab("📝 Results",    buildResultsPanel());
        tabs.addTab("💰 Fees",       buildFeesPanel());
        tabs.addTab("📢 Notices",    buildNoticesPanel());

        root.add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(UITheme.BG_SIDEBAR);
        bar.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel title = UITheme.makeHeading("Student Portal");
        title.setForeground(UITheme.ACCENT_TEAL);
        bar.add(title, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setBackground(UITheme.BG_SIDEBAR);
        if (student != null) {
            JLabel userLbl = UITheme.makeLabel("👤  " + student.getFullName() + "  |  " + student.getRollNo());
            userLbl.setForeground(UITheme.TEXT_PRIMARY);
            right.add(userLbl);
        }
        JButton btnLogout = UITheme.makeDangerButton("Logout");
        btnLogout.setFont(UITheme.FONT_SMALL);
        btnLogout.addActionListener(e -> {
            try { new AuthService().logout(); } catch (Exception ignored) {}
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        });
        right.add(btnLogout);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    // ── Profile Tab ───────────────────────────────────────────
    private JPanel buildProfilePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 30));
        panel.setBackground(UITheme.BG_DARK);

        if (student == null) return panel;

        JPanel card = UITheme.makeCard();
        card.setLayout(new GridBagLayout());
        card.setPreferredSize(new Dimension(600, 400));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.anchor = GridBagConstraints.WEST;

        // Avatar
        JLabel avatar = new JLabel("👩‍🎓", SwingConstants.CENTER);
        avatar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        card.add(avatar, gbc);

        gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.WEST;
        String[][] fields = {
            {"Full Name",       student.getFullName()},
            {"Roll No.",        student.getRollNo()},
            {"Course",          student.getCourseName() != null ? student.getCourseName() : "-"},
            {"Gender",          student.getGender()},
            {"Email",           student.getEmail() != null ? student.getEmail() : "-"},
            {"Phone",           student.getPhone() != null ? student.getPhone() : "-"},
            {"Admission Date",  student.getAdmissionDate() != null
                                    ? student.getAdmissionDate().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")) : "-"},
        };
        int row = 1;
        for (String[] f : fields) {
            gbc.gridx = 0; gbc.gridy = row;
            card.add(UITheme.makeLabel(f[0] + " :"), gbc);
            gbc.gridx = 1; gbc.gridy = row;
            JLabel val = UITheme.makeHeading(f[1]);
            card.add(val, gbc);
            row++;
        }
        panel.add(card);
        return panel;
    }

    // ── Attendance Tab ────────────────────────────────────────
    private JPanel buildAttendancePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel title = UITheme.makeHeading("My Attendance Summary");
        title.setBorder(new EmptyBorder(0, 0, 12, 0));
        panel.add(title, BorderLayout.NORTH);

        String[] cols = {"Subject", "Total Classes", "Present", "Attendance %"};
        String[][] data;
        try {
            AttendanceDAO dao = new AttendanceDAO();
            List<Object[]> rows = dao.getAttendanceSummary(studentId);
            data = new String[rows.size()][4];
            for (int i = 0; i < rows.size(); i++) {
                Object[] r = rows.get(i);
                data[i] = new String[]{r[0].toString(), r[1].toString(),
                                       r[2].toString(), r[3].toString()};
            }
        } catch (SQLException e) {
            data = new String[0][0];
        }

        JTable table = new JTable(data, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        UITheme.styleTable(table);

        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setBackground(UITheme.BG_DARK);
        tableWrapper.add(UITheme.makeScrollPane(table), BorderLayout.CENTER);

        // Print button
        JButton btnPrint = UITheme.makeGoldButton("🖨 Print Report");
        String[][] finalData = data;
        btnPrint.addActionListener(e -> {
            if (student == null) return;
            try {
                AttendanceDAO dao = new AttendanceDAO();
                PrintUtils.printAttendanceReport(student, dao.getAttendanceSummary(studentId));
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(UITheme.BG_DARK);
        btnPanel.add(btnPrint);

        tableWrapper.add(btnPanel, BorderLayout.SOUTH);
        panel.add(tableWrapper, BorderLayout.CENTER);
        return panel;
    }

    // ── Results Tab ───────────────────────────────────────────
    private JPanel buildResultsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Exam type selector
        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topRow.setBackground(UITheme.BG_DARK);
        topRow.setBorder(new EmptyBorder(0, 0, 12, 0));
        JLabel lbl = UITheme.makeLabel("Exam Type:");
        JComboBox<String> cbExam = UITheme.makeComboBox();
        cbExam.addItem("Final");
        cbExam.addItem("Mid-Term");
        cbExam.addItem("Internal");
        topRow.add(lbl);
        topRow.add(cbExam);
        panel.add(topRow, BorderLayout.NORTH);

        // Table
        String[] cols = {"Subject", "Max Marks", "Marks Obtained", "Percentage", "Grade"};
        JTable table = new JTable(new javax.swing.table.DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
        UITheme.styleTable(table);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(UITheme.BG_DARK);
        center.add(UITheme.makeScrollPane(table), BorderLayout.CENTER);

        // Summary bar
        JLabel summaryLbl = UITheme.makeLabel("  Select exam type to load results.");
        summaryLbl.setFont(UITheme.FONT_BODY);
        summaryLbl.setForeground(UITheme.TEXT_SECONDARY);
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        summaryPanel.setBackground(UITheme.BG_CARD);
        summaryPanel.add(summaryLbl);

        // Print button
        JButton btnPrint = UITheme.makeGoldButton("🖨 Print Mark Sheet");

        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setBackground(UITheme.BG_DARK);
        bottomBar.add(summaryPanel, BorderLayout.CENTER);
        JPanel printPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        printPanel.setBackground(UITheme.BG_DARK);
        printPanel.add(btnPrint);
        bottomBar.add(printPanel, BorderLayout.EAST);
        center.add(bottomBar, BorderLayout.SOUTH);
        panel.add(center, BorderLayout.CENTER);

        // Load data on exam type change
        Runnable loadData = () -> {
            String examType = (String) cbExam.getSelectedItem();
            javax.swing.table.DefaultTableModel model =
                    (javax.swing.table.DefaultTableModel) table.getModel();
            model.setRowCount(0);
            try {
                MarksService svc = new MarksService();
                var marks = svc.getMarksByStudentAndExam(studentId, examType);
                for (var m : marks) {
                    model.addRow(new Object[]{
                        m.getSubjectName(), m.getMaxMarks(),
                        String.format("%.1f", m.getMarksObtained()),
                        String.format("%.1f%%", m.getPercentage()),
                        MarksService.computeShortGrade(m.getPercentage())
                    });
                }
                Object[] summary = svc.getResultSummary(studentId, examType);
                summaryLbl.setText(String.format("  Total: %.1f / %.1f   |   Percentage: %.2f%%   |   Overall Grade: %s",
                        summary[0], summary[1], summary[2],
                        MarksService.computeShortGrade((double) summary[2])));
                btnPrint.addActionListener(ev -> {
                    if (student == null) return;
                    PrintUtils.printMarkSheet(student, marks, summary, examType);
                });
            } catch (SQLException ex) {
                summaryLbl.setText("  Error loading marks.");
            }
        };
        cbExam.addActionListener(e -> loadData.run());
        loadData.run();

        return panel;
    }

    // ── Fees Tab ──────────────────────────────────────────────
    private JPanel buildFeesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel title = UITheme.makeHeading("My Fee Records");
        title.setBorder(new EmptyBorder(0, 0, 12, 0));
        panel.add(title, BorderLayout.NORTH);

        String[] cols = {"Receipt No.", "Fee Type", "Amount Paid (₹)", "Due Amount (₹)", "Paid Date", "Remarks"};
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        try {
            FeeDAO dao = new FeeDAO();
            for (Fee f : dao.findByStudent(studentId)) {
                model.addRow(new Object[]{
                    f.getReceiptNo(), f.getFeeType() != null ? f.getFeeType() : "-",
                    String.format("₹%.2f", f.getAmountPaid()),
                    f.getDueAmount() > 0 ? String.format("₹%.2f", f.getDueAmount()) : "—",
                    f.getPaidDate() != null ? f.getPaidDate().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")) : "-",
                    f.getRemarks() != null ? f.getRemarks() : "-"
                });
            }
        } catch (SQLException ignored) {}

        JTable table = new JTable(model);
        UITheme.styleTable(table);
        panel.add(UITheme.makeScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    // ── Notices Tab ───────────────────────────────────────────
    private JPanel buildNoticesPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(UITheme.BG_DARK);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel title = UITheme.makeHeading("📢 Notices & Announcements");
        title.setBorder(new EmptyBorder(0, 0, 14, 0));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);

        try {
            NoticeDAO dao = new NoticeDAO();
            List<Notice> notices = dao.findActive();
            if (notices.isEmpty()) {
                JLabel empty = UITheme.makeLabel("No active notices.");
                empty.setAlignmentX(Component.LEFT_ALIGNMENT);
                panel.add(empty);
            }
            for (Notice n : notices) {
                JPanel card = UITheme.makeCard();
                card.setLayout(new BorderLayout(0, 6));
                card.setAlignmentX(Component.LEFT_ALIGNMENT);
                card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

                JLabel noticeTitle = UITheme.makeHeading(n.getTitle());
                JTextArea body = UITheme.makeTextArea(3, 60);
                body.setText(n.getContent());
                body.setEditable(false);
                body.setBackground(UITheme.BG_CARD);

                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
                JLabel meta = UITheme.makeLabel("Posted by " + n.getPostedBy() + "  •  " +
                        (n.getPostedDate() != null ? n.getPostedDate().format(fmt) : "-"));
                meta.setFont(UITheme.FONT_SMALL);

                card.add(noticeTitle, BorderLayout.NORTH);
                card.add(body, BorderLayout.CENTER);
                card.add(meta, BorderLayout.SOUTH);

                panel.add(card);
                panel.add(Box.createVerticalStrut(10));
            }
        } catch (SQLException e) {
            panel.add(UITheme.makeLabel("Error loading notices: " + e.getMessage()));
        }

        JScrollPane sp = new JScrollPane(panel);
        sp.setBorder(null);
        sp.getViewport().setBackground(UITheme.BG_DARK);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(UITheme.BG_DARK);
        wrapper.add(sp, BorderLayout.CENTER);
        return wrapper;
    }
}
