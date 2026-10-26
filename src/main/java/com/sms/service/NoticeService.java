package com.sms.service;

import com.sms.dao.NoticeDAO;
import com.sms.model.Notice;
import com.sms.util.ValidationUtils;

import java.sql.SQLException;
import java.util.List;

/**
 * Business service for managing notices and announcements.
 */
public class NoticeService {

    private final NoticeDAO noticeDAO;

    public NoticeService() throws SQLException {
        this.noticeDAO = new NoticeDAO();
    }

    public List<Notice> getAllNotices() throws SQLException {
        return noticeDAO.findAll();
    }

    public List<Notice> getActiveNotices() throws SQLException {
        return noticeDAO.findActive();
    }

    public String postNotice(Notice notice) throws SQLException {
        String err = ValidationUtils.requireNonBlank(notice.getTitle(), "Notice title");
        if (err != null) return err;
        err = ValidationUtils.requireNonBlank(notice.getContent(), "Notice content");
        if (err != null) return err;

        int id = noticeDAO.create(notice);
        if (id <= 0) return "Failed to post notice.";
        notice.setId(id);
        return null;
    }

    public String updateNotice(Notice notice) throws SQLException {
        String err = ValidationUtils.requireNonBlank(notice.getTitle(), "Notice title");
        if (err != null) return err;
        err = ValidationUtils.requireNonBlank(notice.getContent(), "Notice content");
        if (err != null) return err;

        boolean success = noticeDAO.update(notice);
        return success ? null : "Failed to update notice.";
    }

    public boolean deleteNotice(int id) throws SQLException {
        return noticeDAO.delete(id);
    }
}
