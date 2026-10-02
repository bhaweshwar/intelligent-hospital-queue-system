import React from 'react';
import { NavLink } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Sidebar() {
  const { user } = useAuth();
  if (!user) return null;

  const role = user.role;

  return (
    <aside style={{
      width: '240px',
      background: 'rgba(30, 41, 59, 0.4)',
      borderRight: '1px solid var(--border-color)',
      padding: '24px 16px',
      display: 'flex',
      flexDirection: 'column',
      gap: '8px'
    }}>
      <div style={{ fontSize: '0.75rem', fontWeight: 700, color: 'var(--text-dim)', paddingLeft: '12px', marginBottom: '8px', textTransform: 'uppercase', letterSpacing: '0.08em' }}>
        Navigation
      </div>

      {role === 'PATIENT' && (
        <>
          <NavLink to="/patient" className={({ isActive }) => `sidebar-link ${isActive ? 'active' : ''}`}>
            🏥 My Queue & Dashboard
          </NavLink>
          <NavLink to="/book-appointment" className={({ isActive }) => `sidebar-link ${isActive ? 'active' : ''}`}>
            📅 Book Appointment
          </NavLink>
        </>
      )}

      {role === 'DOCTOR' && (
        <>
          <NavLink to="/doctor" className={({ isActive }) => `sidebar-link ${isActive ? 'active' : ''}`}>
            🩺 Doctor Console
          </NavLink>
        </>
      )}

      {role === 'RECEPTIONIST' && (
        <>
          <NavLink to="/receptionist" className={({ isActive }) => `sidebar-link ${isActive ? 'active' : ''}`}>
            📋 Receptionist Desk
          </NavLink>
          <NavLink to="/book-appointment" className={({ isActive }) => `sidebar-link ${isActive ? 'active' : ''}`}>
            ➕ New Appointment
          </NavLink>
        </>
      )}

      {role === 'ADMIN' && (
        <>
          <NavLink to="/admin" className={({ isActive }) => `sidebar-link ${isActive ? 'active' : ''}`}>
            📊 System Analytics
          </NavLink>
          <NavLink to="/receptionist" className={({ isActive }) => `sidebar-link ${isActive ? 'active' : ''}`}>
            🏥 Queue Monitor
          </NavLink>
        </>
      )}

      <style>{`
        .sidebar-link {
          display: flex;
          align-items: center;
          gap: 12px;
          padding: 12px 14px;
          color: var(--text-muted);
          text-decoration: none;
          font-weight: 500;
          font-size: 0.9rem;
          border-radius: 10px;
          transition: all 0.2s ease;
        }
        .sidebar-link:hover {
          background: rgba(255, 255, 255, 0.06);
          color: var(--text-main);
        }
        .sidebar-link.active {
          background: rgba(16, 185, 129, 0.15);
          color: var(--primary-emerald);
          font-weight: 700;
          border-left: 3px solid var(--primary-emerald);
        }
      `}</style>
    </aside>
  );
}
