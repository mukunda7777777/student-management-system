package com.sms.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Utility to automatically initialize the MySQL database schema and sample data.
 * Can be executed via setup_database.bat or called programmatically.
 */
public class DBInitializer {

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("  SMS - MySQL Database Automated Setup Utility  ");
        System.out.println("=================================================\n");

        boolean success = initializeDatabase();
        if (success) {
            System.out.println("\n[SUCCESS] Database 'sms_db' created and initialized with sample data!");
            System.out.println("You can now run 'run.bat' to start the application.");
        } else {
            System.err.println("\n[FAILED] Could not connect to MySQL Server.");
            System.err.println("Please check:");
            System.err.println(" 1. Is MySQL Server / XAMPP running on localhost:3306?");
            System.err.println(" 2. Are the username and password in src/main/resources/db.properties correct?");
        }
    }

    public static boolean initializeDatabase() {
        Properties props = new Properties();
        try (InputStream is = DBInitializer.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (is != null) props.load(is);
        } catch (IOException ignored) {}

        String user = props.getProperty("db.username", "root");
        String pass = props.getProperty("db.password", "root");
        // Server URL without database name so we can create it
        String serverUrl = "jdbc:mysql://localhost:3306/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Kolkata";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL Driver not found: " + e.getMessage());
            return false;
        }

        System.out.println("Connecting to MySQL at localhost:3306 (user: " + user + ")...");
        try (Connection conn = DriverManager.getConnection(serverUrl, user, pass);
             Statement stmt = conn.createStatement()) {

            System.out.println("Executing db/schema.sql...");
            executeSqlFile(stmt, new File("db/schema.sql"));

            System.out.println("Executing db/sample_data.sql...");
            executeSqlFile(stmt, new File("db/sample_data.sql"));

            return true;
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());
            // Try with blank password (common default for XAMPP / WAMP)
            if (!pass.isEmpty()) {
                System.out.println("Trying alternative connection with empty password (XAMPP default)...");
                try (Connection conn = DriverManager.getConnection(serverUrl, user, "");
                     Statement stmt = conn.createStatement()) {
                    executeSqlFile(stmt, new File("db/schema.sql"));
                    executeSqlFile(stmt, new File("db/sample_data.sql"));
                    System.out.println("Connected with empty password! Updating db.properties...");
                    updateDbPassword("");
                    return true;
                } catch (SQLException ignored) {}
            }
            return false;
        }
    }

    private static void executeSqlFile(Statement stmt, File file) {
        if (!file.exists()) {
            System.err.println("File not found: " + file.getAbsolutePath());
            return;
        }

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                // Skip empty lines and comments
                if (trimmed.isEmpty() || trimmed.startsWith("--") || trimmed.startsWith("/*")) {
                    continue;
                }
                sb.append(line).append("\n");
                if (trimmed.endsWith(";")) {
                    String sql = sb.toString().trim();
                    // Remove trailing semicolon
                    if (sql.endsWith(";")) sql = sql.substring(0, sql.length() - 1).trim();
                    if (!sql.isEmpty()) {
                        try {
                            stmt.execute(sql);
                        } catch (SQLException e) {
                            // Ignore harmless warnings like table exists
                            if (!e.getMessage().contains("already exists")) {
                                System.err.println("Note: " + e.getMessage());
                            }
                        }
                    }
                    sb.setLength(0);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading " + file.getName() + ": " + e.getMessage());
        }
    }

    private static void updateDbPassword(String newPass) {
        File propFile = new File("src/main/resources/db.properties");
        if (propFile.exists()) {
            try {
                String content = new String(java.nio.file.Files.readAllBytes(propFile.toPath()), StandardCharsets.UTF_8);
                content = content.replaceAll("db\\.password=.*", "db.password=" + newPass);
                java.nio.file.Files.write(propFile.toPath(), content.getBytes(StandardCharsets.UTF_8));
                // Also update target/classes copy
                File targetProp = new File("target/classes/db.properties");
                if (targetProp.exists()) {
                    java.nio.file.Files.write(targetProp.toPath(), content.getBytes(StandardCharsets.UTF_8));
                }
            } catch (Exception ignored) {}
        }
    }
}
