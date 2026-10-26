package com.sms.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * Input validation utilities used across all service classes.
 */
public class ValidationUtils {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^[6-9]\\d{9}$");  // Indian mobile numbers

    private ValidationUtils() {}

    /** Returns true if the string is null or blank. */
    public static boolean isNullOrBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    /** Validates non-blank required field. */
    public static String requireNonBlank(String value, String fieldName) {
        if (isNullOrBlank(value)) {
            return fieldName + " is required.";
        }
        return null;
    }

    /** Validates email format. */
    public static String validateEmail(String email) {
        if (isNullOrBlank(email)) return null; // optional
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            return "Invalid email address.";
        }
        return null;
    }

    /** Validates Indian phone number (10 digits starting with 6-9). */
    public static String validatePhone(String phone) {
        if (isNullOrBlank(phone)) return null; // optional
        String cleaned = phone.replaceAll("[\\s\\-]", "");
        if (!PHONE_PATTERN.matcher(cleaned).matches()) {
            return "Phone must be a 10-digit Indian mobile number.";
        }
        return null;
    }

    /** Validates that a date string parses as yyyy-MM-dd and is not in the future. */
    public static String validateDate(String dateStr, String fieldName, boolean allowFuture) {
        if (isNullOrBlank(dateStr)) return null;
        try {
            LocalDate d = LocalDate.parse(dateStr.trim(), DateTimeFormatter.ISO_LOCAL_DATE);
            if (!allowFuture && d.isAfter(LocalDate.now())) {
                return fieldName + " cannot be a future date.";
            }
            return null;
        } catch (DateTimeParseException e) {
            return fieldName + " must be in yyyy-MM-dd format.";
        }
    }

    /** Validates marks are within range. */
    public static String validateMarks(double obtained, int max) {
        if (obtained < 0) return "Marks cannot be negative.";
        if (obtained > max) return "Marks obtained cannot exceed maximum marks (" + max + ").";
        return null;
    }

    /** Validates fee amount is positive. */
    public static String validateAmount(double amount) {
        if (amount < 0) return "Amount cannot be negative.";
        return null;
    }

    /** Validates that a roll number follows expected pattern. */
    public static String validateRollNo(String rollNo) {
        if (isNullOrBlank(rollNo)) return "Roll No. is required.";
        if (rollNo.trim().length() < 3 || rollNo.trim().length() > 20) {
            return "Roll No. must be between 3 and 20 characters.";
        }
        return null;
    }
}
