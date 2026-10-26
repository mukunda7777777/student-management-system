package com.sms.ui.notice;

import com.sms.model.Notice;
import com.sms.service.NoticeService;
import com.sms.ui.util.UITheme;
import com.sms.util.SessionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Notice Board Management Panel for Administrator.
 */
public class NoticeManagementPanel extends JPanel {

    private final NoticeService noticeService;

    private DefaultTableModel tableModel;
    private JTable table;
    private List<Notice> currentList;
    private JTextArea taPreview;
    private JCheckBox chkActiveOnly;

    private static final String[] COLS = {"ID", "Title", "Posted By", "Date & Time", "Status"};

    public NoticeManagementPanel() throws SQLException {
        this.noticeService = new NoticeService();

        setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout(0, 12));
        setBorder(new EmptyBorder(0, 0, 0, 0));

        add(buildTopBar(), BorderLayout.NORTH);
        add(buildSplitContent(), BorderLayout.CENTER);
        add(buildBottomBar(), BorderLayout.SOUTH);

        loadNotices();
    }

    private JPanel buildTopBar() {
        JPanel container = new JPanel(new BorderLayout(0, 8));
        container.setBackground(UITheme.BG_DARK);

        JLabel title = UITheme.makeTitle("Campus Notice Board Management");
        container.add(title, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setBackground(UITheme.BG_DARK);

        chkActiveOnly = new JCheckBox("Active Notices Only");
        chkActiveOnly.setForeground(UITheme.TEXT_SECONDARY);
        chkActiveOnly.setBackground(UITheme.BG_DARK);
        chkActiveOnly.addActionListener(e -> loadNotices());

        JButton btnPost = UITheme.makePrimaryButton("+ Post Notice");
        btnPost.addActionListener(e -> openNoticeDialog(null));

        JButton btnRefresh = UITheme.makeSecondaryButton("⟳ Refresh");
        btnRefresh.addActionListener(e -> loadNotices());

        right.add(chkActiveOnly);
        right.add(btnPost);
        right.add(btnRefresh);

        container.add(right, BorderLayout.EAST);
        return container;
    }

    private JSplitPane buildSplitContent() {
        // Top/Left: Table
        tableModel = new DefaultTableModel(COLS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UITheme.styleTable(table);
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(260);
        table.getColumnModel().getColumn(2).setPreferredWidth(120);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row >= 0 && row < currentList.size()) {
                    Notice n = currentList.get(row);
                    taPreview.setText("📢 " + n.getTitle() + "\n" +
                        "Posted By: " + n.getPostedBy() + "  |  Date: " +
                        (n.getPostedDate() != null ? n.getPostedDate().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm")) : "-") +
                        "\n" + "─".repeat(60) + "\n\n" +
                        n.getContent());
                }
            }
        });

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(UITheme.BG_DARK);
        tablePanel.add(UITheme.makeScrollPane(table), BorderLayout.CENTER);

        // Preview Panel
        JPanel previewPanel = new JPanel(new BorderLayout(0, 6));
        previewPanel.setBackground(UITheme.BG_DARK);
        previewPanel.setBorder(new EmptyBorder(4, 0, 0, 0));

        JLabel lblPreview = UITheme.makeSubtitle("Notice Preview");
        previewPanel.add(lblPreview, BorderLayout.NORTH);

        taPreview = new JTextArea();
        taPreview.setEditable(false);
        taPreview.setBackground(UITheme.BG_CARD);
        taPreview.setForeground(UITheme.TEXT_PRIMARY);
        taPreview.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        taPreview.setLineWrap(true);
        taPreview.setWrapStyleWord(true);
        taPreview.setBorder(new EmptyBorder(12, 14, 12, 14));

        previewPanel.add(new JScrollPane(taPreview), BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tablePanel, previewPanel);
        split.setResizeWeight(0.6);
        split.setDividerSize(6);
        split.setBackground(UITheme.BG_DARK);
        return split;
    }

    private JPanel buildBottomBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        bar.setBackground(UITheme.BG_CARD);

        JButton btnEdit = UITheme.makeSecondaryButton("✏ Edit Notice");
        JButton btnDelete = UITheme.makeDangerButton("🗑 Delete Notice");

        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0 || row >= currentList.size()) {
                JOptionPane.showMessageDialog(this, "Please select a notice to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            openNoticeDialog(currentList.get(row));
        });

        btnDelete.addActionListener(e -> deleteSelectedNotice());

        bar.add(btnEdit);
        bar.add(btnDelete);
        return bar;
    }

    private void loadNotices() {
        try {
            if (chkActiveOnly.isSelected()) {
                currentList = noticeService.getActiveNotices();
            } else {
                currentList = noticeService.getAllNotices();
            }

            tableModel.setRowCount(0);
            for (Notice n : currentList) {
                String dateStr = n.getPostedDate() != null ?
                    n.getPostedDate().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm")) : "-";
                String statusStr = n.isActive() ? "Active" : "Archived";
                tableModel.addRow(new Object[]{n.getId(), n.getTitle(), n.getPostedBy(), dateStr, statusStr});
            }

            if (!currentList.isEmpty()) {
                table.setRowSelectionInterval(0, 0);
            } else {
                taPreview.setText("No notices to display.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading notices: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedNotice() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= currentList.size()) {
            JOptionPane.showMessageDialog(this, "Please select a notice to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Notice n = currentList.get(row);
        int opt = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete notice '" + n.getTitle() + "'?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (opt == JOptionPane.YES_OPTION) {
            try {
                boolean ok = noticeService.deleteNotice(n.getId());
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Notice deleted successfully.");
                    loadNotices();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete notice.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openNoticeDialog(Notice existing) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            existing == null ? "Post New Notice" : "Edit Notice", true);
        dialog.getContentPane().setBackground(UITheme.BG_DARK);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UITheme.BG_CARD);
        form.setBorder(new EmptyBorder(20, 24, 20, 24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        JTextField tfTitle = UITheme.makeTextField(existing != null ? existing.getTitle() : "", 25);
        JTextArea taContent = new JTextArea(existing != null ? existing.getContent() : "", 6, 25);
        taContent.setBackground(UITheme.BG_INPUT);
        taContent.setForeground(UITheme.TEXT_PRIMARY);
        taContent.setCaretColor(Color.WHITE);
        taContent.setLineWrap(true);
        taContent.setWrapStyleWord(true);
        taContent.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));

        JCheckBox chkActive = new JCheckBox("Active / Visible to students", existing == null || existing.isActive());
        chkActive.setForeground(UITheme.TEXT_PRIMARY);
        chkActive.setBackground(UITheme.BG_CARD);

        gbc.gridx = 0; gbc.gridy = 0;
        form.add(UITheme.makeLabel("Title: *"), gbc);
        gbc.gridx = 1;
        form.add(tfTitle, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        form.add(UITheme.makeLabel("Content: *"), gbc);
        gbc.gridx = 1;
        form.add(new JScrollPane(taContent), gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        form.add(chkActive, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnPanel.setBackground(UITheme.BG_CARD);

        JButton btnSave = UITheme.makePrimaryButton("Publish");
        JButton btnCancel = UITheme.makeSecondaryButton("Cancel");

        btnCancel.addActionListener(e -> dialog.dispose());
        btnSave.addActionListener(e -> {
            try {
                String title = tfTitle.getText().trim();
                String content = taContent.getText().trim();
                String poster = SessionManager.getInstance().getCurrentUser() != null ?
                    SessionManager.getInstance().getCurrentUser().getUsername() : "Admin";

                if (existing == null) {
                    Notice n = new Notice(0, title, content, poster, null, chkActive.isSelected());
                    String err = noticeService.postNotice(n);
                    if (err != null) {
                        JOptionPane.showMessageDialog(dialog, err, "Validation", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                } else {
                    existing.setTitle(title);
                    existing.setContent(content);
                    existing.setActive(chkActive.isSelected());
                    String err = noticeService.updateNotice(existing);
                    if (err != null) {
                        JOptionPane.showMessageDialog(dialog, err, "Validation", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }

                dialog.dispose();
                loadNotices();
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
