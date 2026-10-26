package com.sms.model;

import java.time.LocalDate;

/**
 * Represents a student enrolled in the institution.
 */
public class Student {
    private int id;
    private String rollNo;
    private String firstName;
    private String lastName;
    private LocalDate dob;
    private String gender;
    private String email;
    private String phone;
    private String address;
    private Integer courseId;
    private String courseName;   // transient – filled by JOIN
    private String photoPath;
    private LocalDate admissionDate;

    public Student() {}

    // ── Full constructor ──────────────────────────────────────
    public Student(int id, String rollNo, String firstName, String lastName,
                   LocalDate dob, String gender, String email, String phone,
                   String address, Integer courseId, String photoPath, LocalDate admissionDate) {
        this.id = id;
        this.rollNo = rollNo;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.gender = gender;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.courseId = courseId;
        this.photoPath = photoPath;
        this.admissionDate = admissionDate;
    }

    // ── Helpers ───────────────────────────────────────────────
    public String getFullName() {
        return firstName + " " + lastName;
    }

    // ── Getters & Setters ──────────────────────────────────────
    public int getId()                        { return id; }
    public void setId(int id)                 { this.id = id; }

    public String getRollNo()                 { return rollNo; }
    public void setRollNo(String r)           { this.rollNo = r; }

    public String getFirstName()              { return firstName; }
    public void setFirstName(String fn)       { this.firstName = fn; }

    public String getLastName()               { return lastName; }
    public void setLastName(String ln)        { this.lastName = ln; }

    public LocalDate getDob()                 { return dob; }
    public void setDob(LocalDate d)           { this.dob = d; }

    public String getGender()                 { return gender; }
    public void setGender(String g)           { this.gender = g; }

    public String getEmail()                  { return email; }
    public void setEmail(String e)            { this.email = e; }

    public String getPhone()                  { return phone; }
    public void setPhone(String p)            { this.phone = p; }

    public String getAddress()                { return address; }
    public void setAddress(String a)          { this.address = a; }

    public Integer getCourseId()              { return courseId; }
    public void setCourseId(Integer cid)      { this.courseId = cid; }

    public String getCourseName()             { return courseName; }
    public void setCourseName(String cn)      { this.courseName = cn; }

    public String getPhotoPath()              { return photoPath; }
    public void setPhotoPath(String pp)       { this.photoPath = pp; }

    public LocalDate getAdmissionDate()       { return admissionDate; }
    public void setAdmissionDate(LocalDate d) { this.admissionDate = d; }

    @Override
    public String toString() {
        return rollNo + " - " + getFullName();
    }
}
