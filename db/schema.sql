-- ============================================================
--  Student Management System — Database Schema (MySQL 8.0)
--  Version 1.0
-- ============================================================

CREATE DATABASE IF NOT EXISTS sms_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE sms_db;

-- ============================================================
-- TABLE: users
-- Stores login credentials for Admin and Student roles
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    username     VARCHAR(50)  NOT NULL UNIQUE,
    password_hash VARCHAR(64) NOT NULL,          -- SHA-256 hex
    role         ENUM('ADMIN', 'STUDENT') NOT NULL DEFAULT 'STUDENT',
    student_id   INT          NULL,              -- FK populated for STUDENT role
    active       TINYINT(1)   NOT NULL DEFAULT 1,
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- TABLE: courses
-- ============================================================
CREATE TABLE IF NOT EXISTS courses (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    duration    VARCHAR(20)  NOT NULL,            -- e.g. "3 Years"
    description TEXT         NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- TABLE: subjects
-- ============================================================
CREATE TABLE IF NOT EXISTS subjects (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    course_id   INT          NOT NULL,
    name        VARCHAR(100) NOT NULL,
    max_marks   INT          NOT NULL DEFAULT 100,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sub_course FOREIGN KEY (course_id)
        REFERENCES courses(id) ON DELETE CASCADE
);

-- ============================================================
-- TABLE: students
-- ============================================================
CREATE TABLE IF NOT EXISTS students (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    roll_no      VARCHAR(20)  NOT NULL UNIQUE,
    first_name   VARCHAR(50)  NOT NULL,
    last_name    VARCHAR(50)  NOT NULL,
    dob          DATE         NULL,
    gender       ENUM('Male','Female','Other') NOT NULL DEFAULT 'Male',
    email        VARCHAR(100) NULL UNIQUE,
    phone        VARCHAR(15)  NULL,
    address      TEXT         NULL,
    course_id    INT          NULL,
    photo_path   VARCHAR(255) NULL,
    admission_date DATE       NOT NULL DEFAULT (CURDATE()),
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_student_course FOREIGN KEY (course_id)
        REFERENCES courses(id) ON DELETE SET NULL
);

-- ============================================================
-- TABLE: attendance
-- ============================================================
CREATE TABLE IF NOT EXISTS attendance (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    student_id  INT          NOT NULL,
    subject_id  INT          NOT NULL,
    att_date    DATE         NOT NULL,
    status      ENUM('P','A') NOT NULL DEFAULT 'A',
    CONSTRAINT fk_att_student FOREIGN KEY (student_id)
        REFERENCES students(id) ON DELETE CASCADE,
    CONSTRAINT fk_att_subject FOREIGN KEY (subject_id)
        REFERENCES subjects(id) ON DELETE CASCADE,
    CONSTRAINT uq_attendance UNIQUE (student_id, subject_id, att_date)
);

-- ============================================================
-- TABLE: marks
-- ============================================================
CREATE TABLE IF NOT EXISTS marks (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    student_id      INT          NOT NULL,
    subject_id      INT          NOT NULL,
    exam_type       VARCHAR(50)  NOT NULL DEFAULT 'Final',  -- Mid-Term / Final / Internal
    marks_obtained  DECIMAL(6,2) NOT NULL DEFAULT 0,
    max_marks       INT          NOT NULL DEFAULT 100,
    exam_date       DATE         NULL,
    CONSTRAINT fk_marks_student FOREIGN KEY (student_id)
        REFERENCES students(id) ON DELETE CASCADE,
    CONSTRAINT fk_marks_subject FOREIGN KEY (subject_id)
        REFERENCES subjects(id) ON DELETE CASCADE,
    CONSTRAINT uq_marks UNIQUE (student_id, subject_id, exam_type)
);

-- ============================================================
-- TABLE: fees
-- ============================================================
CREATE TABLE IF NOT EXISTS fees (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    student_id  INT            NOT NULL,
    receipt_no  VARCHAR(30)    NOT NULL UNIQUE,
    amount_paid DECIMAL(10,2)  NOT NULL DEFAULT 0.00,
    due_amount  DECIMAL(10,2)  NOT NULL DEFAULT 0.00,
    paid_date   DATE           NOT NULL DEFAULT (CURDATE()),
    fee_type    VARCHAR(50)    NULL,              -- Tuition / Hostel / Exam / etc.
    remarks     TEXT           NULL,
    CONSTRAINT fk_fee_student FOREIGN KEY (student_id)
        REFERENCES students(id) ON DELETE CASCADE
);

-- ============================================================
-- TABLE: notices
-- ============================================================
CREATE TABLE IF NOT EXISTS notices (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(200) NOT NULL,
    content     TEXT         NOT NULL,
    posted_by   VARCHAR(50)  NOT NULL DEFAULT 'Admin',
    posted_date TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    active      TINYINT(1)   NOT NULL DEFAULT 1
);

-- ============================================================
-- INDEXES for performance
-- ============================================================
CREATE INDEX idx_att_student   ON attendance(student_id);
CREATE INDEX idx_att_date      ON attendance(att_date);
CREATE INDEX idx_marks_student ON marks(student_id);
CREATE INDEX idx_fee_student   ON fees(student_id);
CREATE INDEX idx_student_roll  ON students(roll_no);
CREATE INDEX idx_student_name  ON students(first_name, last_name);

-- ============================================================
-- DEFAULT ADMIN USER
-- username: admin  password: Admin@123
-- SHA-256 of "Admin@123" = e3afed0047b08059d0fada10f400c1e5...
-- (computed value inserted in sample_data.sql)
-- ============================================================
