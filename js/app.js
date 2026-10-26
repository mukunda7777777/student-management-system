/**
 * Student Management System (SMS) - Interactive Web Application
 * Handles routing, CRUD operations, state synchronization, and accessible dialogs.
 */

import { loadAppState, saveAppState, resetAppState, calculateGrade } from './data.js';

let state = loadAppState();
let currentView = 'dashboard';

// Initialize on DOM Ready
document.addEventListener('DOMContentLoaded', () => {
  setupNavigation();
  setupDialogs();
  setupEventListeners();
  renderAll();
});

// Toast Notifications Helper
function showToast(message, type = 'success') {
  const container = document.getElementById('toast-container');
  if (!container) return;

  const toast = document.createElement('div');
  toast.className = `toast ${type}`;
  toast.innerHTML = `
    <span>${type === 'success' ? '✅' : '⚠️'}</span>
    <span>${message}</span>
  `;
  container.appendChild(toast);

  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transform = 'translateY(10px)';
    toast.style.transition = 'all 200ms ease-out';
    setTimeout(() => toast.remove(), 200);
  }, 3200);
}

// Navigation & View Routing
function setupNavigation() {
  const navLinks = document.querySelectorAll('.nav-link');
  navLinks.forEach(link => {
    link.addEventListener('click', (e) => {
      e.preventDefault();
      const view = link.getAttribute('data-view');
      if (view) switchView(view);
    });
  });

  // Mobile menu toggle
  const toggleBtn = document.getElementById('btn-toggle-sidebar');
  const sidebar = document.getElementById('sidebar');
  if (toggleBtn && sidebar) {
    toggleBtn.addEventListener('click', () => {
      sidebar.classList.toggle('open');
    });

    // Close sidebar on outer click on mobile
    document.addEventListener('click', (e) => {
      if (window.innerWidth <= 900 && !sidebar.contains(e.target) && e.target !== toggleBtn) {
        sidebar.classList.remove('open');
      }
    });
  }
}

function switchView(viewId) {
  currentView = viewId;

  // Update Nav link active classes
  document.querySelectorAll('.nav-link').forEach(link => {
    link.classList.toggle('active', link.getAttribute('data-view') === viewId);
  });

  // Update Panels
  document.querySelectorAll('.view-panel').forEach(panel => {
    panel.classList.remove('active');
  });

  const activePanel = document.getElementById(`view-${viewId}`);
  if (activePanel) {
    activePanel.classList.add('active');
  }

  // Update top titles
  const titles = {
    'dashboard': { title: 'Executive Dashboard', sub: 'Centurion Academic Management System' },
    'students': { title: 'Student Management', sub: 'Directory, Enrollments & Academic Profiles' },
    'courses': { title: 'Programs & Curricula', sub: 'Degrees, Semesters & Subject Syllabi' },
    'attendance': { title: 'Attendance Register', sub: 'Daily Lecture Rosters & Cohort Tracking' },
    'marks': { title: 'Examinations & Grades', sub: 'Transcript Generation & Performance Analytics' },
    'fees': { title: 'Fee Accounting', sub: 'Invoicing, Collections & Payment Ledgers' },
    'notices': { title: 'Campus Notice Board', sub: 'University Circulars & Student Bulletins' },
    'student-portal': { title: 'Student Personal Portal', sub: 'Your Personal Academic Dashboard' }
  };

  const info = titles[viewId] || { title: 'SMS Portal', sub: 'Academic Management' };
  document.getElementById('top-page-title').textContent = info.title;
  document.getElementById('top-page-subtitle').textContent = info.sub;

  // Render specific views on demand
  if (viewId === 'students') renderStudents();
  if (viewId === 'courses') renderCourses();
  if (viewId === 'attendance') renderAttendance();
  if (viewId === 'marks') renderMarks();
  if (viewId === 'fees') renderFees();
  if (viewId === 'notices') renderNotices();
  if (viewId === 'student-portal') renderStudentPortal();
  if (viewId === 'dashboard') renderDashboard();
}

/* ==========================================================================
   Accessible Native <dialog> Management with modern-web-guidance compliance
   ========================================================================== */

function setupDialogs() {
  const dialogs = document.querySelectorAll('dialog');

  dialogs.forEach(dialog => {
    // Cross-browser fallback for light-dismiss (clicking the backdrop)
    if (!('closedBy' in HTMLDialogElement.prototype)) {
      dialog.addEventListener('click', (event) => {
        if (event.target !== dialog) return;
        const rect = dialog.getBoundingClientRect();
        const isDialogContent = (
          rect.top <= event.clientY &&
          event.clientY <= rect.top + rect.height &&
          rect.left <= event.clientX &&
          event.clientX <= rect.left + rect.width
        );
        if (!isDialogContent) dialog.close();
      });
    }
  });

  // Close buttons via data-close-dialog attribute
  document.querySelectorAll('[data-close-dialog]').forEach(btn => {
    btn.addEventListener('click', () => {
      const targetId = btn.getAttribute('data-close-dialog');
      const dialog = document.getElementById(targetId);
      if (dialog) dialog.close();
    });
  });
}

