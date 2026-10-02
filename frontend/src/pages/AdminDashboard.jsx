import React, { useEffect, useState } from 'react';
import { adminService } from '../services/api';
import StatCard from '../components/StatCard';

export default function AdminDashboard() {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);

  const loadAnalytics = async () => {
    try {
      const res = await adminService.getAnalytics();
      setStats(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadAnalytics();
    const interval = setInterval(loadAnalytics, 5000);
    return () => clearInterval(interval);
  }, []);

  if (loading) return <div style={{ color: 'var(--text-muted)' }}>Loading Hospital Analytics...</div>;

  return (
    <div>
      <div style={{ marginBottom: '24px' }}>
        <h1 style={{ fontSize: '1.8rem', fontWeight: 800 }}>Hospital System Analytics</h1>
        <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
          Real-time operational monitoring, doctor utilization, and queue metrics
        </p>
      </div>

      {/* Top 4 Stat Cards */}
      <div className="grid-4" style={{ marginBottom: '24px' }}>
        <StatCard title="Total Queue Entries" value={stats?.totalPatientsToday || 0} icon="🏥" highlightColor="#34d399" subtitle="Today's total traffic" />
        <StatCard title="Active Waiting Queue" value={stats?.waitingPatients || 0} icon="⏳" highlightColor="#f59e0b" subtitle="Currently waiting" />
        <StatCard title="Duty Doctors Online" value={stats?.activeDoctors || 0} icon="🩺" highlightColor="#06b6d4" subtitle="Available in rooms" />
        <StatCard title="Emergency Cases" value={stats?.emergencyCasesToday || 0} icon="🚨" highlightColor="#ef4444" subtitle="Acute admissions" />
      </div>

      <div className="grid-4" style={{ marginBottom: '24px' }}>
        <StatCard title="Avg Wait Time" value={`${Math.round(stats?.avgWaitingTimeMinutes || 0)}m`} icon="⏱️" highlightColor="#60a5fa" subtitle="Cross-department average" />
        <StatCard title="Completed Consultations" value={stats?.completedConsultationsToday || 0} icon="✅" highlightColor="#10b981" subtitle="Discharged patients" />
        <StatCard title="Cancelled Appointments" value={stats?.cancelledAppointmentsToday || 0} icon="❌" highlightColor="#f87171" subtitle="Patient cancellations" />
        <StatCard title="No-Show Rate" value={`${stats?.noShowRatePercentage || 0}%`} icon="📉" highlightColor="#c084fc" subtitle="Missed calls percentage" />
      </div>

      {/* Department Congestion Table */}
      <div className="glass-card">
        <h3 style={{ fontSize: '1.1rem', marginBottom: '16px' }}>Department Congestion & Wait Breakdown</h3>
        <table className="custom-table">
          <thead>
            <tr>
              <th>Department</th>
              <th>Waiting Patients</th>
              <th>Est. Dept Wait Time</th>
              <th>Congestion Status</th>
            </tr>
          </thead>
          <tbody>
            {stats?.patientCountByDepartment && Object.entries(stats.patientCountByDepartment).map(([dept, count]) => {
              const wait = stats.avgWaitTimeByDepartment?.[dept] || 0;
              const isHigh = count > 5;
              return (
                <tr key={dept}>
                  <td style={{ fontWeight: 700 }}>{dept}</td>
                  <td style={{ fontWeight: 800, color: 'var(--accent-cyan)' }}>{count} patients</td>
                  <td>~{Math.round(wait)} minutes</td>
                  <td>
                    <span className={`badge ${isHigh ? 'badge-emergency' : 'badge-regular'}`}>
                      {isHigh ? 'HIGH CONGESTION' : 'NORMAL FLOW'}
                    </span>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    </div>
  );
}
