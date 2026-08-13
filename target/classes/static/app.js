/* ============================================================
   Ward Board — app.js
   Vanilla JS SPA talking to the Spring Boot REST API.
   ============================================================ */
(() => {
  const API = '/api';

  const state = {
    page: 'dashboard',
    beds: [],
    reservations: [],
    patients: [],
    users: [],
  };

  const PAGE_META = {
    dashboard:    { eyebrow: '01 · ภาพรวม',    title: 'ภาพรวมวอร์ด' },
    beds:         { eyebrow: '02 · เตียง',      title: 'ผังเตียงทั้งหมด' },
    reservations: { eyebrow: '03 · การจอง',    title: 'คำขอจองเตียง' },
    patients:     { eyebrow: '04 · ผู้ป่วย',    title: 'รายชื่อผู้ป่วย' },
    users:        { eyebrow: '05 · ผู้ใช้',     title: 'ผู้ใช้ระบบ' },
  };

  const QUICK_ADD = {
    dashboard: null,
    beds: 'เพิ่มเตียง',
    reservations: 'สร้างคำขอจอง',
    patients: 'เพิ่มผู้ป่วย',
    users: 'เพิ่มผู้ใช้',
  };

  // ---------- generic fetch helper ----------
  async function api(path, opts = {}) {
    const res = await fetch(API + path, {
      headers: { 'Content-Type': 'application/json' },
      ...opts,
    });
    setConn(true);
    if (res.status === 204) return null;
    const text = await res.text();
    let data = null;
    try { data = text ? JSON.parse(text) : null; } catch { data = text; }
    if (!res.ok) {
      const msg = (data && data.message) || (typeof data === 'string' ? data : null) || `คำขอล้มเหลว (HTTP ${res.status})`;
      throw new Error(msg);
    }
    return data;
  }

  function setConn(ok) {
    const el = document.getElementById('connStatus');
    const dot = el.querySelector('.dot');
    dot.classList.toggle('is-down', !ok);
    el.lastChild.textContent = ok ? ' เชื่อมต่อ API' : ' ขาดการเชื่อมต่อ';
  }

  // ---------- toasts ----------
  function toast(message, type = 'success') {
    const stack = document.getElementById('toastStack');
    const el = document.createElement('div');
    el.className = `toast is-${type}`;
    el.textContent = message;
    stack.appendChild(el);
    setTimeout(() => {
      el.style.opacity = '0';
      el.style.transition = 'opacity .2s ease';
      setTimeout(() => el.remove(), 200);
    }, 3200);
  }

  // ---------- navigation ----------
  function goTo(page) {
    state.page = page;
    document.querySelectorAll('.nav-item').forEach(b => b.classList.toggle('is-active', b.dataset.page === page));
    document.querySelectorAll('.page').forEach(p => p.classList.toggle('is-active', p.id === `page-${page}`));
    document.getElementById('pageEyebrow').textContent = PAGE_META[page].eyebrow;
    document.getElementById('pageTitle').textContent = PAGE_META[page].title;
    const qa = document.getElementById('quickAddBtn');
    const label = QUICK_ADD[page];
    if (label) {
      qa.hidden = false;
      document.getElementById('quickAddLabel').textContent = label;
    } else {
      qa.hidden = true;
    }
    closeSidebar();
    loadPage(page);
  }

  function loadPage(page) {
    if (page === 'dashboard') loadDashboard();
    if (page === 'beds') loadBeds();
    if (page === 'reservations') loadReservations();
    if (page === 'patients') loadPatients();
    if (page === 'users') loadUsers();
  }

  // ---------- sidebar (mobile) ----------
  const sidebar = document.getElementById('sidebar');
  const scrim = document.getElementById('scrim');
  function openSidebar() { sidebar.classList.add('is-open'); scrim.classList.add('is-open'); }
  function closeSidebar() { sidebar.classList.remove('is-open'); scrim.classList.remove('is-open'); }
  document.getElementById('burger').addEventListener('click', openSidebar);
  scrim.addEventListener('click', closeSidebar);

  document.querySelectorAll('.nav-item').forEach(btn => {
    btn.addEventListener('click', () => goTo(btn.dataset.page));
  });

  // ============================================================
  // DASHBOARD
  // ============================================================
  async function loadDashboard() {
    try {
      const [beds, reservations] = await Promise.all([
        api('/beds').catch(() => []),
        api('/reservations').catch(() => []),
      ]);
      state.beds = beds || [];
      state.reservations = reservations || [];
      renderDashboard();
      updateReservationBadge();
    } catch (e) {
      toast(e.message, 'error');
    }
  }

  function renderDashboard() {
    const total = state.beds.length;
    const free = state.beds.filter(b => b.status === 'ว่าง').length;
    const occupied = state.beds.filter(b => b.status === 'ไม่ว่าง').length;
    const pending = state.reservations.filter(r => r.status === 'รออนุมัติ').length;

    document.getElementById('statTotal').textContent = total;
    document.getElementById('statFree').textContent = free;
    document.getElementById('statOccupied').textContent = occupied;
    document.getElementById('statPending').textContent = pending;

    // ward breakdown
    const wards = {};
    state.beds.forEach(b => {
      const w = b.ward || 'ไม่ระบุแผนก';
      wards[w] = wards[w] || { total: 0, occupied: 0 };
      wards[w].total++;
      if (b.status === 'ไม่ว่าง') wards[w].occupied++;
    });
    const wardEl = document.getElementById('wardBreakdown');
    const entries = Object.entries(wards);
    wardEl.innerHTML = entries.length ? entries.map(([name, d]) => {
      const pct = d.total ? Math.round((d.occupied / d.total) * 100) : 0;
      return `<div class="ward-row">
        <span class="ward-name">${esc(name)}</span>
        <div class="ward-bar"><div class="ward-bar-fill" style="width:${pct}%"></div></div>
        <span class="ward-count">${d.occupied}/${d.total} เตียง</span>
      </div>`;
    }).join('') : `<div class="empty-state">ยังไม่มีข้อมูลเตียง</div>`;

    // recent reservations
    const recent = [...state.reservations]
      .sort((a, b) => (b.id || 0) - (a.id || 0))
      .slice(0, 6);
    const recEl = document.getElementById('recentReservations');
    recEl.innerHTML = recent.length ? renderReservationTable(recent, { compact: true }) : `<div class="empty-state">ยังไม่มีคำขอจองเตียง</div>`;
    bindReservationActions(recEl);
  }

  function updateReservationBadge() {
    const pending = state.reservations.filter(r => r.status === 'รออนุมัติ').length;
    const badge = document.getElementById('navReservationBadge');
    if (pending > 0) {
      badge.hidden = false;
      badge.textContent = pending;
    } else {
      badge.hidden = true;
    }
  }

  // ============================================================
  // BEDS
  // ============================================================
  async function loadBeds() {
    const grid = document.getElementById('bedGrid');
    grid.innerHTML = `<div class="empty-state">กำลังโหลดเตียง…</div>`;
    try {
      state.beds = await api('/beds') || [];
      renderBeds();
    } catch (e) {
      grid.innerHTML = `<div class="empty-state">โหลดข้อมูลเตียงไม่สำเร็จ: ${esc(e.message)}</div>`;
    }
  }

  function renderBeds() {
    const grid = document.getElementById('bedGrid');
    const wardQuery = document.getElementById('bedSearchWard').value.trim().toLowerCase();
    const statusFilter = document.getElementById('bedFilterStatus').value;

    const list = state.beds.filter(b => {
      const matchWard = !wardQuery || (b.ward || '').toLowerCase().includes(wardQuery);
      const matchStatus = !statusFilter || b.status === statusFilter;
      return matchWard && matchStatus;
    });

    if (!list.length) {
      grid.innerHTML = `<div class="empty-state">ไม่พบเตียงที่ตรงกับเงื่อนไข</div>`;
      return;
    }

    grid.innerHTML = list.map(b => {
      const occupied = b.status === 'ไม่ว่าง';
      const patientName = b.patient ? `${b.patient.fname || ''} ${b.patient.lname || ''}`.trim() : null;
      return `
        <div class="bed-card ${occupied ? 'is-occupied' : ''}" data-id="${b.id}">
          <div class="bed-ward">${esc(b.ward || 'ไม่ระบุแผนก')}</div>
          <div class="bed-number">${esc(b.bedNumber || '—')}</div>
          <span class="bed-status">${esc(b.status || '—')}</span>
          <div class="bed-meta">${patientName ? 'ผู้ป่วย: ' + esc(patientName) : (b.dateAssigned ? 'บันทึกเมื่อ ' + b.dateAssigned : 'ยังไม่มีผู้ป่วย')}</div>
        </div>`;
    }).join('');

    grid.querySelectorAll('.bed-card').forEach(card => {
      card.addEventListener('click', () => openBedDetail(Number(card.dataset.id)));
    });
  }

  document.getElementById('bedSearchWard').addEventListener('input', renderBeds);
  document.getElementById('bedFilterStatus').addEventListener('change', renderBeds);
  document.getElementById('refreshBeds').addEventListener('click', loadBeds);

  function openBedDetail(id) {
    const bed = state.beds.find(b => b.id === id);
    if (!bed) return;
    const occupied = bed.status === 'ไม่ว่าง';
    const patientName = bed.patient ? `${bed.patient.fname || ''} ${bed.patient.lname || ''}`.trim() : null;

    const actions = [];
    if (occupied) {
      actions.push({
        label: 'ปล่อยเตียงว่าง',
        className: 'btn-ghost',
        onClick: async () => {
          try {
            await api(`/beds/${bed.id}`, { method: 'PUT', body: JSON.stringify({ ...bed, status: 'ว่าง' }) });
            toast('ปล่อยเตียงว่างแล้ว');
            closeModal(); loadBeds();
          } catch (e) { toast(e.message, 'error'); }
        },
      });
    } else {
      actions.push({
        label: 'ปิดปรับปรุงเตียง',
        className: 'btn-ghost',
        onClick: async () => {
          try {
            await api(`/beds/${bed.id}?status=${encodeURIComponent('ไม่ว่าง')}`, { method: 'PATCH' });
            toast('ปรับสถานะเตียงแล้ว');
            closeModal(); loadBeds();
          } catch (e) { toast(e.message, 'error'); }
        },
      });
    }
    actions.push({
      label: 'ลบเตียง',
      className: 'btn-danger',
      onClick: async () => {
        if (!confirm('ยืนยันลบเตียงนี้?')) return;
        try {
          await api(`/beds/${bed.id}`, { method: 'DELETE' });
          toast('ลบเตียงแล้ว');
          closeModal(); loadBeds();
        } catch (e) { toast(e.message, 'error'); }
      },
    });

    openModal({
      title: `เตียง ${bed.bedNumber} · ${bed.ward}`,
      submitLabel: null,
      cancelLabel: 'ปิด',
      actions,
      bodyHtml: `
        <div class="field"><label>สถานะปัจจุบัน</label>
          <p><span class="badge ${occupied ? 'badge-rejected' : 'badge-approved'}">${esc(bed.status)}</span></p>
        </div>
        ${patientName ? `<div class="field"><label>ผู้ป่วยบนเตียงนี้</label><p>${esc(patientName)}</p></div>` : ''}
        ${bed.dateAssigned ? `<div class="field"><label>วันที่บันทึกเตียง</label><p class="mono">${bed.dateAssigned}</p></div>` : ''}
      `,
    });
  }

  function bedFormModal() {
    openModal({
      title: 'เพิ่มเตียงใหม่',
      submitLabel: 'เพิ่มเตียง',
      bodyHtml: `
        <div class="field-row">
          <div class="field"><label>แผนก / ward *</label><input name="ward" type="text" required placeholder="เช่น อายุรกรรมชาย"></div>
          <div class="field"><label>หมายเลขเตียง *</label><input name="bedNumber" type="text" required placeholder="เช่น B-101"></div>
        </div>
        <div class="field"><label>สถานะเริ่มต้น</label>
          <select name="status">
            <option value="ว่าง">ว่าง</option>
            <option value="ไม่ว่าง">ไม่ว่าง</option>
          </select>
        </div>
      `,
      onSubmit: async (data) => {
        await api('/beds', { method: 'POST', body: JSON.stringify(data) });
        toast('เพิ่มเตียงเรียบร้อย');
        loadBeds();
      },
    });
  }

  // ============================================================
  // RESERVATIONS
  // ============================================================
  async function loadReservations() {
    const wrap = document.getElementById('reservationTableWrap');
    wrap.innerHTML = `<div class="empty-state">กำลังโหลดรายการจอง…</div>`;
    try {
      const [reservations, beds, users] = await Promise.all([
        api('/reservations'),
        api('/beds').catch(() => []),
        api('/users').catch(() => []),
      ]);
      state.reservations = reservations || [];
      state.beds = beds || [];
      state.users = users || [];
      renderReservations();
      updateReservationBadge();
    } catch (e) {
      wrap.innerHTML = `<div class="empty-state">โหลดรายการจองไม่สำเร็จ: ${esc(e.message)}</div>`;
    }
  }

  function renderReservations() {
    const wrap = document.getElementById('reservationTableWrap');
    const statusFilter = document.getElementById('resFilterStatus').value;
    const list = state.reservations
      .filter(r => !statusFilter || r.status === statusFilter)
      .sort((a, b) => (b.id || 0) - (a.id || 0));

    wrap.innerHTML = list.length ? renderReservationTable(list) : `<div class="empty-state">ไม่พบคำขอจองที่ตรงเงื่อนไข</div>`;
    bindReservationActions(wrap);
  }

  document.getElementById('resFilterStatus').addEventListener('change', renderReservations);
  document.getElementById('refreshReservations').addEventListener('click', loadReservations);

  function statusBadge(status) {
    const map = { 'รออนุมัติ': 'badge-pending', 'อนุมัติ': 'badge-approved', 'ปฏิเสธ': 'badge-rejected' };
    return `<span class="badge ${map[status] || 'badge-pending'}">${esc(status || '—')}</span>`;
  }

  function renderReservationTable(list, opts = {}) {
    return `<table>
      <thead><tr>
        <th>#</th><th>ผู้ป่วย</th><th>เลขบัตรประชาชน</th><th>วันรับเข้า</th><th>แผนก</th><th>สถานะ</th>${opts.compact ? '' : '<th></th>'}
      </tr></thead>
      <tbody>
        ${list.map(r => `
          <tr>
            <td class="mono">#${r.id}</td>
            <td>${esc(`${r.fname || ''} ${r.lname || ''}`.trim() || '—')}</td>
            <td class="mono">${esc(r.identificationCard || '—')}</td>
            <td class="mono">${esc(r.admissionDate || '—')}</td>
            <td>${esc(r.ward || '—')}</td>
            <td>${statusBadge(r.status)}</td>
            ${opts.compact ? '' : `<td class="cell-actions">
              ${r.status === 'รออนุมัติ' ? `
                <button class="btn btn-sm btn-primary" data-approve="${r.id}">อนุมัติ</button>
                <button class="btn btn-sm btn-danger" data-reject="${r.id}">ปฏิเสธ</button>
              ` : ''}
            </td>`}
          </tr>
        `).join('')}
      </tbody>
    </table>`;
  }

  function bindReservationActions(root) {
    root.querySelectorAll('[data-approve]').forEach(btn => {
      btn.addEventListener('click', () => openApproveModal(Number(btn.dataset.approve)));
    });
    root.querySelectorAll('[data-reject]').forEach(btn => {
      btn.addEventListener('click', async () => {
        if (!confirm('ยืนยันปฏิเสธคำขอจองนี้?')) return;
        try {
          await api(`/reservations/${btn.dataset.reject}/reject`, { method: 'POST' });
          toast('ปฏิเสธคำขอจองแล้ว');
          loadReservations();
          if (state.page === 'dashboard') loadDashboard();
        } catch (e) { toast(e.message, 'error'); }
      });
    });
  }

  function openApproveModal(reservationId) {
    const admins = state.users.filter(u => u.role === 'ADMIN');
    const availableBeds = state.beds.filter(b => b.status === 'ว่าง');

    if (!admins.length) {
      toast('ยังไม่มีผู้ใช้บทบาท ADMIN — เพิ่มผู้ใช้ก่อนจึงจะอนุมัติได้', 'error');
      return;
    }
    if (!availableBeds.length) {
      toast('ไม่มีเตียงว่างให้จัดสรรในขณะนี้', 'error');
      return;
    }

    openModal({
      title: `อนุมัติคำขอ #${reservationId}`,
      submitLabel: 'อนุมัติการจอง',
      bodyHtml: `
        <div class="field"><label>ผู้อนุมัติ (ADMIN) *</label>
          <select name="userId" required>
            ${admins.map(u => `<option value="${u.id}">${esc(u.username)}</option>`).join('')}
          </select>
        </div>
        <div class="field"><label>เตียงที่จัดสรร *</label>
          <select name="bedId" id="approveBedSelect" required>
            ${availableBeds.map(b => `<option value="${b.id}" data-ward="${esc(b.ward)}">${esc(b.ward)} · ${esc(b.bedNumber)}</option>`).join('')}
          </select>
        </div>
        <div class="field"><label>แผนก *</label><input name="ward" type="text" required value="${esc(availableBeds[0].ward || '')}"></div>
      `,
      onOpen: (root) => {
        const bedSelect = root.querySelector('#approveBedSelect');
        const wardInput = root.querySelector('[name="ward"]');
        bedSelect.addEventListener('change', () => {
          wardInput.value = bedSelect.selectedOptions[0].dataset.ward || '';
        });
      },
      onSubmit: async (data) => {
        const qs = new URLSearchParams({ userId: data.userId, bedId: data.bedId, ward: data.ward });
        await api(`/reservations/${reservationId}/approve?${qs.toString()}`, { method: 'POST' });
        toast('อนุมัติการจองเรียบร้อย');
        loadReservations();
        if (state.page === 'dashboard') loadDashboard();
      },
    });
  }

  function reservationFormModal() {
    openModal({
      title: 'สร้างคำขอจองเตียง',
      submitLabel: 'ส่งคำขอจอง',
      bodyHtml: `
        <div class="field-row">
          <div class="field"><label>ชื่อ *</label><input name="fname" type="text" required></div>
          <div class="field"><label>นามสกุล *</label><input name="lname" type="text" required></div>
        </div>
        <div class="field"><label>เลขบัตรประชาชน (13 หลัก) *</label><input name="identificationCard" type="text" pattern="\\d{13}" maxlength="13" required></div>
        <div class="field-row">
          <div class="field"><label>วันที่รับเข้า *</label><input name="admissionDate" type="date" required></div>
          <div class="field"><label>แผนกที่ต้องการ</label><input name="ward" type="text" placeholder="เช่น ศัลยกรรม"></div>
        </div>
        <div class="field"><label>การวินิจฉัยเบื้องต้น</label><textarea name="diagnostics" rows="2"></textarea></div>
      `,
      onSubmit: async (data) => {
        const payload = {
          admissionDate: data.admissionDate,
          ward: data.ward || null,
          patient: {
            fname: data.fname,
            lname: data.lname,
            identificationCard: data.identificationCard,
            diagnostics: data.diagnostics || null,
          },
        };
        await api('/reservations', { method: 'POST', body: JSON.stringify(payload) });
        toast('ส่งคำขอจองเรียบร้อย รออนุมัติ');
        loadReservations();
      },
    });
  }

  // ============================================================
  // PATIENTS
  // ============================================================
  async function loadPatients() {
    const wrap = document.getElementById('patientTableWrap');
    wrap.innerHTML = `<div class="empty-state">กำลังโหลดข้อมูลผู้ป่วย…</div>`;
    try {
      state.patients = await api('/patients') || [];
      renderPatients();
    } catch (e) {
      wrap.innerHTML = `<div class="empty-state">โหลดข้อมูลผู้ป่วยไม่สำเร็จ: ${esc(e.message)}</div>`;
    }
  }

  function renderPatients() {
    const wrap = document.getElementById('patientTableWrap');
    const q = document.getElementById('patientSearch').value.trim().toLowerCase();
    const list = state.patients.filter(p => {
      if (!q) return true;
      const name = `${p.fname || ''} ${p.lname || ''}`.toLowerCase();
      return name.includes(q) || (p.identificationCard || '').includes(q);
    });

    if (!list.length) {
      wrap.innerHTML = `<div class="empty-state">ไม่พบข้อมูลผู้ป่วย</div>`;
      return;
    }

    wrap.innerHTML = `<table>
      <thead><tr><th>#</th><th>ชื่อ-นามสกุล</th><th>เลขบัตรประชาชน</th><th>HN</th><th>สิทธิการรักษา</th><th>ประเภทอาหาร</th><th></th></tr></thead>
      <tbody>
        ${list.map(p => `
          <tr>
            <td class="mono">#${p.id}</td>
            <td>${esc(`${p.fname || ''} ${p.lname || ''}`.trim())}</td>
            <td class="mono">${esc(p.identificationCard || '—')}</td>
            <td class="mono">${esc(p.hn || '—')}</td>
            <td>${esc(p.healthcareRights || '—')}</td>
            <td>${esc(p.foodType || '—')}</td>
            <td class="cell-actions">
              <button class="btn btn-sm btn-danger" data-del-patient="${p.id}">ลบ</button>
            </td>
          </tr>
        `).join('')}
      </tbody>
    </table>`;

    wrap.querySelectorAll('[data-del-patient]').forEach(btn => {
      btn.addEventListener('click', async () => {
        if (!confirm('ยืนยันลบข้อมูลผู้ป่วยนี้?')) return;
        try {
          await api(`/patients/${btn.dataset.delPatient}`, { method: 'DELETE' });
          toast('ลบข้อมูลผู้ป่วยแล้ว');
          loadPatients();
        } catch (e) { toast(e.message, 'error'); }
      });
    });
  }

  document.getElementById('patientSearch').addEventListener('input', renderPatients);

  function patientFormModal() {
    openModal({
      title: 'เพิ่มผู้ป่วยใหม่',
      submitLabel: 'บันทึกผู้ป่วย',
      bodyHtml: `
        <div class="field-row">
          <div class="field"><label>ชื่อ *</label><input name="fname" type="text" required></div>
          <div class="field"><label>นามสกุล *</label><input name="lname" type="text" required></div>
        </div>
        <div class="field"><label>เลขบัตรประชาชน (13 หลัก) *</label><input name="identificationCard" type="text" pattern="\\d{13}" maxlength="13" required></div>
        <div class="field-row">
          <div class="field"><label>HN (10 หลัก)</label><input name="hn" type="text" maxlength="10"></div>
          <div class="field"><label>AN (10 หลัก)</label><input name="an" type="text" maxlength="10"></div>
        </div>
        <div class="field"><label>สิทธิการรักษา</label><input name="healthcareRights" type="text"></div>
        <div class="field"><label>ประเภทอาหาร</label>
          <select name="foodType">
            <option value="">ไม่ระบุ</option>
            <option value="อาหารปกติ">อาหารปกติ</option>
            <option value="อาหารอ่อน">อาหารอ่อน</option>
            <option value="อาหารเหลว">อาหารเหลว</option>
            <option value="งดอาหาร">งดอาหาร</option>
          </select>
        </div>
        <div class="field"><label>การวินิจฉัย</label><textarea name="diagnostics" rows="2"></textarea></div>
      `,
      onSubmit: async (data) => {
        Object.keys(data).forEach(k => { if (data[k] === '') data[k] = null; });
        await api('/patients', { method: 'POST', body: JSON.stringify(data) });
        toast('เพิ่มผู้ป่วยเรียบร้อย');
        loadPatients();
      },
    });
  }

  // ============================================================
  // USERS
  // ============================================================
  async function loadUsers() {
    const wrap = document.getElementById('userTableWrap');
    wrap.innerHTML = `<div class="empty-state">กำลังโหลดผู้ใช้…</div>`;
    try {
      state.users = await api('/users') || [];
      renderUsers();
    } catch (e) {
      wrap.innerHTML = `<div class="empty-state">โหลดผู้ใช้ไม่สำเร็จ: ${esc(e.message)}</div>`;
    }
  }

  function renderUsers() {
    const wrap = document.getElementById('userTableWrap');
    if (!state.users.length) {
      wrap.innerHTML = `<div class="empty-state">ยังไม่มีผู้ใช้ — เพิ่มผู้ใช้ใหม่ได้ที่ปุ่มด้านบนขวา</div>`;
      return;
    }
    wrap.innerHTML = `<table>
      <thead><tr><th>#</th><th>Username</th><th>ชื่อ-สกุล</th><th>บทบาท</th><th></th></tr></thead>
      <tbody>
        ${state.users.map(u => `
          <tr>
            <td class="mono">#${u.id}</td>
            <td>${esc(u.username)}</td>
            <td>${esc(u.lname || '—')}</td>
            <td><span class="badge ${u.role === 'ADMIN' ? 'badge-approved' : 'badge-pending'}">${esc(u.role)}</span></td>
            <td class="cell-actions"><button class="btn btn-sm btn-danger" data-del-user="${u.id}">ลบ</button></td>
          </tr>
        `).join('')}
      </tbody>
    </table>`;

    wrap.querySelectorAll('[data-del-user]').forEach(btn => {
      btn.addEventListener('click', async () => {
        if (!confirm('ยืนยันลบผู้ใช้นี้?')) return;
        try {
          await api(`/users/${btn.dataset.delUser}`, { method: 'DELETE' });
          toast('ลบผู้ใช้แล้ว');
          loadUsers();
        } catch (e) { toast(e.message, 'error'); }
      });
    });
  }

  function userFormModal() {
    openModal({
      title: 'เพิ่มผู้ใช้ใหม่',
      submitLabel: 'บันทึกผู้ใช้',
      bodyHtml: `
        <div class="field"><label>Username *</label><input name="username" type="text" required></div>
        <div class="field"><label>Password *</label><input name="password" type="text" required></div>
        <div class="field"><label>ชื่อ-สกุล</label><input name="lname" type="text"></div>
        <div class="field"><label>บทบาท *</label>
          <select name="role" required>
            <option value="ADMIN">ADMIN — อนุมัติการจองได้</option>
            <option value="STAFF">STAFF — เจ้าหน้าที่ทั่วไป</option>
          </select>
        </div>
      `,
      onSubmit: async (data) => {
        await api('/users', { method: 'POST', body: JSON.stringify(data) });
        toast('เพิ่มผู้ใช้เรียบร้อย');
        loadUsers();
      },
    });
  }

  // ============================================================
  // QUICK ADD button routes to the right form per page
  // ============================================================
  document.getElementById('quickAddBtn').addEventListener('click', () => {
    if (state.page === 'beds') bedFormModal();
    if (state.page === 'reservations') reservationFormModal();
    if (state.page === 'patients') patientFormModal();
    if (state.page === 'users') userFormModal();
  });

  // ============================================================
  // MODAL system
  // ============================================================
  const backdrop = document.getElementById('modalBackdrop');
  const modalForm = document.getElementById('modalForm');
  const modalSubmit = document.getElementById('modalSubmit');
  const modalCancel = document.getElementById('modalCancel');
  const modalFootLeft = document.getElementById('modalFootLeft');

  function openModal({ title, bodyHtml, submitLabel, cancelLabel = 'ยกเลิก', actions = [], onSubmit, onOpen }) {
    document.getElementById('modalTitle').textContent = title;
    modalForm.innerHTML = bodyHtml || '';
    modalCancel.textContent = cancelLabel;

    if (submitLabel) {
      modalSubmit.hidden = false;
      modalSubmit.textContent = submitLabel;
    } else {
      modalSubmit.hidden = true;
    }

    // secondary/destructive actions on the left of the footer (e.g. delete, free bed)
    modalFootLeft.innerHTML = '';
    actions.forEach(action => {
      const btn = document.createElement('button');
      btn.type = 'button';
      btn.className = `btn btn-sm ${action.className || 'btn-ghost'}`;
      btn.textContent = action.label;
      btn.addEventListener('click', action.onClick);
      modalFootLeft.appendChild(btn);
    });

    modalForm.onsubmit = async (ev) => {
      ev.preventDefault();
      if (!onSubmit) return;
      const fd = new FormData(modalForm);
      const data = Object.fromEntries(fd.entries());
      modalSubmit.disabled = true;
      try {
        await onSubmit(data);
        closeModal();
      } catch (e) {
        toast(e.message, 'error');
      } finally {
        modalSubmit.disabled = false;
      }
    };

    backdrop.classList.add('is-open');
    if (onOpen) onOpen(backdrop);
    const firstInput = modalForm.querySelector('input, select, textarea');
    if (firstInput) setTimeout(() => firstInput.focus(), 50);
  }

  function closeModal() {
    backdrop.classList.remove('is-open');
    modalForm.onsubmit = null;
  }

  document.getElementById('modalClose').addEventListener('click', closeModal);
  document.getElementById('modalCancel').addEventListener('click', closeModal);
  backdrop.addEventListener('click', (e) => { if (e.target === backdrop) closeModal(); });
  document.addEventListener('keydown', (e) => { if (e.key === 'Escape' && backdrop.classList.contains('is-open')) closeModal(); });

  // ---------- utils ----------
  function esc(str) {
    return String(str ?? '').replace(/[&<>"']/g, (c) => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
  }

  // ---------- boot ----------
  goTo('dashboard');
})();
