package com.sms.ui.util;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * Central design system for the SMS application.
 * All colors, fonts, and component factories are defined here.
 */
public class UITheme {

    // ── Color Palette ─────────────────────────────────────────
    public static final Color BG_DARK       = new Color(0x0F1B2D);  // deep navy
    public static final Color BG_CARD       = new Color(0x1A2942);  // card navy
    public static final Color BG_SIDEBAR    = new Color(0x0D1A2E);  // sidebar
    public static final Color BG_INPUT      = new Color(0x243452);  // input bg
    public static final Color ACCENT_TEAL   = new Color(0x00C8A0);  // primary accent
    public static final Color ACCENT_GOLD   = new Color(0xF5A623);  // secondary accent
    public static final Color ACCENT_DANGER = new Color(0xFF5252);  // danger/delete
    public static final Color TEXT_PRIMARY  = new Color(0xF0F4FF);  // white-ish
    public static final Color TEXT_SECONDARY= new Color(0x8FA3C0);  // muted
    public static final Color BORDER_COLOR  = new Color(0x2A3F5F);  // borders
    public static final Color ROW_ALT       = new Color(0x1F3351);  // alt table row
    public static final Color HOVER_COLOR   = new Color(0x1E3D6E);  // hover

    // ── Fonts ─────────────────────────────────────────────────
    public static final Font FONT_TITLE    = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_HEADER   = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_BODY     = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL    = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BUTTON   = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_MONO     = new Font("Consolas", Font.PLAIN, 13);

    // ── Borders ───────────────────────────────────────────────
    public static Border cardBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(16, 20, 16, 20));
    }

    public static Border inputBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(6, 10, 6, 10));
    }

    public static Border sectionBorder(String title) {
        return BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ACCENT_TEAL, 1, true),
                " " + title + " ",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                FONT_HEADER, ACCENT_TEAL);
    }

    // ── Component Factories ───────────────────────────────────

    /** Standard form label */
    public static JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_BODY);
        lbl.setForeground(TEXT_SECONDARY);
        return lbl;
    }

    /** Subtitle / section heading label */
    public static JLabel makeSubtitle(String text) {
        return makeHeading(text);
    }

    /** Section heading label */
    public static JLabel makeHeading(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_HEADER);
        lbl.setForeground(TEXT_PRIMARY);
        return lbl;
    }

    /** Title label */
    public static JLabel makeTitle(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_TITLE);
        lbl.setForeground(TEXT_PRIMARY);
        return lbl;
    }

    /** Styled text field */
    public static JTextField makeTextField(int columns) {
        JTextField tf = new JTextField(columns);
        tf.setFont(FONT_BODY);
        tf.setForeground(TEXT_PRIMARY);
        tf.setBackground(BG_INPUT);
        tf.setCaretColor(ACCENT_TEAL);
        tf.setBorder(inputBorder());
        return tf;
    }

    /** Styled text field with initial text */
    public static JTextField makeTextField(String initialText, int columns) {
        JTextField tf = makeTextField(columns);
        tf.setText(initialText != null ? initialText : "");
        return tf;
    }

    /** Styled password field */
    public static JPasswordField makePasswordField(int columns) {
        JPasswordField pf = new JPasswordField(columns);
        pf.setFont(FONT_BODY);
        pf.setForeground(TEXT_PRIMARY);
        pf.setBackground(BG_INPUT);
        pf.setCaretColor(ACCENT_TEAL);
        pf.setBorder(inputBorder());
        return pf;
    }

    /** Styled text area */
    public static JTextArea makeTextArea(int rows, int cols) {
        JTextArea ta = new JTextArea(rows, cols);
        ta.setFont(FONT_BODY);
        ta.setForeground(TEXT_PRIMARY);
        ta.setBackground(BG_INPUT);
        ta.setCaretColor(ACCENT_TEAL);
        ta.setBorder(new EmptyBorder(8, 10, 8, 10));
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        return ta;
    }

    /** Styled combo box */
    public static <T> JComboBox<T> makeComboBox() {
        JComboBox<T> cb = new JComboBox<>();
        cb.setFont(FONT_BODY);
        cb.setForeground(TEXT_PRIMARY);
        cb.setBackground(BG_INPUT);
        cb.setBorder(inputBorder());
        return cb;
    }

    /** Primary action button (teal) */
    public static JButton makePrimaryButton(String text) {
        return makeButton(text, ACCENT_TEAL, BG_DARK);
    }

    /** Warning/danger button (red) */
    public static JButton makeDangerButton(String text) {
        return makeButton(text, ACCENT_DANGER, Color.WHITE);
    }

    /** Secondary button (outlined) */
    public static JButton makeSecondaryButton(String text) {
        JButton btn = makeButton(text, BG_CARD, TEXT_SECONDARY);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(8, 18, 8, 18)));
        return btn;
    }

    /** Gold/accent button */
    public static JButton makeGoldButton(String text) {
        return makeButton(text, ACCENT_GOLD, BG_DARK);
    }

    private static JButton makeButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? bg.darker() :
                            getModel().isRollover() ? bg.brighter() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BUTTON);
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(9, 20, 9, 20));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /** Styled JTable with alternating rows */
    public static void styleTable(JTable table) {
        table.setFont(FONT_BODY);
        table.setForeground(TEXT_PRIMARY);
        table.setBackground(BG_CARD);
        table.setSelectionBackground(HOVER_COLOR);
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setRowHeight(32);
        table.setGridColor(BORDER_COLOR);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object val,
                    boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(tbl, val, sel, foc, row, col);
                if (!sel) {
                    c.setBackground(row % 2 == 0 ? BG_CARD : ROW_ALT);
                    c.setForeground(TEXT_PRIMARY);
                }
                setBorder(new EmptyBorder(0, 10, 0, 10));
                return c;
            }
        });
        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_HEADER);
        header.setForeground(ACCENT_TEAL);
        header.setBackground(BG_SIDEBAR);
        header.setReorderingAllowed(false);
        ((DefaultTableCellRenderer) header.getDefaultRenderer())
                .setHorizontalAlignment(SwingConstants.LEFT);
    }

    /** Styled scroll pane */
    public static JScrollPane makeScrollPane(Component view) {
        JScrollPane sp = new JScrollPane(view);
        sp.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        sp.getViewport().setBackground(BG_CARD);
        sp.setBackground(BG_CARD);
        return sp;
    }

    /** Styled panel with card look */
    public static JPanel makeCard() {
        JPanel p = new JPanel();
        p.setBackground(BG_CARD);
        p.setBorder(cardBorder());
        return p;
    }

    /** Apply dark background to any panel */
    public static void darkPanel(JPanel p) {
        p.setBackground(BG_DARK);
    }
}