function openModal(dialogId) {
  const dialog = document.getElementById(dialogId);
  if (dialog) {
    dialog.showModal();
  }
}

function closeModal(dialogId) {
  const dialog = document.getElementById(dialogId);
  if (dialog) {
    dialog.close();
  }
}

// Global Event Listeners
function setupEventListeners() {
  // Quick Actions from Dashboard
  document.getElementById('btn-quick-add-student')?.addEventListener('click', () => openStudentForm());
  document.getElementById('btn-quick-mark-attendance')?.addEventListener('click', () => switchView('attendance'));
  document.getElementById('btn-quick-record-marks')?.addEventListener('click', () => openMarksForm());
  document.getElementById('btn-quick-record-fee')?.addEventListener('click', () => openFeeForm());
  document.getElementById('btn-quick-new-notice')?.addEventListener('click', () => openNoticeForm());
  document.getElementById('btn-view-all-students')?.addEventListener('click', () => switchView('students'));

  // Module Action Buttons
  document.getElementById('btn-open-add-student')?.addEventListener('click', () => openStudentForm());
  document.getElementById('btn-open-record-marks')?.addEventListener('click', () => openMarksForm());
  document.getElementById('btn-open-collect-fee')?.addEventListener('click', () => openFeeForm());
  document.getElementById('btn-open-post-notice')?.addEventListener('click', () => openNoticeForm());
  document.getElementById('btn-open-add-course')?.addEventListener('click', () => {
    showToast('Program catalog is verified for the academic year 2026-27.', 'success');
  });

  // Switch Account Dialog
  document.getElementById('btn-switch-account')?.addEventListener('click', () => {
    openModal('switchRoleDialog');
  });

  document.querySelectorAll('.switch-account-option').forEach(btn => {
    btn.addEventListener('click', () => {
      const role = btn.getAttribute('data-role');
      const username = btn.getAttribute('data-username');
      switchAccount(role, username);
      closeModal('switchRoleDialog');
    });
  });

  // Reset Demo Data
  document.getElementById('btn-reset-demo')?.addEventListener('click', () => {
    if (confirm('Reset database to original sample records? Any custom additions will be cleared.')) {
      state = resetAppState();
      renderAll();
      showToast('Database reset to initial sample data!', 'success');
    }
  });

  // Search input
  document.getElementById('global-search-input')?.addEventListener('input', (e) => {
    const query = e.target.value.toLowerCase().trim();
    if (currentView !== 'students') {
      switchView('students');
    }
    filterStudents(query);
  });

  // Student Filters
  document.getElementById('student-course-filter')?.addEventListener('change', () => filterStudents());
  document.getElementById('student-status-filter')?.addEventListener('change', () => filterStudents());

  // Student Form Submit
  document.getElementById('studentForm')?.addEventListener('submit', (e) => {
    e.preventDefault();
    saveStudent();
  });

  // Attendance Controls
  document.getElementById('attendance-course-select')?.addEventListener('change', () => renderAttendance());
  document.getElementById('btn-mark-all-present')?.addEventListener('click', () => setAllAttendance('Present'));
  document.getElementById('btn-mark-all-absent')?.addEventListener('click', () => setAllAttendance('Absent'));
  document.getElementById('btn-save-attendance')?.addEventListener('click', () => saveAttendanceChanges());

  // Marks Form Submit & Controls
  document.getElementById('marks-student-select')?.addEventListener('change', (e) => renderMarks(e.target.value));
  document.getElementById('marksForm')?.addEventListener('submit', (e) => {
    e.preventDefault();
    saveMarks();
  });
  document.getElementById('btn-print-marks')?.addEventListener('click', () => window.print());
  document.getElementById('btn-print-portal-transcript')?.addEventListener('click', () => window.print());

  // Fee Form Submit & Controls
  document.getElementById('form-fee-student')?.addEventListener('change', (e) => {
    const studentId = parseInt(e.target.value);
    const feeRecord = state.fees.find(f => f.studentId === studentId);
    if (feeRecord) {
      document.getElementById('form-fee-total').value = feeRecord.totalAmount;
      document.getElementById('form-fee-pay-amount').max = feeRecord.dueAmount;
    }
  });

  document.getElementById('feeForm')?.addEventListener('submit', (e) => {
    e.preventDefault();
    saveFeePayment();
  });

  // Notice Form Submit
  document.getElementById('noticeForm')?.addEventListener('submit', (e) => {
    e.preventDefault();
    saveNotice();
  });
}

