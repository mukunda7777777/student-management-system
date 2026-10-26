package com.sms.model;

/**
 * Represents an academic course offered by the institution.
 */
public class Course {
    private int id;
    private String name;
    private String duration;
    private String description;

    public Course() {}

    public Course(int id, String name, String duration, String description) {
        this.id = id;
        this.name = name;
        this.duration = duration;
        this.description = description;
    }

    public int getId()                    { return id; }
    public void setId(int id)             { this.id = id; }

    public String getName()               { return name; }
    public void setName(String n)         { this.name = n; }

    public String getDuration()           { return duration; }
    public void setDuration(String d)     { this.duration = d; }

    public String getDescription()        { return description; }
    public void setDescription(String d)  { this.description = d; }

    @Override
    public String toString() { return name; }
}
