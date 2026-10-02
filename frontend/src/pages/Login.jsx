import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { authService } from '../services/api';
import { useNavigate, Link } from 'react-router-dom';

export default function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const res = await authService.login({ email, password });
      login(res.data);
      const role = res.data.role;
      if (role === 'PATIENT') navigate('/patient');
      else if (role === 'DOCTOR') navigate('/doctor');
      else if (role === 'RECEPTIONIST') navigate('/receptionist');
      else if (role === 'ADMIN') navigate('/admin');
      else navigate('/');
    } catch (err) {
      setError(err.response?.data?.message || 'Invalid credentials or login failed');
    } finally {
      setLoading(false);
    }
  };

  const fillDemo = (demoEmail, demoPass) => {
    setEmail(demoEmail);
    setPassword(demoPass);
  };

  return (
    <div style={{ minHeight: '80vh', display: 'flex', alignItems: 'center', justifyContent: 'center', padding: '20px' }}>
      <div className="glass-card" style={{ width: '100%', maxWidth: '440px' }}>
        <div style={{ textAlign: 'center', marginBottom: '24px' }}>
          <h2 style={{ fontSize: '1.8rem', fontWeight: 800 }} className="gradient-text">Welcome Back</h2>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.85rem', marginTop: '4px' }}>
            Sign in to access your queue & appointment portal
          </p>
        </div>

        {error && (
          <div style={{
            background: 'rgba(239, 68, 68, 0.15)',
            border: '1px solid rgba(239, 68, 68, 0.3)',
            color: '#fca5a5',
            padding: '10px 14px',
            borderRadius: '10px',
            fontSize: '0.85rem',
            marginBottom: '16px'
          }}>
            {error}
          </div>
        )}

        <form onSubmit={handleLogin}>
          <div className="form-group">
            <label>Email Address</label>
            <input
              type="email"
              className="form-control"
              placeholder="user@hospital.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label>Password</label>
            <input
              type="password"
              className="form-control"
              placeholder="••••••••"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>

          <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '8px', padding: '12px' }} disabled={loading}>
            {loading ? 'Authenticating...' : 'Sign In'}
          </button>
        </form>

        <div style={{ marginTop: '24px', paddingTop: '16px', borderTop: '1px solid var(--border-color)' }}>
          <div style={{ fontSize: '0.75rem', color: 'var(--text-dim)', fontWeight: 700, marginBottom: '8px', textTransform: 'uppercase' }}>
            Quick Demo Logins (Click to autofill)
          </div>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '8px' }}>
            <button className="btn btn-secondary" style={{ fontSize: '0.75rem', padding: '6px' }} onClick={() => fillDemo('admin@hospital.com', 'password123')}>
              ⚙️ Admin
            </button>
            <button className="btn btn-secondary" style={{ fontSize: '0.75rem', padding: '6px' }} onClick={() => fillDemo('receptionist@hospital.com', 'password123')}>
              📋 Receptionist
            </button>
            <button className="btn btn-secondary" style={{ fontSize: '0.75rem', padding: '6px' }} onClick={() => fillDemo('sarah.jenkins@hospital.com', 'password123')}>
              🩺 Doctor (Sarah)
            </button>
            <button className="btn btn-secondary" style={{ fontSize: '0.75rem', padding: '6px' }} onClick={() => fillDemo('john.doe@patient.com', 'password123')}>
              👤 Patient (John)
            </button>
          </div>
        </div>

        <div style={{ textAlign: 'center', marginTop: '20px', fontSize: '0.85rem', color: 'var(--text-muted)' }}>
          Don't have an account? <Link to="/register" style={{ color: 'var(--primary-emerald)', fontWeight: 600 }}>Register Now</Link>
        </div>
      </div>
    </div>
  );
}