// User & Role Switching
function switchAccount(role, username) {
  if (role === 'ADMIN') {
    state.currentUser = {
      username: 'admin',
      name: 'System Administrator',
      role: 'ADMIN',
      avatar: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150&auto=format&fit=crop&q=80'
    };
    saveAppState(state);
    updateUserUI();
    showToast('Switched to Administrator profile', 'success');
    switchView('dashboard');
  } else {
    const student = state.students.find(s => s.rollNo === username);
    if (student) {
      state.currentUser = {
        username: student.rollNo,
        name: student.name,
        role: 'STUDENT',
        avatar: student.avatar,
        studentId: student.id
      };
      saveAppState(state);
      updateUserUI();
      showToast(`Switched to Student portal: ${student.name}`, 'success');
      switchView('student-portal');
    }
  }
}

function updateUserUI() {
  const avatar = document.getElementById('sidebar-user-avatar');
  const name = document.getElementById('sidebar-user-name');
  const roleTag = document.getElementById('sidebar-user-role');

  if (avatar) avatar.src = state.currentUser.avatar;
  if (name) name.textContent = state.currentUser.name;
  if (roleTag) roleTag.textContent = `Role: ${state.currentUser.role}`;

  // Badge counters
  document.getElementById('badge-students-count').textContent = state.students.length;
  document.getElementById('badge-notices-count').textContent = state.notices.length;
}

// Render All Components
function renderAll() {
  updateUserUI();
  populateDropdowns();
  renderDashboard();
  renderStudents();
  renderCourses();
  renderAttendance();
  renderMarks();
  renderFees();
  renderNotices();
  renderStudentPortal();
}

// Populate Course & Student select dropdowns
function populateDropdowns() {
  // Course filters
  const courseFilter = document.getElementById('student-course-filter');
  const formStudentCourse = document.getElementById('form-student-course');
  const attCourse = document.getElementById('attendance-course-select');

  if (courseFilter && formStudentCourse && attCourse) {
    courseFilter.innerHTML = '<option value="ALL">All Programs</option>';
    formStudentCourse.innerHTML = '';
    attCourse.innerHTML = '';

    state.courses.forEach(c => {
      courseFilter.innerHTML += `<option value="${c.code}">${c.code} - ${c.name}</option>`;
      formStudentCourse.innerHTML += `<option value="${c.id}">${c.name} (${c.code})</option>`;
      attCourse.innerHTML += `<option value="${c.code}">${c.code} - ${c.name}</option>`;
    });
  }

  // Marks student select & Fee student select
  const marksSelect = document.getElementById('marks-student-select');
  const formMarksStudent = document.getElementById('form-marks-student');
  const formFeeStudent = document.getElementById('form-fee-student');

  if (marksSelect && formMarksStudent && formFeeStudent) {
    marksSelect.innerHTML = '';
    formMarksStudent.innerHTML = '';
    formFeeStudent.innerHTML = '';

    state.students.forEach(s => {
      marksSelect.innerHTML += `<option value="${s.rollNo}">${s.name} (${s.rollNo})</option>`;
      formMarksStudent.innerHTML += `<option value="${s.id}">${s.name} (${s.rollNo})</option>`;
      formFeeStudent.innerHTML += `<option value="${s.id}">${s.name} (${s.rollNo}) - Due: ₹${getStudentDue(s.id).toLocaleString()}</option>`;
    });
  }

  // Populate subjects for marks dialog
  const formMarksSubject = document.getElementById('form-marks-subject');
  if (formMarksSubject) {
    formMarksSubject.innerHTML = '';
    state.subjects.forEach(sub => {
      formMarksSubject.innerHTML += `<option value="${sub.code}">${sub.code} - ${sub.name}</option>`;
    });
  }

  // Attendance subject select
  const attSubject = document.getElementById('attendance-subject-select');
  if (attSubject) {
    attSubject.innerHTML = '';
    state.subjects.forEach(sub => {
      attSubject.innerHTML += `<option value="${sub.code}">${sub.code} - ${sub.name}</option>`;
    });
  }
}

function getStudentDue(studentId) {
  const record = state.fees.find(f => f.studentId === studentId);
  return record ? record.dueAmount : 0;
}

