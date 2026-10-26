package com.sms.service;

import com.sms.dao.UserDAO;
import com.sms.model.User;
import com.sms.util.PasswordUtils;
import com.sms.util.SessionManager;

import java.sql.SQLException;

/**
 * Handles authentication business logic.
 */
public class AuthService {

    private final UserDAO userDAO;

    public AuthService() throws SQLException {
        this.userDAO = new UserDAO();
    }

    /**
     * Authenticates the user. On success, stores the user in SessionManager
     * and returns the User object. Returns null on failure.
     *
     * @param username the username
     * @param password the plain-text password
     * @return the authenticated User, or null on failure
     */
    public User login(String username, String password) throws SQLException {
        if (username == null || username.isBlank() || password == null || password.isEmpty()) {
            return null;
        }
        User user = userDAO.findByUsername(username.trim());
        if (user == null || !user.isActive()) {
            return null;
        }
        if (!PasswordUtils.verify(password, user.getPasswordHash())) {
            return null;
        }
        SessionManager.getInstance().login(user);
        return user;
    }

    /**
     * Logs out the current session.
     */
    public void logout() {
        SessionManager.getInstance().logout();
    }

    /**
     * Changes password for a given user.
     */
    public boolean changePassword(int userId, String currentPassword,
                                   String newPassword) throws SQLException {
        User user = userDAO.findByUsername(
                SessionManager.getInstance().getCurrentUser().getUsername());
        if (user == null) return false;
        if (!PasswordUtils.verify(currentPassword, user.getPasswordHash())) return false;
        String newHash = PasswordUtils.hash(newPassword);
        return userDAO.updatePassword(userId, newHash);
    }
}
