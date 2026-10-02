import React, { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { patientService, queueService, departmentService } from '../services/api';
import StatCard from '../components/StatCard';
import { Link } from 'react-router-dom';

export default function PatientDashboard() {
  const { user } = useAuth();
  const [profile, setProfile] = useState(null);
  const [dashboardData, setDashboardData] = useState(null);
  const [departments, setDepartments] = useState([]);
  const [selectedDept, setSelectedDept] = useState('');
  const [severity, setSeverity] = useState('WALK_IN');
  const [joiningQueue, setJoiningQueue] = useState(false);
  const [loading, setLoading] = useState(true);
  const [msg, setMsg] = useState('');

  const loadData = async () => {
    try {
      if (user?.id) {
        const profRes = await patientService.getByUserId(user.id);
        setProfile(profRes.data);
        const dashRes = await patientService.getDashboard(profRes.data.id);
        setDashboardData(dashRes.data);
      }
      const deptsRes = await departmentService.getAll();
      setDepartments(deptsRes.data);
      if (deptsRes.data.length > 0) setSelectedDept(deptsRes.data[0].id);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
    const interval = setInterval(loadData, 5000); // Polling queue position every 5s
    return () => clearInterval(interval);
  }, [user]);

  const handleJoinQueue = async (e) => {
    e.preventDefault();
    if (!profile) return;
    setJoiningQueue(true);
    setMsg('');
    try {
      await queueService.join({
        patientId: profile.id,
        departmentId: parseInt(selectedDept),
        severityLevel: severity
      });
      setMsg('Successfully joined queue ticket!');
      loadData();
    } catch (err) {
      setMsg(err.response?.data?.message || 'Failed to join queue');
    } finally {
      setJoiningQueue(false);
    }
  };

  if (loading) {
    return <div style={{ color: 'var(--text-muted)' }}>Loading Patient Console...</div>;
  }

  const activeTicket = dashboardData?.currentQueuePosition;

  return (
    <div>
      <div style={{ marginBottom: '24px' }}>
        <h1 style={{ fontSize: '1.8rem', fontWeight: 800 }}>Patient Queue Portal</h1>
        <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
          Welcome back, {user?.fullName}. Medical Record #: <span style={{ color: 'var(--accent-cyan)', fontWeight: 700 }}>{profile?.medicalRecordNumber}</span>
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

      {/* Active Ticket Banner */}
      {activeTicket ? (
        <div className="glass-card" style={{
          background: 'linear-gradient(135deg, rgba(16, 185, 129, 0.15) 0%, rgba(6, 182, 212, 0.15) 100%)',
          border: '1px solid rgba(16, 185, 129, 0.4)',
          marginBottom: '24px'
        }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '16px' }}>
            <div>
              <div style={{ fontSize: '0.8rem', textTransform: 'uppercase', letterSpacing: '0.08em', color: 'var(--primary-emerald)', fontWeight: 700 }}>
                Active Queue Ticket
              </div>
              <h2 style={{ fontSize: '2.5rem', fontWeight: 800, color: 'var(--accent-cyan)', fontFamily: 'monospace' }}>
                {activeTicket.ticketNumber}
              </h2>
              <div style={{ fontSize: '0.9rem', color: 'var(--text-main)', marginTop: '4px' }}>
                Department: <strong>{activeTicket.departmentName}</strong> | Doctor: <strong>{activeTicket.doctorName}</strong>
              </div>
            </div>

            <div style={{ display: 'flex', gap: '20px', textAlign: 'center' }}>
              <div style={{ background: 'rgba(15, 23, 42, 0.6)', padding: '12px 24px', borderRadius: '12px', border: '1px solid var(--border-color)' }}>
                <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', textTransform: 'uppercase' }}>Position in Queue</div>
                <div style={{ fontSize: '1.8rem', fontWeight: 800, color: '#34d399' }}>#{activeTicket.position}</div>
                <div style={{ fontSize: '0.7rem', color: 'var(--text-dim)' }}>of {activeTicket.totalInQueue} patients</div>
              </div>

              <div style={{ background: 'rgba(15, 23, 42, 0.6)', padding: '12px 24px', borderRadius: '12px', border: '1px solid var(--border-color)' }}>
                <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', textTransform: 'uppercase' }}>Est. Waiting Time</div>
                <div style={{ fontSize: '1.8rem', fontWeight: 800, color: '#f59e0b' }}>~{activeTicket.estimatedWaitMinutes} mins</div>
                <div style={{ fontSize: '0.7rem', color: 'var(--text-dim)' }}>Dynamic calculation</div>
              </div>
            </div>
          </div>
          <div style={{ marginTop: '14px', fontSize: '0.85rem', color: 'var(--text-muted)', borderTop: '1px solid var(--border-color)', paddingTop: '10px' }}>
            ℹ️ {activeTicket.statusMessage}
          </div>
        </div>
      ) : (
        /* Join Queue Card */
        <div className="glass-card" style={{ marginBottom: '24px' }}>
          <h3 style={{ fontSize: '1.1rem', marginBottom: '12px' }}>Check-in & Join Department Queue</h3>
          <form onSubmit={handleJoinQueue} style={{ display: 'grid', gridTemplateColumns: '2fr 1fr 1fr', gap: '16px', alignItems: 'end' }}>
            <div className="form-group" style={{ marginBottom: 0 }}>
              <label>Select Department</label>
              <select className="form-control" value={selectedDept} onChange={(e) => setSelectedDept(e.target.value)}>
                {departments.map(d => (
                  <option key={d.id} value={d.id}>{d.name} ({d.code}) - Avg Wait: {d.avgConsultationMinutes}m</option>
                ))}
              </select>
            </div>

            <div className="form-group" style={{ marginBottom: 0 }}>
              <label>Severity Urgency</label>
              <select className="form-control" value={severity} onChange={(e) => setSeverity(e.target.value)}>
                <option value="WALK_IN">Walk-in (Regular)</option>
                <option value="URGENT">Urgent Care</option>
                <option value="EMERGENCY">Acute Emergency</option>
              </select>
            </div>

            <button type="submit" className="btn btn-primary" style={{ height: '44px' }} disabled={joiningQueue}>
              {joiningQueue ? 'Generating Ticket...' : 'Get Queue Ticket'}
            </button>
          </form>
        </div>
      )}

      {/* Grid Stats & History */}
      <div className="grid-2">
        <div className="glass-card">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
            <h3 style={{ fontSize: '1.1rem' }}>Upcoming Appointments</h3>
            <Link to="/book-appointment" className="btn btn-secondary" style={{ padding: '4px 10px', fontSize: '0.8rem' }}>
              + Schedule
            </Link>
          </div>
          {dashboardData?.appointmentHistory?.length > 0 ? (
            <table className="custom-table">
              <thead>
                <tr>
                  <th>Date</th>
                  <th>Time</th>
                  <th>Doctor</th>
                  <th>Dept</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {dashboardData.appointmentHistory.map(appt => (
                  <tr key={appt.id}>
                    <td>{appt.appointmentDate}</td>
                    <td style={{ fontWeight: 600 }}>{appt.timeSlot}</td>
                    <td>{appt.doctorName}</td>
                    <td>{appt.departmentName}</td>
                    <td><span className="badge badge-regular">{appt.status}</span></td>
                  </tr>
                ))}
              </tbody>
            </table>
          ) : (
            <p style={{ color: 'var(--text-dim)', fontSize: '0.85rem' }}>No upcoming appointments scheduled.</p>
          )}
        </div>

        <div className="glass-card">
          <h3 style={{ fontSize: '1.1rem', marginBottom: '12px' }}>Hospital Queue Engine Features</h3>
          <ul style={{ color: 'var(--text-muted)', fontSize: '0.85rem', paddingLeft: '18px', display: 'flex', flexDirection: 'column', gap: '8px' }}>
            <li><strong>Dynamic Priority Scoring:</strong> Emergency severity, patient age factor, and waiting duration contribute to rank.</li>
            <li><strong>Starvation Prevention:</strong> Waiting time automatically adds priority points every minute so walk-ins are never blocked.</li>
            <li><strong>Real-time Wait Estimation:</strong> Calculated based on doctor consultation averages and current positions ahead.</li>
          </ul>
        </div>
      </div>
    </div>
  );
}
