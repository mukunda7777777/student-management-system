package com.sms.ui.student;

import com.sms.model.Course;
import com.sms.model.Student;
import com.sms.service.StudentService;
import com.sms.dao.CourseDAO;
import com.sms.ui.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Form for adding or editing a student record.
 */
public class StudentFormDialog extends JDialog {

    private final StudentService studentService;
    private final Student existingStudent; // null = add mode
    private boolean saved = false;

    // Form fields
    private JTextField tfRollNo, tfFirstName, tfLastName, tfEmail, tfPhone;
    private JTextArea  taAddress;
    private JComboBox<String> cbGender;
    private JComboBox<Course> cbCourse;
    private JTextField tfDob, tfAdmission;

    public StudentFormDialog(Frame parent, Student student) throws SQLException {
        super(parent, student == null ? "Add New Student" : "Edit Student", true);
        this.studentService   = new StudentService();
        this.existingStudent  = student;
        initUI();
        if (student != null) populateFields(student);
        pack();
        setLocationRelativeTo(parent);
    }

    private void initUI() throws SQLException {
        setBackground(UITheme.BG_CARD);
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG_CARD);
        root.setBorder(new EmptyBorder(20, 28, 20, 28));
        setContentPane(root);

        // Title
        JLabel title = UITheme.makeTitle(existingStudent == null ? "Add New Student" : "Edit Student");
        title.setBorder(new EmptyBorder(0, 0, 16, 0));
        root.add(title, BorderLayout.NORTH);

        // Form grid
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UITheme.BG_CARD);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        tfRollNo    = UITheme.makeTextField(15);
        tfFirstName = UITheme.makeTextField(15);
        tfLastName  = UITheme.makeTextField(15);
        tfEmail     = UITheme.makeTextField(15);
        tfPhone     = UITheme.makeTextField(15);
        tfDob       = UITheme.makeTextField(12);
        tfDob.setToolTipText("yyyy-MM-dd");
        tfAdmission = UITheme.makeTextField(12);
        tfAdmission.setToolTipText("yyyy-MM-dd");
        tfAdmission.setText(LocalDate.now().toString());

        cbGender = UITheme.makeComboBox();
        cbGender.addItem("Male"); cbGender.addItem("Female"); cbGender.addItem("Other");

        cbCourse = UITheme.makeComboBox();
        cbCourse.addItem(null);  // no-course option
        CourseDAO courseDAO = new CourseDAO();
        for (Course c : courseDAO.findAllCourses()) cbCourse.addItem(c);

        taAddress = UITheme.makeTextArea(3, 20);
        JScrollPane addrScroll = new JScrollPane(taAddress);
        addrScroll.setPreferredSize(new Dimension(220, 70));
        addrScroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));

        int row = 0;
        addField(form, gbc, "Roll No. *", tfRollNo,    row++, 0);
        addField(form, gbc, "First Name *", tfFirstName, row++, 0);
        addField(form, gbc, "Last Name *",  tfLastName,  row++, 0);
        addField(form, gbc, "Date of Birth (yyyy-MM-dd)", tfDob, row++, 0);
        addField(form, gbc, "Gender", cbGender,          row++, 0);
        addField(form, gbc, "Email",  tfEmail,            row++, 0);
        addField(form, gbc, "Phone",  tfPhone,            row++, 0);
        addField(form, gbc, "Course", cbCourse,           row++, 0);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        form.add(UITheme.makeLabel("Address"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.gridwidth = 2;
        form.add(addrScroll, gbc);
        row++;

        addField(form, gbc, "Admission Date", tfAdmission, row, 0);

        root.add(form, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(UITheme.BG_CARD);
        btnPanel.setBorder(new EmptyBorder(16, 0, 0, 0));
        JButton btnCancel = UITheme.makeSecondaryButton("Cancel");
        JButton btnSave   = UITheme.makePrimaryButton(existingStudent == null ? "Add Student" : "Save Changes");
        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> doSave());
        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);
        root.add(btnPanel, BorderLayout.SOUTH);

        setMinimumSize(new Dimension(500, 500));
    }

    private void addField(JPanel panel, GridBagConstraints gbc, String label,
                          Component field, int row, int startCol) {
        gbc.gridwidth = 1;
        gbc.gridx = startCol; gbc.gridy = row;
        panel.add(UITheme.makeLabel(label), gbc);
        gbc.gridx = startCol + 1; gbc.gridy = row; gbc.gridwidth = 2;
        panel.add(field, gbc);
        gbc.gridwidth = 1;
    }

    private void populateFields(Student s) {
        tfRollNo.setText(s.getRollNo());
        tfFirstName.setText(s.getFirstName());
        tfLastName.setText(s.getLastName());
        tfEmail.setText(s.getEmail() != null ? s.getEmail() : "");
        tfPhone.setText(s.getPhone() != null ? s.getPhone() : "");
        taAddress.setText(s.getAddress() != null ? s.getAddress() : "");
        if (s.getDob() != null) tfDob.setText(s.getDob().toString());
        if (s.getAdmissionDate() != null) tfAdmission.setText(s.getAdmissionDate().toString());
        cbGender.setSelectedItem(s.getGender());
        // Select course
        if (s.getCourseId() != null) {
            for (int i = 0; i < cbCourse.getItemCount(); i++) {
                Course c = cbCourse.getItemAt(i);
                if (c != null && c.getId() == s.getCourseId()) {
                    cbCourse.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void doSave() {
        Student s = existingStudent != null ? existingStudent : new Student();
        s.setRollNo(tfRollNo.getText().trim());
        s.setFirstName(tfFirstName.getText().trim());
        s.setLastName(tfLastName.getText().trim());
        s.setEmail(tfEmail.getText().trim());
        s.setPhone(tfPhone.getText().trim());
        s.setAddress(taAddress.getText().trim());
        s.setGender((String) cbGender.getSelectedItem());

        String dobStr = tfDob.getText().trim();
        if (!dobStr.isEmpty()) {
            try { s.setDob(LocalDate.parse(dobStr)); }
            catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid Date of Birth format (use yyyy-MM-dd).");
                return;
            }
        }
        String admStr = tfAdmission.getText().trim();
        try {
            s.setAdmissionDate(!admStr.isEmpty() ? LocalDate.parse(admStr) : LocalDate.now());
        } catch (Exception e) { s.setAdmissionDate(LocalDate.now()); }

        Course selectedCourse = (Course) cbCourse.getSelectedItem();
        s.setCourseId(selectedCourse != null ? selectedCourse.getId() : null);

        try {
            String err = existingStudent == null
                    ? studentService.addStudent(s)
                    : studentService.updateStudent(s);
            if (err != null) {
                JOptionPane.showMessageDialog(this, err, "Validation Error", JOptionPane.WARNING_MESSAGE);
            } else {
                saved = true;
                JOptionPane.showMessageDialog(this,
                        (existingStudent == null ? "Student added!" : "Student updated!") +
                        (existingStudent == null ? "\nDefault password: Student@123" : ""),
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() { return saved; }
}
