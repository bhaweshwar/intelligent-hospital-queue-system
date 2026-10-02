import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { departmentService, doctorService, appointmentService, patientService } from '../services/api';
import { useNavigate } from 'react-router-dom';

export default function BookAppointmentPage() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [departments, setDepartments] = useState([]);
  const [doctors, setDoctors] = useState([]);
  const [profile, setProfile] = useState(null);
  
  const [deptId, setDeptId] = useState('');
  const [doctorId, setDoctorId] = useState('');
  const [date, setDate] = useState(new Date().toISOString().split('T')[0]);
  const [timeSlot, setTimeSlot] = useState('09:00 AM');
  const [apptType, setApptType] = useState('ROUTINE');
  const [reason, setReason] = useState('');
  
  const [msg, setMsg] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    async function init() {
      try {
        const deptsRes = await departmentService.getAll();
        setDepartments(deptsRes.data);
        if (deptsRes.data.length > 0) {
          setDeptId(deptsRes.data[0].id);
          const docsRes = await doctorService.getByDept(deptsRes.data[0].id);
          setDoctors(docsRes.data);
          if (docsRes.data.length > 0) setDoctorId(docsRes.data[0].id);
        }
        if (user?.id && user?.role === 'PATIENT') {
          const pRes = await patientService.getByUserId(user.id);
          setProfile(pRes.data);
        }
      } catch (err) {
        console.error(err);
      }
    }
    init();
  }, [user]);

  const handleDeptChange = async (e) => {
    const dId = e.target.value;
    setDeptId(dId);
    try {
      const docsRes = await doctorService.getByDept(dId);
      setDoctors(docsRes.data);
      if (docsRes.data.length > 0) setDoctorId(docsRes.data[0].id);
      else setDoctorId('');
    } catch (err) {
      console.error(err);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setMsg('');
    setLoading(true);

    const patId = profile ? profile.id : 1; // Fallback for receptionist booking

    try {
      const res = await appointmentService.book({
        patientId: patId,
        doctorId: parseInt(doctorId),
        departmentId: parseInt(deptId),
        appointmentDate: date,
        timeSlot: timeSlot,
        appointmentType: apptType,
        reason: reason
      });
      setMsg(`Appointment booked successfully! Date: ${res.data.appointmentDate} Slot: ${res.data.timeSlot}`);
      setTimeout(() => navigate('/patient'), 1500);
    } catch (err) {
      setError(err.response?.data?.message || 'Double booking conflict or slot unavailable');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: '600px', margin: '0 auto' }}>
      <div style={{ marginBottom: '24px' }}>
        <h1 style={{ fontSize: '1.8rem', fontWeight: 800 }}>Schedule Appointment</h1>
        <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
          Transactional booking with double-booking concurrency protection
        </p>
      </div>

      {msg && (
        <div style={{ background: 'rgba(16, 185, 129, 0.15)', border: '1px solid rgba(16, 185, 129, 0.3)', color: '#6ee7b7', padding: '12px', borderRadius: '10px', marginBottom: '20px' }}>
          {msg}
        </div>
      )}

      {error && (
        <div style={{ background: 'rgba(239, 68, 68, 0.15)', border: '1px solid rgba(239, 68, 68, 0.3)', color: '#fca5a5', padding: '12px', borderRadius: '10px', marginBottom: '20px' }}>
          {error}
        </div>
      )}

      <div className="glass-card">
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Department</label>
            <select className="form-control" value={deptId} onChange={handleDeptChange} required>
              {departments.map(d => (
                <option key={d.id} value={d.id}>{d.name} ({d.code})</option>
              ))}
            </select>
          </div>

          <div className="form-group">
            <label>Assigned Specialist Doctor</label>
            <select className="form-control" value={doctorId} onChange={(e) => setDoctorId(e.target.value)} required>
              {doctors.length > 0 ? (
                doctors.map(doc => (
                  <option key={doc.id} value={doc.id}>
                    {doc.user?.fullName} ({doc.specialization}) - {doc.roomNumber}
                  </option>
                ))
              ) : (
                <option value="">No available doctors in this department</option>
              )}
            </select>
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
            <div className="form-group">
              <label>Appointment Date</label>
              <input type="date" className="form-control" value={date} onChange={(e) => setDate(e.target.value)} required />
            </div>

            <div className="form-group">
              <label>Time Slot</label>
              <select className="form-control" value={timeSlot} onChange={(e) => setTimeSlot(e.target.value)}>
                <option value="09:00 AM">09:00 AM</option>
                <option value="09:30 AM">09:30 AM</option>
                <option value="10:00 AM">10:00 AM</option>
                <option value="10:30 AM">10:30 AM</option>
                <option value="11:00 AM">11:00 AM</option>
                <option value="02:00 PM">02:00 PM</option>
                <option value="02:30 PM">02:30 PM</option>
                <option value="03:00 PM">03:00 PM</option>
              </select>
            </div>
          </div>

          <div className="form-group">
            <label>Appointment Urgency Type</label>
            <select className="form-control" value={apptType} onChange={(e) => setApptType(e.target.value)}>
              <option value="ROUTINE">Routine Consultation</option>
              <option value="FOLLOW_UP">Follow-up Checkup</option>
              <option value="URGENT">Urgent Priority</option>
            </select>
          </div>

          <div className="form-group">
            <label>Reason / Symptoms</label>
            <textarea className="form-control" rows={3} placeholder="Describe primary symptoms..." value={reason} onChange={(e) => setReason(e.target.value)} />
          </div>

          <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '8px', padding: '12px' }} disabled={loading || !doctorId}>
            {loading ? 'Reserving Slot...' : 'Confirm Appointment Slot'}
          </button>
        </form>
      </div>
    </div>
  );
}
