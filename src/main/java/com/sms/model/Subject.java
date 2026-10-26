package com.sms.model;

/**
 * Represents a subject belonging to a course.
 */
public class Subject {
    private int id;
    private int courseId;
    private String courseName; // transient
    private String name;
    private int maxMarks;

    public Subject() {}

    public Subject(int id, int courseId, String name, int maxMarks) {
        this.id = id;
        this.courseId = courseId;
        this.name = name;
        this.maxMarks = maxMarks;
    }

    public int getId()                   { return id; }
    public void setId(int id)            { this.id = id; }

    public int getCourseId()             { return courseId; }
    public void setCourseId(int cid)     { this.courseId = cid; }

    public String getCourseName()        { return courseName; }
    public void setCourseName(String cn) { this.courseName = cn; }

    public String getName()              { return name; }
    public void setName(String n)        { this.name = n; }

    public int getMaxMarks()             { return maxMarks; }
    public void setMaxMarks(int m)       { this.maxMarks = m; }

    @Override
    public String toString() { return name; }
}
