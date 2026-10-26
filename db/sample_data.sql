-- ============================================================
--  Student Management System — Sample / Seed Data
--  Run AFTER schema.sql
-- ============================================================

USE sms_db;

-- ============================================================
-- ADMIN USER
-- username: admin  |  password: Admin@123
-- SHA-256("Admin@123") = 2ef7bde608ce5404e97d5f042f95f89f1c232871
-- We use SHA2 256 in Java; value below is correct hex
-- ============================================================
INSERT IGNORE INTO users (username, password_hash, role, student_id)
VALUES ('admin',
        SHA2('Admin@123', 256),
        'ADMIN',
        NULL);

-- ============================================================
-- COURSES
-- ============================================================
INSERT IGNORE INTO courses (id, name, duration, description) VALUES
(1, 'B.Tech Computer Science',       '4 Years', 'Bachelor of Technology in Computer Science and Engineering'),
(2, 'B.Tech Electronics',             '4 Years', 'Bachelor of Technology in Electronics and Communication'),
(3, 'BCA',                           '3 Years', 'Bachelor of Computer Applications'),
(4, 'MCA',                           '2 Years', 'Master of Computer Applications'),
(5, 'B.Sc Computer Science',          '3 Years', 'Bachelor of Science in Computer Science');

-- ============================================================
-- SUBJECTS
-- ============================================================
-- B.Tech CSE (course_id=1)
INSERT IGNORE INTO subjects (course_id, name, max_marks) VALUES
(1, 'Mathematics I',       100),
(1, 'Physics',             100),
(1, 'Programming in C',   100),
(1, 'Data Structures',    100),
(1, 'Database Systems',   100),
(1, 'Operating Systems',  100),
(1, 'Computer Networks',  100),
(1, 'Software Engineering',100);

-- BCA (course_id=3)
INSERT IGNORE INTO subjects (course_id, name, max_marks) VALUES
(3, 'Mathematics',         100),
(3, 'C Programming',      100),
(3, 'Web Technology',     100),
(3, 'Java Programming',   100),
(3, 'DBMS',               100);

-- ============================================================
-- STUDENTS
-- ============================================================
INSERT IGNORE INTO students (roll_no, first_name, last_name, dob, gender, email, phone, address, course_id, admission_date)
VALUES
('CS2024001', 'Priya',   'Sharma',   '2005-03-15', 'Female', 'priya.sharma@email.com',   '9876543210', 'Bhubaneswar, Odisha',  1, '2024-07-01'),
('CS2024002', 'Rahul',   'Verma',    '2005-06-22', 'Male',   'rahul.verma@email.com',    '9876543211', 'Cuttack, Odisha',      1, '2024-07-01'),
('CS2024003', 'Sneha',   'Patel',    '2004-11-08', 'Female', 'sneha.patel@email.com',    '9876543212', 'Rourkela, Odisha',     1, '2024-07-01'),
('CS2024004', 'Aditya',  'Kumar',    '2005-01-30', 'Male',   'aditya.kumar@email.com',   '9876543213', 'Puri, Odisha',         1, '2024-07-01'),
('CS2024005', 'Anjali',  'Singh',    '2005-09-14', 'Female', 'anjali.singh@email.com',   '9876543214', 'Berhampur, Odisha',    1, '2024-07-01'),
('BCA2024001','Rohan',   'Das',      '2006-02-18', 'Male',   'rohan.das@email.com',      '9876543215', 'Bhubaneswar, Odisha',  3, '2024-07-05'),
('BCA2024002','Pooja',   'Nayak',    '2006-07-25', 'Female', 'pooja.nayak@email.com',    '9876543216', 'Kendrapara, Odisha',   3, '2024-07-05'),
('BCA2024003','Arjun',   'Mishra',   '2005-12-03', 'Male',   'arjun.mishra@email.com',   '9876543217', 'Sambalpur, Odisha',    3, '2024-07-05');

-- ============================================================
-- STUDENT USER ACCOUNTS
-- Default password for all students: Student@123
-- ============================================================
INSERT IGNORE INTO users (username, password_hash, role, student_id)
SELECT s.roll_no, SHA2('Student@123', 256), 'STUDENT', s.id
FROM students s;

