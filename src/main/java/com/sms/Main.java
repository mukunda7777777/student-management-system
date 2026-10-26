package com.sms;

import com.sms.ui.LoginFrame;

import javax.swing.*;

/**
 * Main application entry point for the Student Management System (SMS).
 */
public class Main {

    public static void main(String[] args) {
        // Set cross-platform look and feel or system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Launch UI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
