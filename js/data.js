/**
 * Initial seed data matching db/sample_data.sql
 * Stored in localStorage for live persistence and reset capabilities.
 */

const STORAGE_KEY = 'sms_web_app_state_v1';

export const INITIAL_DATA = {
  currentUser: {
    username: 'admin',
    name: 'System Administrator',
    role: 'ADMIN',
    avatar: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150&auto=format&fit=crop&q=80'
  },
  courses: [
    { id: 1, code: 'BTECH-CSE', name: 'B.Tech Computer Science & Engineering', durationYears: 4, feePerYear: 95000 },
    { id: 2, code: 'BCA', name: 'Bachelor of Computer Applications', durationYears: 3, feePerYear: 65000 },
    { id: 3, code: 'BTECH-ECE', name: 'B.Tech Electronics & Communication', durationYears: 4, feePerYear: 90000 },
    { id: 4, code: 'MCA', name: 'Master of Computer Applications', durationYears: 2, feePerYear: 80000 }
  ],
  subjects: [
    { id: 1, courseId: 1, code: 'CS101', name: 'Data Structures & Algorithms', semester: 3, maxMarks: 100 },
    { id: 2, courseId: 1, code: 'CS102', name: 'Database Management Systems', semester: 3, maxMarks: 100 },
    { id: 3, courseId: 1, code: 'CS103', name: 'Object Oriented Programming (Java)', semester: 3, maxMarks: 100 },
    { id: 4, courseId: 1, code: 'CS104', name: 'Computer Networks', semester: 4, maxMarks: 100 },
    { id: 5, courseId: 2, code: 'BCA201', name: 'Web Technologies & JavaScript', semester: 2, maxMarks: 100 },
    { id: 6, courseId: 2, code: 'BCA202', name: 'Operating Systems Principles', semester: 2, maxMarks: 100 }
  ],
  students: [
    {
      id: 1,
      rollNo: 'CS2024001',
      name: 'Priya Sharma',
      email: 'priya.sharma@campus.edu',
      phone: '+91 98765 43210',
      courseId: 1,
      courseCode: 'BTECH-CSE',
      semester: 3,
      dob: '2004-05-14',
      gender: 'Female',
      address: '74 Lakeview Colony, Bangalore, KA',
      avatar: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150&auto=format&fit=crop&q=80',
      status: 'Active'
    },
    {
      id: 2,
      rollNo: 'CS2024002',
      name: 'Rahul Verma',
      email: 'rahul.verma@campus.edu',
      phone: '+91 98112 23344',
      courseId: 1,
      courseCode: 'BTECH-CSE',
      semester: 3,
      dob: '2003-11-20',
      gender: 'Male',
      address: '12 Green Park Extension, New Delhi',
      avatar: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&auto=format&fit=crop&q=80',
      status: 'Active'
    },
    {
      id: 3,
      rollNo: 'BCA2024001',
      name: 'Rohan Das',
      email: 'rohan.das@campus.edu',
      phone: '+91 97480 11223',
      courseId: 2,
      courseCode: 'BCA',
      semester: 2,
      dob: '2004-01-09',
      gender: 'Male',
      address: '88 Salt Lake Sector V, Kolkata, WB',
      avatar: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150&auto=format&fit=crop&q=80',
      status: 'Active'
    },
    {
      id: 4,
      rollNo: 'CS2024003',
      name: 'Ananya Iyer',
      email: 'ananya.iyer@campus.edu',
      phone: '+91 94451 88990',
      courseId: 1,
      courseCode: 'BTECH-CSE',
      semester: 3,
      dob: '2004-08-25',
      gender: 'Female',
      address: '33 TTK Road, Alwarpet, Chennai, TN',
      avatar: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150&auto=format&fit=crop&q=80',
      status: 'Active'
    },
    {
      id: 5,
      rollNo: 'BCA2024002',
      name: 'Vikramaditya Roy',
      email: 'vikram.roy@campus.edu',
      phone: '+91 98302 44556',
      courseId: 2,
      courseCode: 'BCA',
      semester: 2,
      dob: '2003-09-17',
      gender: 'Male',
      address: '15 Civil Lines, Jaipur, RJ',
      avatar: 'https://images.unsplash.com/photo-1492562080023-ab3db95bfbce?w=150&auto=format&fit=crop&q=80',
      status: 'Active'
    }
  ],
  attendance: [
    { id: 1, studentId: 1, rollNo: 'CS2024001', date: '2026-10-20', status: 'Present', subject: 'CS101' },
    { id: 2, studentId: 1, rollNo: 'CS2024001', date: '2026-10-21', status: 'Present', subject: 'CS102' },
    { id: 3, studentId: 1, rollNo: 'CS2024001', date: '2026-10-22', status: 'Present', subject: 'CS103' },
    { id: 4, studentId: 1, rollNo: 'CS2024001', date: '2026-10-23', status: 'Present', subject: 'CS101' },
    { id: 5, studentId: 1, rollNo: 'CS2024001', date: '2026-10-24', status: 'Present', subject: 'CS102' },

    { id: 6, studentId: 2, rollNo: 'CS2024002', date: '2026-10-20', status: 'Present', subject: 'CS101' },
    { id: 7, studentId: 2, rollNo: 'CS2024002', date: '2026-10-21', status: 'Absent', subject: 'CS102' },
    { id: 8, studentId: 2, rollNo: 'CS2024002', date: '2026-10-22', status: 'Present', subject: 'CS103' },
    { id: 9, studentId: 2, rollNo: 'CS2024002', date: '2026-10-23', status: 'Present', subject: 'CS101' },
    { id: 10, studentId: 2, rollNo: 'CS2024002', date: '2026-10-24', status: 'Absent', subject: 'CS102' },

    { id: 11, studentId: 3, rollNo: 'BCA2024001', date: '2026-10-20', status: 'Present', subject: 'BCA201' },
    { id: 12, studentId: 3, rollNo: 'BCA2024001', date: '2026-10-21', status: 'Present', subject: 'BCA202' },
    { id: 13, studentId: 3, rollNo: 'BCA2024001', date: '2026-10-22', status: 'Present', subject: 'BCA201' },
    { id: 14, studentId: 3, rollNo: 'BCA2024001', date: '2026-10-23', status: 'Present', subject: 'BCA202' },
    { id: 15, studentId: 3, rollNo: 'BCA2024001', date: '2026-10-24', status: 'Present', subject: 'BCA201' }
  ],
  marks: [
    { id: 1, studentId: 1, rollNo: 'CS2024001', subjectCode: 'CS101', subjectName: 'Data Structures & Algorithms', marksObtained: 92, maxMarks: 100, examType: 'End-Term Exam', semester: 3 },
    { id: 2, studentId: 1, rollNo: 'CS2024001', subjectCode: 'CS102', subjectName: 'Database Management Systems', marksObtained: 88, maxMarks: 100, examType: 'End-Term Exam', semester: 3 },
    { id: 3, studentId: 1, rollNo: 'CS2024001', subjectCode: 'CS103', subjectName: 'Object Oriented Programming', marksObtained: 95, maxMarks: 100, examType: 'End-Term Exam', semester: 3 },

    { id: 4, studentId: 2, rollNo: 'CS2024002', subjectCode: 'CS101', subjectName: 'Data Structures & Algorithms', marksObtained: 74, maxMarks: 100, examType: 'End-Term Exam', semester: 3 },
    { id: 5, studentId: 2, rollNo: 'CS2024002', subjectCode: 'CS102', subjectName: 'Database Management Systems', marksObtained: 81, maxMarks: 100, examType: 'End-Term Exam', semester: 3 },
    { id: 6, studentId: 2, rollNo: 'CS2024002', subjectCode: 'CS103', subjectName: 'Object Oriented Programming', marksObtained: 68, maxMarks: 100, examType: 'End-Term Exam', semester: 3 },

    { id: 7, studentId: 3, rollNo: 'BCA2024001', subjectCode: 'BCA201', subjectName: 'Web Technologies & JS', marksObtained: 89, maxMarks: 100, examType: 'End-Term Exam', semester: 2 },
    { id: 8, studentId: 3, rollNo: 'BCA2024001', subjectCode: 'BCA202', subjectName: 'Operating Systems', marksObtained: 84, maxMarks: 100, examType: 'End-Term Exam', semester: 2 }
  ],
  fees: [
    { id: 1, studentId: 1, rollNo: 'CS2024001', receiptNo: 'REC-2026-001', totalAmount: 95000, paidAmount: 95000, dueAmount: 0, status: 'Paid', paymentDate: '2026-08-10', paymentMode: 'UPI Net Banking' },
    { id: 2, studentId: 2, rollNo: 'CS2024002', receiptNo: 'REC-2026-002', totalAmount: 95000, paidAmount: 50000, dueAmount: 45000, status: 'Partial', paymentDate: '2026-08-15', paymentMode: 'Demand Draft' },
    { id: 3, studentId: 3, rollNo: 'BCA2024001', receiptNo: 'REC-2026-003', totalAmount: 65000, paidAmount: 65000, dueAmount: 0, status: 'Paid', paymentDate: '2026-08-12', paymentMode: 'Debit Card' },
    { id: 4, studentId: 4, rollNo: 'CS2024003', receiptNo: 'REC-2026-004', totalAmount: 95000, paidAmount: 0, dueAmount: 95000, status: 'Pending', paymentDate: '-', paymentMode: '-' },
    { id: 5, studentId: 5, rollNo: 'BCA2024002', receiptNo: 'REC-2026-005', totalAmount: 65000, paidAmount: 35000, dueAmount: 30000, status: 'Partial', paymentDate: '2026-09-01', paymentMode: 'UPI' }
  ],
  notices: [
    {
      id: 1,
      title: 'End-Semester Theory Examinations Schedule Announced',
      category: 'Examination',
      priority: 'High',
      date: '2026-10-24',
      target: 'All Students',
      content: 'The final semester examinations for Odd Semester 2026-27 will commence on November 15, 2026. Hall tickets will be available for download starting November 05 from the student portal.'
    },
    {
      id: 2,
      title: 'Annual TechFest & Hackathon Registration Open',
      category: 'Events',
      priority: 'Normal',
      date: '2026-10-22',
      target: 'B.Tech & BCA',
      content: 'Registrations are officially open for "CodeForge 2026" - 36 hour national collegiate hackathon with exciting cash prizes and industry internship opportunities.'
    },
    {
      id: 3,
      title: 'Fee Payment Deadline Notice - Odd Semester',
      category: 'Finance',
      priority: 'Urgent',
      date: '2026-10-18',
      target: 'All Students',
      content: 'Students with outstanding tuition fees are reminded to clear all dues before October 31, 2026 to avoid late penalty charges and examination hall ticket restrictions.'
    },
    {
      id: 4,
      title: 'Deepavali & Autumn Break Notification',
      category: 'Holiday',
      priority: 'Normal',
      date: '2026-10-15',
      target: 'All Students & Staff',
      content: 'The University administrative offices and instructional classes will observe autumn vacation from November 01 through November 04, 2026.'
    }
  ]
};

