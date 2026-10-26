package com.sms.model;

import java.time.LocalDate;

/**
 * Records a single attendance entry for a student in a subject on a given date.
 */
public class Attendance {
    private int id;
    private int studentId;
    private String studentName; // transient
    private String rollNo;      // transient
    private int subjectId;
    private String subjectName; // transient
    private LocalDate attDate;
    private String status;      // "P" or "A"

    public Attendance() {}

    public Attendance(int id, int studentId, int subjectId, LocalDate attDate, String status) {
        this.id = id;
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.attDate = attDate;
        this.status = status;
    }

    public int getId()                     { return id; }
    public void setId(int id)              { this.id = id; }

    public int getStudentId()              { return studentId; }
    public void setStudentId(int sid)      { this.studentId = sid; }

    public String getStudentName()         { return studentName; }
    public void setStudentName(String sn)  { this.studentName = sn; }

    public String getRollNo()              { return rollNo; }
    public void setRollNo(String r)        { this.rollNo = r; }

    public int getSubjectId()              { return subjectId; }
    public void setSubjectId(int sid)      { this.subjectId = sid; }

    public String getSubjectName()         { return subjectName; }
    public void setSubjectName(String sn)  { this.subjectName = sn; }

    public LocalDate getAttDate()          { return attDate; }
    public void setAttDate(LocalDate d)    { this.attDate = d; }

    public String getStatus()              { return status; }
    public void setStatus(String s)        { this.status = s; }

    public boolean isPresent()             { return "P".equals(status); }
}