-- ============================================================
-- ATTENDANCE (sample for student id=1, subject_ids 1-4)
-- ============================================================
INSERT IGNORE INTO attendance (student_id, subject_id, att_date, status) VALUES
(1, 1, '2024-08-01', 'P'), (1, 1, '2024-08-02', 'P'), (1, 1, '2024-08-05', 'A'),
(1, 1, '2024-08-06', 'P'), (1, 1, '2024-08-07', 'P'), (1, 1, '2024-08-08', 'P'),
(1, 2, '2024-08-01', 'P'), (1, 2, '2024-08-02', 'A'), (1, 2, '2024-08-05', 'P'),
(1, 2, '2024-08-06', 'P'), (1, 2, '2024-08-07', 'P'), (1, 2, '2024-08-08', 'A'),
(2, 1, '2024-08-01', 'P'), (2, 1, '2024-08-02', 'P'), (2, 1, '2024-08-05', 'P'),
(2, 1, '2024-08-06', 'A'), (2, 1, '2024-08-07', 'P'), (2, 1, '2024-08-08', 'P'),
(2, 2, '2024-08-01', 'A'), (2, 2, '2024-08-02', 'P'), (2, 2, '2024-08-05', 'P'),
(2, 2, '2024-08-06', 'P'), (2, 2, '2024-08-07', 'P'), (2, 2, '2024-08-08', 'P');

-- ============================================================
-- MARKS (sample)
-- ============================================================
INSERT IGNORE INTO marks (student_id, subject_id, exam_type, marks_obtained, max_marks, exam_date) VALUES
(1, 1, 'Mid-Term', 42, 50,  '2024-10-15'),
(1, 2, 'Mid-Term', 38, 50,  '2024-10-16'),
(1, 3, 'Mid-Term', 45, 50,  '2024-10-17'),
(1, 1, 'Final',    82, 100, '2025-01-10'),
(1, 2, 'Final',    75, 100, '2025-01-11'),
(1, 3, 'Final',    90, 100, '2025-01-12'),
(2, 1, 'Mid-Term', 35, 50,  '2024-10-15'),
(2, 2, 'Mid-Term', 40, 50,  '2024-10-16'),
(2, 1, 'Final',    68, 100, '2025-01-10'),
(2, 2, 'Final',    72, 100, '2025-01-11');

-- ============================================================
-- FEES (sample)
-- ============================================================
INSERT IGNORE INTO fees (student_id, receipt_no, amount_paid, due_amount, paid_date, fee_type, remarks) VALUES
(1, 'RCP-2024-0001', 45000.00, 0.00,    '2024-07-10', 'Tuition', 'Full year fee paid'),
(2, 'RCP-2024-0002', 25000.00, 20000.00,'2024-07-12', 'Tuition', 'Partial payment'),
(3, 'RCP-2024-0003', 45000.00, 0.00,    '2024-07-08', 'Tuition', 'Full year fee paid'),
(4, 'RCP-2024-0004', 0.00,     45000.00,'2024-07-01', 'Tuition', 'Fee not yet paid'),
(5, 'RCP-2024-0005', 45000.00, 0.00,    '2024-07-15', 'Tuition', 'Full year fee paid'),
(6, 'RCP-2024-0006', 30000.00, 0.00,    '2024-07-06', 'Tuition', 'Full year fee paid'),
(7, 'RCP-2024-0007', 15000.00, 15000.00,'2024-07-07', 'Tuition', 'Partial payment'),
(8, 'RCP-2024-0008', 30000.00, 0.00,    '2024-07-09', 'Tuition', 'Full year fee paid');

-- ============================================================
-- NOTICES
-- ============================================================
INSERT IGNORE INTO notices (title, content, posted_by, posted_date) VALUES
('Welcome to Academic Year 2024-25',
 'Dear Students, welcome to the new academic session. Classes commence from August 1, 2024. Please ensure your fee payment is complete before August 15.',
 'admin', NOW()),
('Mid-Term Examination Schedule',
 'Mid-term examinations are scheduled from October 14 to October 20, 2024. Admit cards will be distributed one week prior. Ensure your attendance is above 75%.',
 'admin', DATE_SUB(NOW(), INTERVAL 5 DAY)),
('Holiday Notice — Dussehra',
 'The institution will remain closed on October 12 (Saturday) on account of Dussehra. Classes will resume on October 14 (Monday).',
 'admin', DATE_SUB(NOW(), INTERVAL 10 DAY));