// 1. Dashboard View
function renderDashboard() {
  // Total Students
  document.getElementById('kpi-total-students').textContent = state.students.length;
  // Total Courses
  document.getElementById('kpi-total-courses').textContent = state.courses.length;

  // Fees calculation
  const totalPaid = state.fees.reduce((acc, f) => acc + (f.paidAmount || 0), 0);
  const totalDue = state.fees.reduce((acc, f) => acc + (f.dueAmount || 0), 0);
  document.getElementById('kpi-total-fees').textContent = `₹${totalPaid.toLocaleString('en-IN')}`;
  document.getElementById('kpi-pending-fees').textContent = `₹${totalDue.toLocaleString('en-IN')} pending dues`;

  // Average attendance calculation
  const presentCount = state.attendance.filter(a => a.status === 'Present').length;
  const totalRecords = state.attendance.length || 1;
  const avgAtt = Math.round((presentCount / totalRecords) * 100);
  document.getElementById('kpi-avg-attendance').textContent = `${avgAtt}%`;

  // Recent Students table
  const tbody = document.getElementById('dashboard-recent-students-body');
  if (!tbody) return;

  tbody.innerHTML = '';
  state.students.slice(0, 4).forEach(s => {
    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td><strong>${s.rollNo}</strong></td>
      <td>
        <div class="student-cell">
          <img src="${s.avatar}" alt="${s.name}">
          <div class="student-meta">
            <div class="name">${s.name}</div>
            <div class="sub">${s.gender}</div>
          </div>
        </div>
      </td>
      <td><span class="status-pill cyan">${s.courseCode}</span></td>
      <td>${s.email}</td>
      <td><span class="status-pill success">${s.status}</span></td>
    `;
    tbody.appendChild(tr);
  });
}

// 2. Student Directory View
function renderStudents(filteredList = null) {
  const list = filteredList || state.students;
  const tbody = document.getElementById('students-table-body');
  if (!tbody) return;

  tbody.innerHTML = '';
  if (list.length === 0) {
    tbody.innerHTML = `<tr><td colspan="6" style="text-align: center; padding: 2rem; color: var(--text-dim);">No students found matching current criteria.</td></tr>`;
    return;
  }

  list.forEach(s => {
    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td>
        <div class="student-cell">
          <img src="${s.avatar}" alt="${s.name}">
          <div class="student-meta">
            <div class="name">${s.name}</div>
            <div class="sub">${s.gender} | DOB: ${s.dob}</div>
          </div>
        </div>
      </td>
      <td><strong style="color: var(--accent-cyan);">${s.rollNo}</strong></td>
      <td>
        <div style="font-weight: 500;">${s.courseCode}</div>
        <div style="font-size: 0.75rem; color: var(--text-dim);">Semester ${s.semester}</div>
      </td>
      <td>
        <div>${s.email}</div>
        <div style="font-size: 0.75rem; color: var(--text-dim);">${s.phone}</div>
      </td>
      <td>
        <span class="status-pill ${s.status === 'Active' ? 'success' : 'warning'}">${s.status}</span>
      </td>
      <td>
        <div style="display: flex; gap: 0.35rem;">
          <button class="table-btn edit-student-btn" data-id="${s.id}" title="Edit Student">✏️</button>
          <button class="table-btn delete delete-student-btn" data-id="${s.id}" title="Remove Student">🗑️</button>
        </div>
      </td>
    `;
    tbody.appendChild(tr);
  });

  // Bind edit/delete actions
  tbody.querySelectorAll('.edit-student-btn').forEach(btn => {
    btn.addEventListener('click', () => {
      const id = parseInt(btn.getAttribute('data-id'));
      openStudentForm(id);
    });
  });

  tbody.querySelectorAll('.delete-student-btn').forEach(btn => {
    btn.addEventListener('click', () => {
      const id = parseInt(btn.getAttribute('data-id'));
      deleteStudent(id);
    });
  });
}

function filterStudents(queryParam = null) {
  const query = queryParam !== null ? queryParam : (document.getElementById('global-search-input')?.value.toLowerCase().trim() || '');
  const courseFilter = document.getElementById('student-course-filter')?.value || 'ALL';
  const statusFilter = document.getElementById('student-status-filter')?.value || 'ALL';

  const filtered = state.students.filter(s => {
    const matchesSearch = !query || s.name.toLowerCase().includes(query) || s.rollNo.toLowerCase().includes(query) || s.email.toLowerCase().includes(query);
    const matchesCourse = courseFilter === 'ALL' || s.courseCode === courseFilter;
    const matchesStatus = statusFilter === 'ALL' || s.status === statusFilter;
    return matchesSearch && matchesCourse && matchesStatus;
  });

  renderStudents(filtered);
}

function openStudentForm(studentId = null) {
  const dialog = document.getElementById('studentDialog');
  const form = document.getElementById('studentForm');
  const title = document.getElementById('studentDialogTitle');
  const editIdInput = document.getElementById('student-edit-id');

  if (studentId) {
    const student = state.students.find(s => s.id === studentId);
    if (!student) return;
    title.textContent = 'Edit Student Details';
    editIdInput.value = student.id;
    document.getElementById('form-student-roll').value = student.rollNo;
    document.getElementById('form-student-name').value = student.name;
    document.getElementById('form-student-course').value = student.courseId;
    document.getElementById('form-student-sem').value = student.semester;
    document.getElementById('form-student-email').value = student.email;
    document.getElementById('form-student-phone').value = student.phone;
    document.getElementById('form-student-gender').value = student.gender;
    document.getElementById('form-student-dob').value = student.dob;
    document.getElementById('form-student-address').value = student.address || '';
  } else {
    title.textContent = 'Register New Student';
    form.reset();
    editIdInput.value = '';
    // Auto-generate next roll number
    const nextNum = state.students.length + 1;
    document.getElementById('form-student-roll').value = `CS202400${nextNum}`;
  }

  openModal('studentDialog');
}

