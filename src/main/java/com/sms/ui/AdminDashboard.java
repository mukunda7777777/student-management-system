package com.sms.ui;

import com.sms.service.AuthService;
import com.sms.service.DashboardService;
import com.sms.ui.attendance.MarkAttendancePanel;
import com.sms.ui.course.CourseManagementPanel;
import com.sms.ui.fee.FeeManagementPanel;
import com.sms.ui.marks.MarksManagementPanel;
import com.sms.ui.notice.NoticeManagementPanel;
import com.sms.ui.student.StudentManagementPanel;
import com.sms.ui.util.UITheme;
import com.sms.util.SessionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

/**
 * Main Admin Dashboard with sidebar navigation and content area.
 */
public class AdminDashboard extends JFrame {

    private JPanel contentArea;
    private JPanel activeNavItem;
    private DashboardService dashboardService;

    // Nav items: {label, emoji, panel class key}
    private static final String[][] NAV_ITEMS = {
        {"Dashboard",  "🏠"},
        {"Students",   "👩‍🎓"},
        {"Courses",    "📚"},
        {"Attendance", "📅"},
        {"Marks",      "📝"},
        {"Fees",       "💰"},
        {"Notices",    "📢"},
    };

    public AdminDashboard() {
        try { dashboardService = new DashboardService(); }
        catch (SQLException e) { /* non-fatal — stats will show 0 */ }

        initUI();
        showDashboardHome();
    }

    private void initUI() {
        setTitle("SMS — Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1100, 680));

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(UITheme.BG_DARK);
        setContentPane(root);

        root.add(buildSidebar(), BorderLayout.WEST);

        contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(UITheme.BG_DARK);
        contentArea.setBorder(new EmptyBorder(20, 20, 20, 20));
        root.add(contentArea, BorderLayout.CENTER);
    }

    // ── Sidebar ───────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(UITheme.BG_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(210, 0));

        // Logo section
        JPanel logo = new JPanel();
        logo.setBackground(UITheme.BG_SIDEBAR);
        logo.setLayout(new BoxLayout(logo, BoxLayout.Y_AXIS));
        logo.setBorder(new EmptyBorder(24, 16, 20, 16));

        JLabel iconLbl = new JLabel("🎓");
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        iconLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel smsLbl = new JLabel("SMS");
        smsLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        smsLbl.setForeground(UITheme.TEXT_PRIMARY);
        smsLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel roleLbl = new JLabel("Administrator");
        roleLbl.setFont(UITheme.FONT_SMALL);
        roleLbl.setForeground(UITheme.ACCENT_TEAL);
        roleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        logo.add(iconLbl);
        logo.add(Box.createVerticalStrut(6));
        logo.add(smsLbl);
        logo.add(Box.createVerticalStrut(2));
        logo.add(roleLbl);

        // Separator
        JSeparator sep = new JSeparator();
        sep.setForeground(UITheme.BORDER_COLOR);
        sep.setBackground(UITheme.BORDER_COLOR);

        // Nav items
        JPanel nav = new JPanel();
        nav.setBackground(UITheme.BG_SIDEBAR);
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBorder(new EmptyBorder(12, 0, 0, 0));

        for (String[] item : NAV_ITEMS) {
            JPanel navItem = makeNavItem(item[1], item[0]);
            nav.add(navItem);
            nav.add(Box.createVerticalStrut(2));
        }

        // Logout button at bottom
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        logoutPanel.setBackground(UITheme.BG_SIDEBAR);
        JButton btnLogout = UITheme.makeDangerButton("⏻  Logout");
        btnLogout.setFont(UITheme.FONT_BUTTON);
        btnLogout.addActionListener(e -> doLogout());
        logoutPanel.add(btnLogout);

        sidebar.add(logo, BorderLayout.NORTH);
        sidebar.add(nav, BorderLayout.CENTER);
        sidebar.add(logoutPanel, BorderLayout.SOUTH);
        return sidebar;
    }

    private JPanel makeNavItem(String emoji, String label) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        item.setBackground(UITheme.BG_SIDEBAR);
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel emojiLbl = new JLabel(emoji);
        emojiLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        JLabel textLbl = new JLabel(label);
        textLbl.setFont(UITheme.FONT_BODY);
        textLbl.setForeground(UITheme.TEXT_SECONDARY);

        item.add(emojiLbl);
        item.add(textLbl);

