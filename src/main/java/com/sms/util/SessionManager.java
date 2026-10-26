package com.sms.util;

import com.sms.model.User;

/**
 * Singleton that holds the currently authenticated user for the session.
 * Cleared on logout.
 */
public class SessionManager {

    private static SessionManager instance;
    private User currentUser;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void login(User user) {
        this.currentUser = user;
    }

    public void logout() {
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean isAdmin() {
        return isLoggedIn() && currentUser.isAdmin();
    }

    public boolean isStudent() {
        return isLoggedIn() && "STUDENT".equals(currentUser.getRole());
    }

    /** Convenience — returns the logged-in student's ID or -1 if not a student. */
    public int getStudentId() {
        if (isStudent() && currentUser.getStudentId() != null) {
            return currentUser.getStudentId();
        }
        return -1;
    }
}
