package com.sms.model;

import java.time.LocalDate;

/**
 * Stores the marks obtained by a student in one subject for one exam type.
 */
public class Marks {
    private int id;
    private int studentId;
    private String studentName;  // transient
    private String rollNo;       // transient
    private int subjectId;
    private String subjectName;  // transient
    private String examType;     // e.g. "Mid-Term", "Final", "Internal"
    private double marksObtained;
    private int maxMarks;
    private LocalDate examDate;

    public Marks() {}

    public Marks(int id, int studentId, int subjectId, String examType,
                 double marksObtained, int maxMarks, LocalDate examDate) {
        this.id = id;
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.examType = examType;
        this.marksObtained = marksObtained;
        this.maxMarks = maxMarks;
        this.examDate = examDate;
    }

    /** Returns the percentage for this entry. */
    public double getPercentage() {
        return maxMarks > 0 ? (marksObtained / maxMarks) * 100.0 : 0;
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

    public String getExamType()            { return examType; }
    public void setExamType(String et)     { this.examType = et; }

    public double getMarksObtained()       { return marksObtained; }
    public void setMarksObtained(double m) { this.marksObtained = m; }

    public int getMaxMarks()               { return maxMarks; }
    public void setMaxMarks(int mm)        { this.maxMarks = mm; }

    public LocalDate getExamDate()         { return examDate; }
    public void setExamDate(LocalDate d)   { this.examDate = d; }
}
