import React, { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { doctorService, queueService, consultationService } from '../services/api';
import QueueVisualizer from '../components/QueueVisualizer';

export default function DoctorDashboard() {
  const { user } = useAuth();
  const [doctor, setDoctor] = useState(null);
  const [dashboard, setDashboard] = useState(null);
  const [activeConsultation, setActiveConsultation] = useState(null);
  const [diagnosis, setDiagnosis] = useState('');
  const [prescription, setPrescription] = useState('');
  const [loading, setLoading] = useState(true);
  const [msg, setMsg] = useState('');

  const loadDoctorData = async () => {
    try {
      if (user?.id) {
        const docRes = await doctorService.getByUserId(user.id);
        setDoctor(docRes.data);
        const dashRes = await doctorService.getDashboard(docRes.data.id);
        setDashboard(dashRes.data);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadDoctorData();
    const interval = setInterval(loadDoctorData, 5000);
    return () => clearInterval(interval);
  }, [user]);

  const handleToggleAvailability = async () => {
    if (!doctor) return;
    try {
      const res = await doctorService.toggleAvailability(doctor.id, !doctor.isAvailable);
      setDoctor(res.data);
      setMsg(`Availability updated: ${res.data.isAvailable ? 'AVAILABLE' : 'UNAVAILABLE'}`);
    } catch (err) {
      setMsg('Failed to update availability');
    }
  };

  const handleCallNext = async () => {
    if (!doctor) return;
    setMsg('');
    try {
      const res = await queueService.callNext(doctor.department.id, doctor.id);
      if (res.status === 204 || !res.data) {
        setMsg('No patients currently waiting in queue.');
      } else {
        setActiveConsultation(res.data);
        setMsg(`Called ticket ${res.data.ticketNumber} (${res.data.patientName})`);
        loadDoctorData();
      }
    } catch (err) {
      setMsg(err.response?.data?.message || 'Error calling next patient');
    }
  };

  const handleCompleteConsultation = async (e) => {
    e.preventDefault();
    if (!activeConsultation) return;
    try {
      await consultationService.complete({
        consultationId: activeConsultation.id,
        diagnosis,
        prescription
      });
      setMsg(`Consultation for ${activeConsultation.ticketNumber} completed successfully.`);
      setActiveConsultation(null);
      setDiagnosis('');
      setPrescription('');
      loadDoctorData();
    } catch (err) {
      setMsg(err.response?.data?.message || 'Failed to complete consultation');
    }
  };

  const handleMarkNoShow = async (queueId) => {
    try {
      await consultationService.markNoShow(queueId);
      setMsg('Patient marked as No-Show');
      if (activeConsultation?.id === queueId) setActiveConsultation(null);
      loadDoctorData();
    } catch (err) {
      setMsg('Failed to mark no-show');
    }
  };

  if (loading) return <div style={{ color: 'var(--text-muted)' }}>Loading Doctor Dashboard...</div>;

  return (
    <div>
      {/* Header & Controls */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px', flexWrap: 'wrap', gap: '16px' }}>
        <div>
          <h1 style={{ fontSize: '1.8rem', fontWeight: 800 }}>Dr. {user?.fullName}</h1>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
            Specialization: <strong style={{ color: 'var(--accent-cyan)' }}>{doctor?.specialization}</strong> | Room: <strong>{doctor?.roomNumber}</strong> | Department: <strong>{doctor?.department?.name}</strong>
          </p>
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
          <button className={`btn ${doctor?.isAvailable ? 'btn-primary' : 'btn-secondary'}`} onClick={handleToggleAvailability}>
            {doctor?.isAvailable ? '🟢 Status: Available' : '🔴 Status: Unavailable'}
          </button>
          <button className="btn btn-primary" style={{ background: 'linear-gradient(135deg, #06b6d4 0%, #3b82f6 100%)' }} onClick={handleCallNext} disabled={!doctor?.isAvailable}>
            ⚡ Call Next Patient
          </button>
        </div>
      </div>

      {msg && (
        <div style={{
          background: 'rgba(6, 182, 212, 0.15)',
          border: '1px solid rgba(6, 182, 212, 0.3)',
          color: '#67e8f9',
          padding: '12px',
          borderRadius: '10px',
          marginBottom: '20px'
        }}>
          {msg}
        </div>
      )}

      {/* Active Call Screen */}
      {activeConsultation && (
        <div className="glass-card" style={{ border: '2px solid var(--primary-emerald)', marginBottom: '24px' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
            <div>
              <span className="badge badge-regular">In Consultation</span>
              <h2 style={{ fontSize: '2rem', color: 'var(--accent-cyan)', fontFamily: 'monospace', margin: '4px 0' }}>
                Ticket: {activeConsultation.ticketNumber}
              </h2>
              <div style={{ fontSize: '1.1rem', fontWeight: 700 }}>
                Patient: {activeConsultation.patientName} ({activeConsultation.patientAge} yrs)
              </div>
            </div>
            <button className="btn btn-danger" onClick={() => handleMarkNoShow(activeConsultation.id)}>
              Mark No-Show
            </button>
          </div>

          <form onSubmit={handleCompleteConsultation}>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
              <div className="form-group">
                <label>Diagnosis Notes</label>
                <textarea className="form-control" rows={3} placeholder="Primary symptoms and diagnosis..." value={diagnosis} onChange={(e) => setDiagnosis(e.target.value)} required />
              </div>
              <div className="form-group">
                <label>Prescription & Instructions</label>
                <textarea className="form-control" rows={3} placeholder="Prescribed medications & dosage..." value={prescription} onChange={(e) => setPrescription(e.target.value)} required />
              </div>
            </div>

            <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '8px' }}>
              ✅ Complete Consultation & Discharge
            </button>
          </form>
        </div>
      )}

      {/* Live Priority Queue */}
      <div style={{ marginBottom: '24px' }}>
        <QueueVisualizer queue={dashboard?.waitingQueue} />
      </div>
    </div>
  );
}