        item.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                setActiveNav(item, textLbl);
                navigateTo(label);
            }
            @Override public void mouseEntered(MouseEvent e) {
                if (item != activeNavItem) item.setBackground(UITheme.HOVER_COLOR);
            }
            @Override public void mouseExited(MouseEvent e) {
                if (item != activeNavItem) item.setBackground(UITheme.BG_SIDEBAR);
            }
        });
        return item;
    }

    private void setActiveNav(JPanel item, JLabel textLbl) {
        // Reset previous active
        if (activeNavItem != null) {
            activeNavItem.setBackground(UITheme.BG_SIDEBAR);
            for (Component c : activeNavItem.getComponents()) {
                if (c instanceof JLabel) ((JLabel) c).setForeground(UITheme.TEXT_SECONDARY);
            }
        }
        item.setBackground(new Color(0x1A3A5C));
        textLbl.setForeground(UITheme.ACCENT_TEAL);
        for (Component c : item.getComponents()) {
            if (c instanceof JLabel) ((JLabel) c).setForeground(UITheme.ACCENT_TEAL);
        }
        activeNavItem = item;
    }

    private void navigateTo(String section) {
        contentArea.removeAll();
        JPanel panel;
        try {
            panel = switch (section) {
                case "Dashboard"  -> buildHomePanel();
                case "Students"   -> new StudentManagementPanel();
                case "Courses"    -> new CourseManagementPanel();
                case "Attendance" -> new MarkAttendancePanel();
                case "Marks"      -> new MarksManagementPanel();
                case "Fees"       -> new FeeManagementPanel();
                case "Notices"    -> new NoticeManagementPanel();
                default           -> buildHomePanel();
            };
        } catch (SQLException e) {
            JLabel err = UITheme.makeLabel("Error loading panel: " + e.getMessage());
            err.setForeground(UITheme.ACCENT_DANGER);
            panel = new JPanel();
            ((JPanel) panel).add(err);
        }
        contentArea.add(panel, BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
    }

    private void showDashboardHome() {
        navigateTo("Dashboard");
    }

    // ── Home / Dashboard panel ────────────────────────────────
    private JPanel buildHomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_DARK);

        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        header.setBackground(UITheme.BG_DARK);
        header.setBorder(new EmptyBorder(0, 0, 24, 0));
        JLabel title = UITheme.makeTitle("Dashboard Overview");
        JLabel user  = UITheme.makeLabel("  ·  Logged in as Admin");
        header.add(title);
        header.add(user);
        panel.add(header, BorderLayout.NORTH);

        // Stats cards
        JPanel cards = new JPanel(new GridLayout(1, 4, 16, 0));
        cards.setBackground(UITheme.BG_DARK);
        cards.setPreferredSize(new Dimension(0, 120));

        int students = 0, courses = 0, dues = 0, notices = 0;
        try {
            if (dashboardService != null) {
                students = dashboardService.getTotalStudents();
                courses  = dashboardService.getTotalCourses();
                dues     = dashboardService.getDueStudentsCount();
                notices  = dashboardService.getActiveNoticesCount();
            }
        } catch (SQLException ignored) {}

        cards.add(makeStatCard("👩‍🎓", "Total Students", String.valueOf(students), UITheme.ACCENT_TEAL));
        cards.add(makeStatCard("📚", "Courses",          String.valueOf(courses),  new Color(0x7B61FF)));
        cards.add(makeStatCard("💰", "Fee Dues",          String.valueOf(dues),    UITheme.ACCENT_DANGER));
        cards.add(makeStatCard("📢", "Active Notices",    String.valueOf(notices), UITheme.ACCENT_GOLD));
        panel.add(cards, BorderLayout.CENTER);

        // Quick access section
        JPanel quickPanel = new JPanel(new BorderLayout());
        quickPanel.setBackground(UITheme.BG_DARK);
        quickPanel.setBorder(new EmptyBorder(24, 0, 0, 0));

        JLabel quickTitle = UITheme.makeHeading("Quick Actions");
        quickTitle.setBorder(new EmptyBorder(0, 0, 12, 0));
        quickPanel.add(quickTitle, BorderLayout.NORTH);

        JPanel quickGrid = new JPanel(new GridLayout(2, 4, 12, 12));
        quickGrid.setBackground(UITheme.BG_DARK);

        String[][] quickItems = {
            {"Add Student",     "👩‍🎓", "Students"},
            {"Add Course",      "📚",   "Courses"},
            {"Mark Attendance", "📅",   "Attendance"},
            {"Enter Marks",     "📝",   "Marks"},
            {"Record Fee",      "💰",   "Fees"},
            {"Post Notice",     "📢",   "Notices"},
        };
        for (String[] qi : quickItems) {
            JButton qb = makeQuickButton(qi[1], qi[0]);
            qb.addActionListener(e -> navigateTo(qi[2]));
            quickGrid.add(qb);
        }
        quickPanel.add(quickGrid, BorderLayout.CENTER);
        panel.add(quickPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel makeStatCard(String emoji, String label, String value, Color accent) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(UITheme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accent),
                new EmptyBorder(18, 16, 18, 16)));

        JLabel emojiLbl = new JLabel(emoji + "  " + label);
        emojiLbl.setFont(UITheme.FONT_SMALL);
        emojiLbl.setForeground(UITheme.TEXT_SECONDARY);

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLbl.setForeground(UITheme.TEXT_PRIMARY);

        card.add(emojiLbl, BorderLayout.NORTH);
        card.add(valueLbl, BorderLayout.CENTER);
        return card;
    }

    private JButton makeQuickButton(String emoji, String label) {
        JButton btn = new JButton("<html><center>" + emoji + "<br><span style='font-size:11px'>" + label + "</span></center></html>");
        btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        btn.setForeground(UITheme.TEXT_PRIMARY);
        btn.setBackground(UITheme.BG_CARD);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1),
                new EmptyBorder(12, 8, 12, 8)));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(UITheme.HOVER_COLOR); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(UITheme.BG_CARD); }
        });
        return btn;
    }

    private void doLogout() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            try { new AuthService().logout(); } catch (Exception ignored) {}
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        }
    }
}