function saveStudent() {
  const editId = document.getElementById('student-edit-id').value;
  const courseId = parseInt(document.getElementById('form-student-course').value);
  const course = state.courses.find(c => c.id === courseId);

  const studentData = {
    rollNo: document.getElementById('form-student-roll').value.trim(),
    name: document.getElementById('form-student-name').value.trim(),
    courseId: courseId,
    courseCode: course ? course.code : 'BTECH',
    semester: parseInt(document.getElementById('form-student-sem').value) || 1,
    email: document.getElementById('form-student-email').value.trim(),
    phone: document.getElementById('form-student-phone').value.trim(),
    gender: document.getElementById('form-student-gender').value,
    dob: document.getElementById('form-student-dob').value,
    address: document.getElementById('form-student-address').value.trim(),
    status: 'Active',
    avatar: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150&auto=format&fit=crop&q=80'
  };

  if (editId) {
    const index = state.students.findIndex(s => s.id === parseInt(editId));
    if (index !== -1) {
      state.students[index] = { ...state.students[index], ...studentData };
      showToast(`Student ${studentData.name} updated successfully!`);
    }
  } else {
    const newId = (state.students.reduce((max, s) => Math.max(max, s.id), 0) || 0) + 1;
    const newStudent = { id: newId, ...studentData };
    state.students.push(newStudent);

    // Initialize blank fee record for new student
    state.fees.push({
      id: state.fees.length + 1,
      studentId: newId,
      rollNo: newStudent.rollNo,
      receiptNo: `REC-2026-00${state.fees.length + 1}`,
      totalAmount: course ? course.feePerYear : 80000,
      paidAmount: 0,
      dueAmount: course ? course.feePerYear : 80000,
      status: 'Pending',
      paymentDate: '-',
      paymentMode: '-'
    });

    showToast(`Student ${studentData.name} registered successfully!`);
  }

  saveAppState(state);
  closeModal('studentDialog');
  renderAll();
}

function deleteStudent(studentId) {
  const student = state.students.find(s => s.id === studentId);
  if (!student) return;

  if (confirm(`Are you sure you want to delete ${student.name} (${student.rollNo})?`)) {
    state.students = state.students.filter(s => s.id !== studentId);
    state.attendance = state.attendance.filter(a => a.studentId !== studentId);
    state.marks = state.marks.filter(m => m.studentId !== studentId);
    state.fees = state.fees.filter(f => f.studentId !== studentId);
    saveAppState(state);
    renderAll();
    showToast(`Student ${student.name} removed from registry.`, 'success');
  }
}

