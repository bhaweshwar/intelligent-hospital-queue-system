import React, { useEffect, useState } from 'react';
import { departmentService, doctorService, queueService } from '../services/api';
import QueueVisualizer from '../components/QueueVisualizer';

export default function ReceptionistDashboard() {
  const [departments, setDepartments] = useState([]);
  const [doctors, setDoctors] = useState([]);
  const [selectedDept, setSelectedDept] = useState('');
  const [queue, setQueue] = useState([]);
  const [patientId, setPatientId] = useState('1'); // Default patient ID 1 for walk-ins
  const [severity, setSeverity] = useState('WALK_IN');
  const [msg, setMsg] = useState('');

  const loadData = async () => {
    try {
      const deptsRes = await departmentService.getAll();
      setDepartments(deptsRes.data);
      if (deptsRes.data.length > 0 && !selectedDept) {
        setSelectedDept(deptsRes.data[0].id);
      }
      const docsRes = await doctorService.getAll();
      setDoctors(docsRes.data);
    } catch (err) {
      console.error(err);
    }
  };

  const loadQueue = async () => {
    if (!selectedDept) return;
    try {
      const qRes = await queueService.getDeptQueue(selectedDept);
      setQueue(qRes.data);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  useEffect(() => {
    loadQueue();
    const interval = setInterval(loadQueue, 4000);
    return () => clearInterval(interval);
  }, [selectedDept]);

  const handleWalkInCheckIn = async (e) => {
    e.preventDefault();
    setMsg('');
    try {
      const res = await queueService.join({
        patientId: parseInt(patientId),
        departmentId: parseInt(selectedDept),
        severityLevel: severity
      });
      setMsg(`Walk-in Ticket Issued: ${res.data.ticketNumber} (Priority Score: ${Math.round(res.data.calculatedPriority)} pts)`);
      loadQueue();
    } catch (err) {
      setMsg(err.response?.data?.message || 'Check-in failed');
    }
  };

  return (
    <div>
      <div style={{ marginBottom: '24px' }}>
        <h1 style={{ fontSize: '1.8rem', fontWeight: 800 }}>Receptionist Control Desk</h1>
        <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
          Walk-in registration, priority assignment, and department queue monitors
        </p>
      </div>

      {msg && (
        <div style={{
          background: 'rgba(16, 185, 129, 0.15)',
          border: '1px solid rgba(16, 185, 129, 0.3)',
          color: '#6ee7b7',
          padding: '12px',
          borderRadius: '10px',
          marginBottom: '20px'
        }}>
          {msg}
        </div>
      )}

      <div className="grid-2" style={{ marginBottom: '24px' }}>
        {/* Walk-in Check-in Form */}
        <div className="glass-card">
          <h3 style={{ fontSize: '1.1rem', marginBottom: '16px' }}>Issue Ticket / Walk-In Admission</h3>
          <form onSubmit={handleWalkInCheckIn}>
            <div className="form-group">
              <label>Select Department</label>
              <select className="form-control" value={selectedDept} onChange={(e) => setSelectedDept(e.target.value)}>
                {departments.map(d => (
                  <option key={d.id} value={d.id}>{d.name} ({d.code})</option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label>Patient ID (MRN/ID)</label>
              <input type="number" className="form-control" value={patientId} onChange={(e) => setPatientId(e.target.value)} required />
            </div>

            <div className="form-group">
              <label>Triage Severity Level</label>
              <select className="form-control" value={severity} onChange={(e) => setSeverity(e.target.value)}>
                <option value="WALK_IN">🟢 Walk-in (Standard Priority: 50 pts)</option>
                <option value="REGULAR_APPOINTMENT">🔵 Regular Appt (Priority: 150 pts)</option>
                <option value="URGENT">🟡 Urgent (Priority: 500 pts)</option>
                <option value="EMERGENCY">🔴 Acute Emergency (Priority: 1000 pts)</option>
              </select>
            </div>

            <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '8px' }}>
              🎫 Issue Ticket & Enqueue
            </button>
          </form>
        </div>

        {/* Doctor Availability Matrix */}
        <div className="glass-card">
          <h3 style={{ fontSize: '1.1rem', marginBottom: '16px' }}>Duty Doctor Availability</h3>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
            {doctors.map(doc => (
              <div key={doc.id} style={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
                padding: '12px 16px',
                background: 'rgba(15, 23, 42, 0.6)',
                borderRadius: '10px',
                border: '1px solid var(--border-color)'
              }}>
                <div>
                  <div style={{ fontWeight: 600 }}>{doc.user?.fullName}</div>
                  <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                    {doc.department?.name} | {doc.roomNumber}
                  </div>
                </div>
                <span className={`badge ${doc.isAvailable ? 'badge-regular' : 'badge-emergency'}`}>
                  {doc.isAvailable ? 'AVAILABLE' : 'OFFLINE'}
                </span>
              </div>
            ))}
          </div>
        </div>
      </div>

      <QueueVisualizer queue={queue} />
    </div>
  );
}
