package com.sms.service;

import com.sms.dao.FeeDAO;
import com.sms.model.Fee;
import com.sms.util.ValidationUtils;

import java.sql.SQLException;
import java.util.List;

/**
 * Business service for Fee Management.
 */
public class FeeService {

    private final FeeDAO feeDAO;

    public FeeService() throws SQLException {
        this.feeDAO = new FeeDAO();
    }

    public String recordFee(Fee fee) throws SQLException {
        String err = ValidationUtils.validateAmount(fee.getAmountPaid());
        if (err != null) return err;
        err = ValidationUtils.validateAmount(fee.getDueAmount());
        if (err != null) return err;

        if (fee.getReceiptNo() == null || fee.getReceiptNo().trim().isEmpty()) {
            fee.setReceiptNo("REC-" + System.currentTimeMillis());
        }

        int id = feeDAO.create(fee);
        if (id <= 0) return "Failed to record fee payment.";
        fee.setId(id);
        return null;
    }

    public List<Fee> getAllFees() throws SQLException {
        return feeDAO.findAll();
    }

    public List<Fee> getFeesByStudent(int studentId) throws SQLException {
        return feeDAO.findByStudent(studentId);
    }

    public List<Fee> getDueStudents() throws SQLException {
        return feeDAO.findDueStudents();
    }

    public String updateFee(Fee fee) throws SQLException {
        String err = ValidationUtils.validateAmount(fee.getAmountPaid());
        if (err != null) return err;
        err = ValidationUtils.validateAmount(fee.getDueAmount());
        if (err != null) return err;

        boolean success = feeDAO.update(fee);
        return success ? null : "Failed to update fee record.";
    }
}