// 3. Courses & Subjects View
function renderCourses() {
  const container = document.getElementById('courses-grid');
  if (!container) return;

  container.innerHTML = '';
  state.courses.forEach(c => {
    const card = document.createElement('div');
    card.className = 'notice-card';
    card.innerHTML = `
      <div class="notice-top">
        <span class="status-pill cyan">${c.code}</span>
        <span style="font-size: 0.75rem; color: var(--text-dim);">${c.durationYears} Years</span>
      </div>
      <h4>${c.name}</h4>
      <p>Annual Tuition: <strong style="color: #38BDF8;">₹${c.feePerYear.toLocaleString('en-IN')}</strong> / year</p>
      <div class="notice-footer">
        <span>Affiliation: Centurion University</span>
        <span class="status-pill success">Approved</span>
      </div>
    `;
    container.appendChild(card);
  });

  const subBody = document.getElementById('subjects-table-body');
  if (!subBody) return;
  subBody.innerHTML = '';
  state.subjects.forEach(sub => {
    const course = state.courses.find(c => c.id === sub.courseId);
    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td><strong style="color: var(--accent-cyan);">${sub.code}</strong></td>
      <td style="font-weight: 600;">${sub.name}</td>
      <td><span class="status-pill cyan">${course ? course.code : 'UG'}</span></td>
      <td>Semester ${sub.semester}</td>
      <td><span style="font-weight: 700; color: #10B981;">${sub.maxMarks} Marks</span></td>
    `;
    subBody.appendChild(tr);
  });
}

// 4. Attendance View
function renderAttendance() {
  const dateInput = document.getElementById('attendance-date');
  if (dateInput && !dateInput.value) {
    dateInput.value = new Date().toISOString().split('T')[0];
  }

  const selectedCourse = document.getElementById('attendance-course-select')?.value || 'BTECH-CSE';
  const tbody = document.getElementById('attendance-table-body');
  if (!tbody) return;

  tbody.innerHTML = '';
  const students = state.students.filter(s => s.courseCode === selectedCourse);

  if (students.length === 0) {
    tbody.innerHTML = `<tr><td colspan="5" style="text-align: center; padding: 2rem; color: var(--text-dim);">No students in this course program.</td></tr>`;
    return;
  }

  students.forEach(s => {
    const studentRecords = state.attendance.filter(a => a.studentId === s.id);
    const presentRecords = studentRecords.filter(a => a.status === 'Present').length;
    const total = studentRecords.length || 1;
    const pct = Math.round((presentRecords / total) * 100);

    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td><strong>${s.rollNo}</strong></td>
      <td>
        <div class="student-cell">
          <img src="${s.avatar}" alt="${s.name}">
          <div class="name">${s.name}</div>
        </div>
      </td>
      <td><span class="status-pill cyan">${s.courseCode}</span></td>
      <td>
        <div style="display: flex; align-items: center; gap: 0.5rem;">
          <div style="flex: 1; height: 6px; background: rgba(255,255,255,0.08); border-radius: 4px; overflow: hidden;">
            <div style="width: ${pct}%; height: 100%; background: ${pct >= 75 ? '#10B981' : '#F43F5E'};"></div>
          </div>
          <span style="font-weight: 600; font-size: 0.8rem; min-width: 38px;">${pct}%</span>
        </div>
      </td>
      <td>
        <div style="display: flex; gap: 0.5rem;" data-student-id="${s.id}">
          <button class="btn btn-sm btn-att-toggle btn-success active" data-status="Present">Present</button>
          <button class="btn btn-sm btn-att-toggle btn-secondary" data-status="Absent">Absent</button>
        </div>
      </td>
    `;
    tbody.appendChild(tr);
  });

  // Bind status toggles
  tbody.querySelectorAll('.btn-att-toggle').forEach(btn => {
    btn.addEventListener('click', (e) => {
      const parent = e.target.closest('div');
      parent.querySelectorAll('.btn-att-toggle').forEach(b => {
        b.classList.remove('btn-success', 'btn-danger');
        b.classList.add('btn-secondary');
      });

      const isPresent = btn.getAttribute('data-status') === 'Present';
      btn.classList.remove('btn-secondary');
      btn.classList.add(isPresent ? 'btn-success' : 'btn-danger');
    });
  });
}

function setAllAttendance(targetStatus) {
  document.querySelectorAll('#attendance-table-body tr').forEach(tr => {
    const buttons = tr.querySelectorAll('.btn-att-toggle');
    buttons.forEach(b => {
      b.classList.remove('btn-success', 'btn-danger');
      b.classList.add('btn-secondary');
      if (b.getAttribute('data-status') === targetStatus) {
        b.classList.remove('btn-secondary');
        b.classList.add(targetStatus === 'Present' ? 'btn-success' : 'btn-danger');
      }
    });
  });
  showToast(`Marked all students as ${targetStatus}`);
}

function saveAttendanceChanges() {
  const date = document.getElementById('attendance-date').value;
  const subject = document.getElementById('attendance-subject-select').value;

  document.querySelectorAll('#attendance-table-body tr').forEach(tr => {
    const studentCell = tr.querySelector('[data-student-id]');
    if (!studentCell) return;
    const studentId = parseInt(studentCell.getAttribute('data-student-id'));
    const student = state.students.find(s => s.id === studentId);
    const activeBtn = studentCell.querySelector('.btn-success, .btn-danger');
    const status = activeBtn ? activeBtn.getAttribute('data-status') : 'Present';

    state.attendance.push({
      id: state.attendance.length + 1,
      studentId: studentId,
      rollNo: student ? student.rollNo : 'N/A',
      date: date,
      status: status,
      subject: subject
    });
  });

  saveAppState(state);
  showToast('Attendance records saved successfully!');
  renderDashboard();
  renderAttendance();
}

