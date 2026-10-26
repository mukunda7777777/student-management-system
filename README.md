# 🎓 Student Management System (SMS)

[![Java Version](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Apache%20Maven-3.8%2B-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![Database](https://img.shields.io/badge/MySQL-8.0%2B-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![UI Theme](https://img.shields.io/badge/FlatLaf-Modern%20Dark%20Navy-007ACC?style=for-the-badge)](https://www.formdev.com/flatlaf/)
[![Architecture](https://img.shields.io/badge/Architecture-3--Tier%20MVC-success?style=for-the-badge)](#system-architecture)
[![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)](LICENSE)

An enterprise-grade desktop management application engineered in **Java Swing** and powered by a relational **MySQL** database. Designed to streamline academic and administrative workflows, this platform allows institutions to manage student records, curriculum subjects, daily attendance, examination grades, fee invoicing, and campus announcements within a sleek, modern UI.

---

## 📑 Table of Contents
1. [Key Features](#-key-features)
2. [Technology Stack](#-technology-stack)
3. [System Architecture](#-system-architecture)
4. [Functional Modules](#-functional-modules)
5. [Prerequisites](#-prerequisites)
6. [Database Setup](#-database-setup)
7. [How to Build & Run](#-how-to-build--run)
8. [Default Login Credentials](#-default-login-credentials)
9. [Project Structure](#-project-structure)
10. [Pushing to GitHub](#-pushing-to-github-guide)
11. [Troubleshooting & FAQs](#-troubleshooting--faqs)

---

## ✨ Key Features

- 🔐 **Role-Based Access Control (RBAC):** Distinct dashboards and permissions for Administrators and Students.
- 🛡️ **Cryptographic Security:** Passwords secured using SHA-256 hashing with salt support (no plaintext passwords).
- 🎨 **Modern FlatLaf UI:** Dark Navy & Cyan aesthetic with responsive layouts using MigLayout.
- 👥 **Student Record Management:** Full CRUD operations with live real-time search by Roll Number, Name, or Course.
- 📅 **Attendance Tracking:** Date-wise attendance logging (Present/Absent), bulk marking, and student percentage summaries.
- 📊 **Exam & Grade Processing:** Automated letter grade assignment (`O`, `A+`, `A`, `B+`, `B`, `C`, `F`), percentage calculations, and printable mark sheets.
- 💰 **Tuition & Fee Ledger:** Partial payments, dues calculation, receipt generation, and payment history tracking.
- 📢 **Campus Notice Board:** Broadcast instant circulars, academic notices, and holiday announcements.
- 🖨️ **Printing & Export:** Native Java 2D Graphics printing integration and iText PDF export ready.

---

## 🛠️ Technology Stack

| Component | Technology / Library | Version | Purpose |
|---|---|---|---|
| **Language** | Java SE | 17+ (LTS) | Core platform |
| **GUI Framework** | Java Swing + FlatLaf | 3.4.1 | Modern desktop look & feel |
| **Layout Manager** | MigLayout | 11.3 | Fluid, component-based layout |
| **Database** | MySQL Server | 8.0+ | Relational data persistence |
| **JDBC Driver** | MySQL Connector/J | 8.3.0 | Database connectivity & connection pool |
| **Security** | Java Cryptography Architecture | SHA-256 | Credential hashing |
| **PDF Generation** | iText PDF | 5.5.13.3 | Document export and report creation |
| **Build Tool** | Apache Maven | 3.8+ | Dependency management and packaging |

---

## 🏛️ System Architecture

The project follows a clean **3-Tier Model-View-Controller (MVC)** architectural pattern:

```
┌────────────────────────────────────────────────────────┐
│               PRESENTATION LAYER (GUI)                 │
│  LoginFrame | AdminDashboard | StudentDashboard        │
│  Student, Course, Attendance, Marks, Fee, Notice Panels│
└──────────────────────────┬─────────────────────────────┘
                           │
┌──────────────────────────▼─────────────────────────────┐
│                 SERVICE LAYER (Logic)                  │
│  AuthService | StudentService | CourseService          │
│  AttendanceService | MarksService | FeeService         │
│  NoticeService | ValidationUtils | SessionManager      │
└──────────────────────────┬─────────────────────────────┘
                           │
┌──────────────────────────▼─────────────────────────────┐
│                 DATA ACCESS LAYER (DAO)                │
│  DBConnection | UserDAO | StudentDAO | CourseDAO       │
│  AttendanceDAO | MarksDAO | FeeDAO | NoticeDAO         │
└──────────────────────────┬─────────────────────────────┘
                           │ (JDBC / Connection Pooling)
┌──────────────────────────▼─────────────────────────────┐
│                     MySQL DATABASE                     │
│  sms_db: users, students, courses, subjects,           │
│          attendance, marks, fees, notices              │
└────────────────────────────────────────────────────────┘
```

---

## 📦 Functional Modules

| Module Code | Module Name | Capabilities & Highlights |
|:---:|---|---|
| **FR-1** | **Authentication & Security** | Role-based routing (`ADMIN` / `STUDENT`), session management, SHA-256 hashed passwords. |
| **FR-2** | **Student Management** | Register, update, view, and soft/hard delete students; live filtering by roll number, name, or course. |
| **FR-3** | **Course & Subject Curriculum** | Define degree programs, associate subject syllabi, semester allocation, and maximum marks. |
| **FR-4** | **Attendance Register** | Daily roll-call entry, bulk-mark all present, date filter, subject-wise attendance % analytics. |
| **FR-5** | **Examination & Grades** | Subject marks entry, automatic total/percentage calculation, letter grading, and printable report cards. |
| **FR-6** | **Fee Management** | Tuition invoicing, partial installments, receipt numbering, remaining balance tracking. |
| **FR-7** | **Notice Board** | Admin publishing & archiving; live announcements feed on the student dashboard. |

---

## 📋 Prerequisites

Before running the application, make sure you have installed:

1. **Java Development Kit (JDK):** Version 17 or higher
   - Verify with: `java -version`
2. **MySQL Server:** Version 8.0 or higher (or XAMPP / WampServer)
   - Ensure the MySQL service is running on port `3306`.
3. **Apache Maven** *(Optional, recommended)*:
   - Verify with: `mvn -version`
   - *Note: If Maven is not installed, the project includes pre-bundled dependencies in the `lib/` directory for zero-install runs.*

---

## 🗄️ Database Setup

### Option A: Automatic Setup (Fastest & Recommended)
1. Ensure your MySQL Server is running.
2. Edit [`src/main/resources/db.properties`](src/main/resources/db.properties) with your MySQL credentials:
   ```properties
   db.url=jdbc:mysql://localhost:3306/sms_db?useSSL=false&serverTimezone=Asia/Kolkata&allowPublicKeyRetrieval=true
   db.username=root
   db.password=YOUR_MYSQL_PASSWORD
   db.driver=com.mysql.cj.jdbc.Driver
   ```
3. Run the automated database initializer:
   - **Windows:** Double-click [`setup_database.bat`](setup_database.bat) or run:
     ```cmd
     setup_database.bat
     ```
   - **Terminal / PowerShell:**
     ```bash
     java -cp "target/classes;lib/*" com.sms.util.DBInitializer
     ```
   This will automatically create the `sms_db` database, initialize all 8 tables, and populate sample test data!

### Option B: Manual SQL Setup
You can also import the SQL scripts manually via MySQL Workbench, phpMyAdmin, or the MySQL CLI:

```sql
-- 1. Create schema and constraints
source db/schema.sql;

-- 2. Populate courses, subjects, users, students, marks, fees & notices
source db/sample_data.sql;
```

---

## 🔑 Default Login Credentials

### 1. Administrator Account
- **Username:** `admin`
- **Password:** `Admin@123`
- **Access Level:** Full access to all management modules, analytics, and reports.

### 2. Pre-seeded Student Accounts
| Roll Number / Username | Student Name | Password | Course |
|---|---|---|---|
| `CS2024001` | Priya Sharma | `Student@123` | Computer Science |
| `CS2024002` | Rahul Verma | `Student@123` | Computer Science |
| `BCA2024001` | Rohan Das | `Student@123` | Bachelor of Computer Applications |

---

## 🚀 How to Build & Run

### Method 1: One-Click Quick Launch (Windows)
Double-click [`run.bat`](run.bat) in the project root:
```cmd
run.bat
```

---

### Method 2: Using Apache Maven (Recommended)

1. **Compile the project:**
   ```bash
   mvn clean compile
   ```

2. **Run the application:**
   ```bash
   mvn exec:java -Dexec.mainClass="com.sms.Main"
   ```

3. **Package into an executable Fat JAR (with all dependencies bundled):**
   ```bash
   mvn clean package
   ```
   Run the generated standalone JAR:
   ```bash
   java -jar target/StudentManagementSystem.jar
   ```

---

### Method 3: Using Standard Command Line (Zero Maven Required)

Using the dependencies bundled in the `lib/` directory:

- **Windows (PowerShell):**
  ```powershell
  # Compile
  javac -encoding UTF-8 -cp "lib/*" -d "target/classes" (Get-ChildItem -Path "src/main/java" -Recurse -Filter "*.java").FullName
  
  # Copy resources
  Copy-Item -Path "src/main/resources/*" -Destination "target/classes" -Recurse -Force

  # Run
  java -cp "target/classes;lib/*" com.sms.Main
  ```

- **Linux / macOS (Bash):**
  ```bash
  # Compile
  javac -encoding UTF-8 -cp "lib/*" -d target/classes $(find src/main/java -name "*.java")
  
  # Copy resources
  cp -r src/main/resources/* target/classes/

  # Run
  java -cp "target/classes:lib/*" com.sms.Main
  ```

---

## 📂 Project Structure

```
Student management system/
├── .github/                           ← GitHub workflows (optional CI)
├── .gitignore                         ← Configured for Java, Maven, IDEs
├── db/
│   ├── schema.sql                     ← Complete DDL for 8 relational tables
│   └── sample_data.sql                ← Realistic mock records with SHA-256 hashes
├── lib/                               ← Bundled dependencies for zero-install runs
│   ├── flatlaf-3.4.1.jar
│   ├── itextpdf-5.5.13.3.jar
│   ├── miglayout-core-11.3.jar
│   ├── miglayout-swing-11.3.jar
│   └── mysql-connector-j-8.3.0.jar
├── src/
│   └── main/
│       ├── java/
│       │   └── com/sms/
│       │       ├── Main.java          ← Application launch entrypoint
│       │       ├── dao/               ← Data Access Layer (JDBC queries & connection)
│       │       │   ├── DBConnection.java
│       │       │   ├── UserDAO.java
│       │       │   ├── StudentDAO.java
│       │       │   ├── CourseDAO.java
│       │       │   ├── AttendanceDAO.java
│       │       │   ├── MarksDAO.java
│       │       │   ├── FeeDAO.java
│       │       │   └── NoticeDAO.java
│       │       ├── model/             ← Entity POJOs
│       │       │   ├── User.java
│       │       │   ├── Student.java
│       │       │   ├── Course.java
│       │       │   ├── Subject.java
│       │       │   ├── Attendance.java
│       │       │   ├── Marks.java
│       │       │   ├── Fee.java
│       │       │   └── Notice.java
│       │       ├── service/           ← Business rules & services
│       │       │   ├── AuthService.java
│       │       │   ├── StudentService.java
│       │       │   ├── CourseService.java
│       │       │   ├── AttendanceService.java
│       │       │   ├── MarksService.java
│       │       │   ├── FeeService.java
│       │       │   ├── NoticeService.java
│       │       │   └── DashboardService.java
│       │       ├── ui/                ← Swing Presentation Layer
│       │       │   ├── LoginFrame.java
│       │       │   ├── AdminDashboard.java
│       │       │   ├── StudentDashboard.java
│       │       │   ├── attendance/
│       │       │   ├── course/
│       │       │   ├── fee/
│       │       │   ├── marks/
│       │       │   ├── notice/
│       │       │   ├── student/
│       │       │   └── util/
│       │       └── util/              ← Helpers (Hash, Validation, Session, DB Init)
│       │           ├── DBInitializer.java
│       │           ├── PasswordUtils.java
│       │           ├── SessionManager.java
│       │           └── ValidationUtils.java
│       └── resources/
│           └── db.properties          ← Database connection configuration
├── pom.xml                            ← Maven project definition & fat-jar builder
├── run.bat                            ← One-click execution launcher
├── setup_database.bat                 ← One-click database initializer
└── README.md                          ← Project documentation
```

---

## 🌐 Pushing to GitHub Guide

Follow these steps in your terminal to publish this repository to GitHub:

### 1. Initialize Git in the project directory
```bash
git init
```

### 2. Stage all files (using the configured `.gitignore`)
```bash
git add .
```

### 3. Commit your files
```bash
git commit -m "feat: initial commit for Student Management System (SMS)"
```

### 4. Create a new repository on GitHub
1. Go to [GitHub - New Repository](https://github.com/new).
2. Set the repository name (e.g., `student-management-system`).
3. Leave **"Initialize this repository with a README" unchecked** (you already have one).
4. Click **Create repository**.

### 5. Link local repository and push to GitHub
```bash
# Rename branch to main
git branch -M main

# Link remote repository (replace with your GitHub username & repo name)
git remote add origin https://github.com/YOUR_USERNAME/student-management-system.git

# Push code to GitHub
git push -u origin main
```

---

## ❓ Troubleshooting & FAQs

### 1. `Communications link failure` / `Connection refused`
- **Cause:** MySQL Server is not running or port `3306` is blocked.
- **Fix:** Start MySQL service via MySQL Workbench, Windows Services (`services.msc`), or XAMPP Control Panel.

### 2. `Access denied for user 'root'@'localhost'`
- **Cause:** Incorrect MySQL password.
- **Fix:** Open [`src/main/resources/db.properties`](src/main/resources/db.properties) and update `db.password` to match your local MySQL root password.

### 3. `The server time zone value '...' is unrecognized`
- **Cause:** MySQL JDBC driver timezone mismatch.
- **Fix:** The included connection string in `db.properties` already sets `serverTimezone=Asia/Kolkata`. You can change it to `serverTimezone=UTC` if needed.

### 4. FlatLaf UI looks blurry on high-DPI Windows screens
- **Fix:** FlatLaf automatically detects Windows scaling. You can explicitly pass the JVM parameter:
  ```bash
  java -Dsun.java2d.uiScale=1.0 -cp "target/classes;lib/*" com.sms.Main
  ```

---

## 📄 License
This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

---

⭐ *If you find this project helpful for your academic submission or learning Java Swing, feel free to give it a star on GitHub!*
