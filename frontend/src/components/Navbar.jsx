import React from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <header style={{
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'space-between',
      padding: '16px 32px',
      background: 'rgba(15, 23, 42, 0.9)',
      borderBottom: '1px solid var(--border-color)',
      backdropFilter: 'blur(10px)'
    }}>
      <div style={{ display: 'flex', alignItems: 'center', gap: '12px', cursor: 'pointer' }} onClick={() => navigate('/')}>
        <div style={{
          width: '38px',
          height: '38px',
          borderRadius: '10px',
          background: 'linear-gradient(135deg, #10b981 0%, #06b6d4 100%)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          fontWeight: '800',
          fontSize: '1.2rem',
          color: '#fff'
        }}>
          HQ
        </div>
        <div>
          <h2 style={{ fontSize: '1.15rem', fontWeight: 700 }} className="gradient-text">MediFlow Queue Engine</h2>
          <p style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Intelligent Scheduling & Queue Optimization</p>
        </div>
      </div>

      {user && (
        <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
          <div style={{ textAlign: 'right' }}>
            <div style={{ fontWeight: 600, fontSize: '0.9rem' }}>{user.fullName}</div>
            <span className={`badge ${user.role === 'ADMIN' ? 'badge-emergency' : user.role === 'DOCTOR' ? 'badge-urgent' : 'badge-regular'}`}>
              {user.role}
            </span>
          </div>
          <button className="btn btn-secondary" onClick={handleLogout} style={{ padding: '6px 14px', fontSize: '0.85rem' }}>
            Logout
          </button>
        </div>
      )}
    </header>
  );
}