// 5. Marks & Examinations View
function renderMarks(selectedRollNo = null) {
  const select = document.getElementById('marks-student-select');
  const rollNo = selectedRollNo || (select ? select.value : (state.students[0]?.rollNo || 'CS2024001'));

  const tbody = document.getElementById('marks-table-body');
  if (!tbody) return;

  const records = state.marks.filter(m => m.rollNo === rollNo);
  tbody.innerHTML = '';

  if (records.length === 0) {
    tbody.innerHTML = `<tr><td colspan="7" style="text-align: center; padding: 2rem; color: var(--text-dim);">No examination records found for this student.</td></tr>`;
    return;
  }

  records.forEach(m => {
    const grade = calculateGrade(m.marksObtained, m.maxMarks);
    const pct = Math.round((m.marksObtained / m.maxMarks) * 100);

    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td><strong>${m.subjectCode}</strong></td>
      <td>${m.subjectName}</td>
      <td>Semester ${m.semester}</td>
      <td><span style="font-weight: 700; color: #38BDF8;">${m.marksObtained}</span></td>
      <td>${m.maxMarks}</td>
      <td>${pct}%</td>
      <td><span class="status-pill ${grade.color === 'emerald' ? 'success' : (grade.color === 'rose' ? 'danger' : 'cyan')}">${grade.grade} (${grade.label})</span></td>
    `;
    tbody.appendChild(tr);
  });
}

function openMarksForm() {
  const form = document.getElementById('marksForm');
  form.reset();
  openModal('marksDialog');
}

function saveMarks() {
  const studentId = parseInt(document.getElementById('form-marks-student').value);
  const student = state.students.find(s => s.id === studentId);
  const subjectCode = document.getElementById('form-marks-subject').value;
  const subject = state.subjects.find(sub => sub.code === subjectCode);

  const newMark = {
    id: state.marks.length + 1,
    studentId: studentId,
    rollNo: student ? student.rollNo : 'N/A',
    subjectCode: subjectCode,
    subjectName: subject ? subject.name : subjectCode,
    marksObtained: parseInt(document.getElementById('form-marks-obtained').value),
    maxMarks: parseInt(document.getElementById('form-marks-max').value),
    examType: document.getElementById('form-marks-exam-type').value,
    semester: student ? student.semester : 1
  };

  state.marks.push(newMark);
  saveAppState(state);
  closeModal('marksDialog');
  renderMarks(student?.rollNo);
  showToast('Examination marks logged successfully!');
}

// 6. Fees Ledger View
function renderFees() {
  const tbody = document.getElementById('fees-table-body');
  if (!tbody) return;

  tbody.innerHTML = '';
  state.fees.forEach(f => {
    const student = state.students.find(s => s.id === f.studentId);
    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td><strong style="color: var(--accent-cyan);">${f.receiptNo}</strong></td>
      <td>${f.rollNo}</td>
      <td style="font-weight: 600;">${student ? student.name : 'Enrolled Student'}</td>
      <td>₹${f.totalAmount.toLocaleString('en-IN')}</td>
      <td style="color: #10B981; font-weight: 600;">₹${f.paidAmount.toLocaleString('en-IN')}</td>
      <td style="color: #F43F5E; font-weight: 600;">₹${f.dueAmount.toLocaleString('en-IN')}</td>
      <td>${f.paymentMode}</td>
      <td>
        <span class="status-pill ${f.status === 'Paid' ? 'success' : (f.status === 'Partial' ? 'warning' : 'danger')}">${f.status}</span>
      </td>
      <td>
        <button class="btn btn-sm btn-secondary" onclick="window.print()" title="Print Receipt">🖨️ Receipt</button>
      </td>
    `;
    tbody.appendChild(tr);
  });
}

function openFeeForm() {
  const form = document.getElementById('feeForm');
  form.reset();
  const firstStudent = state.students[0];
  if (firstStudent) {
    const fee = state.fees.find(f => f.studentId === firstStudent.id);
    if (fee) {
      document.getElementById('form-fee-total').value = fee.totalAmount;
      document.getElementById('form-fee-pay-amount').value = fee.dueAmount > 0 ? fee.dueAmount : 0;
    }
  }
  openModal('feeDialog');
}

function saveFeePayment() {
  const studentId = parseInt(document.getElementById('form-fee-student').value);
  const payAmount = parseInt(document.getElementById('form-fee-pay-amount').value);
  const mode = document.getElementById('form-fee-mode').value;

  const feeRecord = state.fees.find(f => f.studentId === studentId);
  if (feeRecord) {
    feeRecord.paidAmount += payAmount;
    feeRecord.dueAmount = Math.max(0, feeRecord.totalAmount - feeRecord.paidAmount);
    feeRecord.status = feeRecord.dueAmount === 0 ? 'Paid' : 'Partial';
    feeRecord.paymentDate = new Date().toISOString().split('T')[0];
    feeRecord.paymentMode = mode;
  }

  saveAppState(state);
  closeModal('feeDialog');
  renderFees();
  renderDashboard();
  showToast(`Receipt generated! Payment of ₹${payAmount.toLocaleString('en-IN')} recorded.`);
}