export function loadAppState() {
  try {
    const serialized = localStorage.getItem(STORAGE_KEY);
    if (!serialized) {
      saveAppState(INITIAL_DATA);
      return JSON.parse(JSON.stringify(INITIAL_DATA));
    }
    return JSON.parse(serialized);
  } catch (e) {
    console.error('Failed to load state from localStorage, falling back to seed:', e);
    return JSON.parse(JSON.stringify(INITIAL_DATA));
  }
}

export function saveAppState(state) {
  try {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(state));
  } catch (e) {
    console.error('Failed to save state to localStorage:', e);
  }
}

export function resetAppState() {
  localStorage.removeItem(STORAGE_KEY);
  return loadAppState();
}

export function calculateGrade(marks, max = 100) {
  const pct = (marks / max) * 100;
  if (pct >= 90) return { grade: 'O', label: 'Outstanding', color: 'emerald' };
  if (pct >= 80) return { grade: 'A+', label: 'Excellent', color: 'cyan' };
  if (pct >= 70) return { grade: 'A', label: 'Very Good', color: 'sky' };
  if (pct >= 60) return { grade: 'B+', label: 'Good', color: 'blue' };
  if (pct >= 50) return { grade: 'B', label: 'Above Average', color: 'amber' };
  if (pct >= 40) return { grade: 'C', label: 'Pass', color: 'orange' };
  return { grade: 'F', label: 'Fail', color: 'rose' };
}
