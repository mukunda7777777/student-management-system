package com.sms.model;

import java.time.LocalDate;

/**
 * Records a fee payment or due entry for a student.
 */
public class Fee {
    private int id;
    private int studentId;
    private String studentName;  // transient
    private String rollNo;       // transient
    private String courseName;   // transient
    private String receiptNo;
    private double amountPaid;
    private double dueAmount;
    private LocalDate paidDate;
    private String feeType;
    private String remarks;

    public Fee() {}

    public Fee(int id, int studentId, String receiptNo, double amountPaid,
               double dueAmount, LocalDate paidDate, String feeType, String remarks) {
        this.id = id;
        this.studentId = studentId;
        this.receiptNo = receiptNo;
        this.amountPaid = amountPaid;
        this.dueAmount = dueAmount;
        this.paidDate = paidDate;
        this.feeType = feeType;
        this.remarks = remarks;
    }

    public boolean hasDue() { return dueAmount > 0; }

    public int getId()                      { return id; }
    public void setId(int id)               { this.id = id; }

    public int getStudentId()               { return studentId; }
    public void setStudentId(int sid)       { this.studentId = sid; }

    public String getStudentName()          { return studentName; }
    public void setStudentName(String sn)   { this.studentName = sn; }

    public String getRollNo()               { return rollNo; }
    public void setRollNo(String r)         { this.rollNo = r; }

    public String getCourseName()           { return courseName; }
    public void setCourseName(String cn)    { this.courseName = cn; }

    public String getReceiptNo()            { return receiptNo; }
    public void setReceiptNo(String rn)     { this.receiptNo = rn; }

    public double getAmountPaid()           { return amountPaid; }
    public void setAmountPaid(double ap)    { this.amountPaid = ap; }

    public double getDueAmount()            { return dueAmount; }
    public void setDueAmount(double da)     { this.dueAmount = da; }

    public LocalDate getPaidDate()          { return paidDate; }
    public void setPaidDate(LocalDate pd)   { this.paidDate = pd; }

    public String getFeeType()              { return feeType; }
    public void setFeeType(String ft)       { this.feeType = ft; }

    public String getRemarks()              { return remarks; }
    public void setRemarks(String r)        { this.remarks = r; }
}
