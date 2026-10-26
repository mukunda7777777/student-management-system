package com.sms.model;

import java.time.LocalDateTime;

/**
 * Represents a notice/announcement posted by the Admin.
 */
public class Notice {
    private int id;
    private String title;
    private String content;
    private String postedBy;
    private LocalDateTime postedDate;
    private boolean active;

    public Notice() {}

    public Notice(int id, String title, String content, String postedBy,
                  LocalDateTime postedDate, boolean active) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.postedBy = postedBy;
        this.postedDate = postedDate;
        this.active = active;
    }

    public int getId()                       { return id; }
    public void setId(int id)                { this.id = id; }

    public String getTitle()                 { return title; }
    public void setTitle(String t)           { this.title = t; }

    public String getContent()               { return content; }
    public void setContent(String c)         { this.content = c; }

    public String getPostedBy()              { return postedBy; }
    public void setPostedBy(String pb)       { this.postedBy = pb; }

    public LocalDateTime getPostedDate()     { return postedDate; }
    public void setPostedDate(LocalDateTime d){ this.postedDate = d; }

    public boolean isActive()                { return active; }
    public void setActive(boolean a)         { this.active = a; }

    @Override
    public String toString() { return title; }
}
