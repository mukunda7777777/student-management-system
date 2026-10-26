package com.sms.model;

/**
 * Represents a system user (Admin or Student).
 */
public class User {
    private int id;
    private String username;
    private String passwordHash;
    private String role;        // "ADMIN" or "STUDENT"
    private Integer studentId;  // null for Admin
    private boolean active;

    public User() {}

    public User(int id, String username, String passwordHash, String role, Integer studentId, boolean active) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.studentId = studentId;
        this.active = active;
    }

    // ── Getters & Setters ──────────────────────────────────────
    public int getId()                    { return id; }
    public void setId(int id)             { this.id = id; }

    public String getUsername()           { return username; }
    public void setUsername(String u)     { this.username = u; }

    public String getPasswordHash()       { return passwordHash; }
    public void setPasswordHash(String h) { this.passwordHash = h; }

    public String getRole()               { return role; }
    public void setRole(String r)         { this.role = r; }

    public Integer getStudentId()         { return studentId; }
    public void setStudentId(Integer sid) { this.studentId = sid; }

    public boolean isActive()             { return active; }
    public void setActive(boolean a)      { this.active = a; }

    public boolean isAdmin()              { return "ADMIN".equals(role); }

    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + username + "', role='" + role + "'}";
    }
}
