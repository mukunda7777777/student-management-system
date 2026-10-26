package com.sms.ui;

import com.sms.model.User;
import com.sms.service.AuthService;
import com.sms.ui.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;

/**
 * Login screen — the application entry point UI.
 * Provides username/password fields, show/hide password toggle,
 * and role-based routing to Admin or Student dashboard.
 */
public class LoginFrame extends JFrame {

    private JTextField    tfUsername;
    private JPasswordField pfPassword;
    private JLabel        lblError;
    private JButton       btnLogin;
    private JCheckBox     cbShowPass;
    private AuthService   authService;

    public LoginFrame() {
        initUI();
        try {
            authService = new AuthService();
        } catch (SQLException e) {
            lblError.setText("DB offline: Start MySQL & check db.properties");
        }
    }

    private void initUI() {
        setTitle("SMS — Student Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 560);
        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, 900, 560, 20, 20));

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG_DARK);
        setContentPane(root);

        // ── LEFT decorative panel ─────────────────────────────
        JPanel left = buildLeftPanel();
        root.add(left, BorderLayout.WEST);

        // ── RIGHT form panel ──────────────────────────────────
        JPanel right = buildRightPanel();
        root.add(right, BorderLayout.CENTER);

        // Drag to move (since undecorated)
        DragListener drag = new DragListener();
        root.addMouseListener(drag);
        root.addMouseMotionListener(drag);
    }

    private JPanel buildLeftPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(0x0D3B6E),
                        0, getHeight(), new Color(0x00C8A0));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setPreferredSize(new Dimension(340, 560));
        panel.setLayout(new GridBagLayout());

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(0, 30, 0, 30));

        // Icon / Logo area
        JLabel icon = new JLabel("🎓", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Student", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title2 = new JLabel("Management", SwingConstants.CENTER);
        title2.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title2.setForeground(Color.WHITE);
        title2.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title3 = new JLabel("System", SwingConstants.CENTER);
        title3.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        title3.setForeground(new Color(0xCCFFEF));
        title3.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(0x00C8A0, true));
        sep.setMaximumSize(new Dimension(200, 2));

        JLabel desc = new JLabel("<html><center>Centralized academic<br>administration platform</center></html>", SwingConstants.CENTER);
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        desc.setForeground(new Color(0xB0EED8));
        desc.setAlignmentX(Component.CENTER_ALIGNMENT);

        content.add(Box.createVerticalGlue());
        content.add(icon);
        content.add(Box.createVerticalStrut(12));
        content.add(title);
        content.add(title2);
        content.add(title3);
        content.add(Box.createVerticalStrut(20));
        content.add(sep);
        content.add(Box.createVerticalStrut(16));
        content.add(desc);
        content.add(Box.createVerticalGlue());

        panel.add(content);
        return panel;
    }

    private JPanel buildRightPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UITheme.BG_DARK);

        JPanel form = new JPanel();
        form.setBackground(UITheme.BG_CARD);
        form.setBorder(new EmptyBorder(40, 50, 40, 50));
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setPreferredSize(new Dimension(380, 420));

        // ── Close button top right ────────────────────────────
        JButton btnClose = new JButton("✕");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnClose.setForeground(UITheme.TEXT_SECONDARY);
        btnClose.setBackground(UITheme.BG_CARD);
        btnClose.setBorderPainted(false);
        btnClose.setContentAreaFilled(false);
        btnClose.setFocusPainted(false);
        btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> System.exit(0));
        JPanel closeRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        closeRow.setOpaque(false);
        closeRow.add(btnClose);

        // ── Heading ───────────────────────────────────────────
        JLabel heading = UITheme.makeTitle("Welcome Back");
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel subhead = UITheme.makeLabel("Sign in to your account");
        subhead.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ── Username ──────────────────────────────────────────
        JLabel lblUser = UITheme.makeLabel("Username");
        lblUser.setAlignmentX(Component.LEFT_ALIGNMENT);
        tfUsername = UITheme.makeTextField(20);
        tfUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        tfUsername.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ── Password ──────────────────────────────────────────
        JLabel lblPass = UITheme.makeLabel("Password");
        lblPass.setAlignmentX(Component.LEFT_ALIGNMENT);
        pfPassword = UITheme.makePasswordField(20);
        pfPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        pfPassword.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ── Show password ─────────────────────────────────────
        cbShowPass = new JCheckBox("Show password");
        cbShowPass.setFont(UITheme.FONT_SMALL);
        cbShowPass.setForeground(UITheme.TEXT_SECONDARY);
        cbShowPass.setBackground(UITheme.BG_CARD);
        cbShowPass.setFocusPainted(false);
        cbShowPass.setAlignmentX(Component.LEFT_ALIGNMENT);
        cbShowPass.addActionListener(e -> {
            pfPassword.setEchoChar(cbShowPass.isSelected() ? '\0' : '•');
        });

        // ── Error label ───────────────────────────────────────
        lblError = new JLabel(" ");
        lblError.setFont(UITheme.FONT_SMALL);
        lblError.setForeground(UITheme.ACCENT_DANGER);
        lblError.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ── Login button ──────────────────────────────────────
        btnLogin = UITheme.makePrimaryButton("Sign In →");
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogin.addActionListener(e -> doLogin());

        // Enter key triggers login
        getRootPane().setDefaultButton(null);
        pfPassword.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) doLogin();
            }
        });
        tfUsername.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) pfPassword.requestFocus();
            }
        });

        // ── Info label ────────────────────────────────────────
        JLabel hint = new JLabel("Default: admin / Admin@123");
        hint.setFont(UITheme.FONT_SMALL);
        hint.setForeground(new Color(0x4A6080));
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ── Assemble form ─────────────────────────────────────
        form.add(closeRow);
        form.add(Box.createVerticalStrut(4));
        form.add(heading);
        form.add(Box.createVerticalStrut(4));
        form.add(subhead);
        form.add(Box.createVerticalStrut(24));
        form.add(lblUser);
        form.add(Box.createVerticalStrut(6));
        form.add(tfUsername);
        form.add(Box.createVerticalStrut(16));
        form.add(lblPass);
        form.add(Box.createVerticalStrut(6));
        form.add(pfPassword);
        form.add(Box.createVerticalStrut(8));
        form.add(cbShowPass);
        form.add(Box.createVerticalStrut(6));
        form.add(lblError);
        form.add(Box.createVerticalStrut(16));
        form.add(btnLogin);
        form.add(Box.createVerticalStrut(16));
        form.add(hint);

        panel.add(form);
        return panel;
    }

    private void doLogin() {
        String username = tfUsername.getText().trim();
        String password = new String(pfPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("Please enter username and password.");
            return;
        }

        if (authService == null) {
            try {
                authService = new AuthService();
            } catch (SQLException e) {
                lblError.setText("DB offline: " + e.getMessage());
                return;
            }
        }

        btnLogin.setEnabled(false);
        lblError.setText("Signing in...");

        SwingWorker<User, Void> worker = new SwingWorker<>() {
            @Override
            protected User doInBackground() throws Exception {
                return authService.login(username, password);
            }

            @Override
            protected void done() {
                try {
                    User user = get();
                    if (user == null) {
                        lblError.setText("Invalid credentials. Please try again.");
                        pfPassword.setText("");
                        pfPassword.requestFocus();
                    } else {
                        lblError.setText(" ");
                        openDashboard(user);
                    }
                } catch (Exception ex) {
                    lblError.setText("Login error: " + ex.getMessage());
                }
                btnLogin.setEnabled(true);
            }
        };
        worker.execute();
    }

    private void openDashboard(User user) {
        dispose();
        SwingUtilities.invokeLater(() -> {
            if (user.isAdmin()) {
                new AdminDashboard().setVisible(true);
            } else {
                new StudentDashboard(user.getStudentId()).setVisible(true);
            }
        });
    }

    // ── Drag to move ──────────────────────────────────────────
    private class DragListener extends MouseAdapter {
        private Point offset;
        @Override public void mousePressed(MouseEvent e)  { offset = e.getPoint(); }
        @Override public void mouseDragged(MouseEvent e)  {
            if (offset != null) {
                Point pos = e.getLocationOnScreen();
                setLocation(pos.x - offset.x, pos.y - offset.y);
            }
        }
    }
}
