package com.sms.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility for SHA-256 password hashing.
 * Passwords are NEVER stored or transmitted in plain text.
 */
public class PasswordUtils {

    private PasswordUtils() {}

    /**
     * Hashes a plain-text password using SHA-256.
     * Returns the lowercase hex representation of the digest.
     */
    public static String hash(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            throw new IllegalArgumentException("Password must not be empty");
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(plainText.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Verifies a plain-text password against a stored hash.
     */
    public static boolean verify(String plainText, String storedHash) {
        return hash(plainText).equals(storedHash);
    }

    /** Alias for hash() */
    public static String hashPassword(String plainText) {
        return hash(plainText);
    }

    /** Alias for verify() */
    public static boolean verifyPassword(String plainText, String storedHash) {
        return verify(plainText, storedHash);
    }
}