// 7. Notices View
function renderNotices() {
  const container = document.getElementById('notices-cards-container');
  if (!container) return;

  container.innerHTML = '';
  state.notices.forEach(n => {
    const card = document.createElement('article');
    card.className = 'notice-card';
    card.innerHTML = `
      <div class="notice-top">
        <span class="status-pill ${n.priority === 'Urgent' ? 'danger' : (n.priority === 'High' ? 'warning' : 'cyan')}">${n.category}</span>
        <span style="font-size: 0.75rem; color: var(--text-dim);">${n.date}</span>
      </div>
      <h4>${n.title}</h4>
      <p>${n.content}</p>
      <div class="notice-footer">
        <span>Audience: <strong>${n.target}</strong></span>
        <span style="color: var(--accent-cyan); font-weight: 600;">Authorized</span>
      </div>
    `;
    container.appendChild(card);
  });
}

function openNoticeForm() {
  const form = document.getElementById('noticeForm');
  form.reset();
  openModal('noticeDialog');
}

function saveNotice() {
  const newNotice = {
    id: state.notices.length + 1,
    title: document.getElementById('form-notice-title').value.trim(),
    category: document.getElementById('form-notice-category').value,
    priority: document.getElementById('form-notice-priority').value,
    date: new Date().toISOString().split('T')[0],
    target: document.getElementById('form-notice-target').value.trim(),
    content: document.getElementById('form-notice-content').value.trim()
  };

  state.notices.unshift(newNotice);
  saveAppState(state);
  closeModal('noticeDialog');
  renderNotices();
  document.getElementById('badge-notices-count').textContent = state.notices.length;
  showToast('Institutional circular posted successfully!');
}

// 8. Student Personal Portal View
function renderStudentPortal() {
  const student = state.currentUser.role === 'STUDENT'
    ? (state.students.find(s => s.rollNo === state.currentUser.username) || state.students[0])
    : state.students[0];

  if (!student) return;

  // Hero Card
  document.getElementById('portal-hero-avatar').src = student.avatar;
  document.getElementById('portal-hero-name').textContent = student.name;
  document.getElementById('portal-hero-roll').textContent = `Roll: ${student.rollNo}`;
  document.getElementById('portal-hero-course').textContent = `${student.courseCode} - Sem ${student.semester}`;

  // Student Marks calculation
  const marks = state.marks.filter(m => m.studentId === student.id);
  const totalScored = marks.reduce((acc, m) => acc + m.marksObtained, 0);
  const totalMax = marks.reduce((acc, m) => acc + m.maxMarks, 0) || 1;
  const avgPct = Math.round((totalScored / totalMax) * 100);
  document.getElementById('portal-stat-cgpa').textContent = `${avgPct}%`;

  // Student Attendance calculation
  const att = state.attendance.filter(a => a.studentId === student.id);
  const present = att.filter(a => a.status === 'Present').length;
  const attPct = att.length ? Math.round((present / att.length) * 100) : 100;
  document.getElementById('portal-stat-attendance').textContent = `${attPct}%`;

  // Student Fees calculation
  const fee = state.fees.find(f => f.studentId === student.id);
  if (fee) {
    document.getElementById('portal-stat-fee').textContent = fee.status === 'Paid' ? 'Paid in Full' : `Due: ₹${fee.dueAmount.toLocaleString('en-IN')}`;
    document.getElementById('portal-stat-due').textContent = `Total Paid: ₹${fee.paidAmount.toLocaleString('en-IN')} of ₹${fee.totalAmount.toLocaleString('en-IN')}`;
  }

  // Marks Table
  const marksBody = document.getElementById('portal-marks-table-body');
  if (marksBody) {
    marksBody.innerHTML = '';
    marks.forEach(m => {
      const grade = calculateGrade(m.marksObtained, m.maxMarks);
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td><strong>${m.subjectCode}</strong></td>
        <td>${m.subjectName}</td>
        <td>Semester ${m.semester}</td>
        <td style="font-weight: 700; color: #38BDF8;">${m.marksObtained}</td>
        <td>${m.maxMarks}</td>
        <td><span class="status-pill ${grade.color === 'emerald' ? 'success' : 'cyan'}">${grade.grade}</span></td>
      `;
      marksBody.appendChild(tr);
    });
  }

  // Fees Table
  const feeBody = document.getElementById('portal-fees-table-body');
  if (feeBody && fee) {
    feeBody.innerHTML = `
      <tr>
        <td><strong>${fee.receiptNo}</strong></td>
        <td>₹${fee.totalAmount.toLocaleString('en-IN')}</td>
        <td style="color: #10B981; font-weight: 600;">₹${fee.paidAmount.toLocaleString('en-IN')}</td>
        <td style="color: #F43F5E; font-weight: 600;">₹${fee.dueAmount.toLocaleString('en-IN')}</td>
        <td>${fee.paymentDate}</td>
        <td>${fee.paymentMode}</td>
        <td><span class="status-pill ${fee.status === 'Paid' ? 'success' : 'warning'}">${fee.status}</span></td>
      </tr>
    `;
  }
}
