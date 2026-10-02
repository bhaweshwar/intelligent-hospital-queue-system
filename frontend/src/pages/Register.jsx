import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { authService } from '../services/api';
import { useNavigate, Link } from 'react-router-dom';

export default function Register() {
  const [formData, setFormData] = useState({
    fullName: '',
    email: '',
    password: '',
    phoneNumber: '',
    role: 'PATIENT',
    age: '',
    bloodGroup: 'O+',
    emergencyContact: ''
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleRegister = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const res = await authService.register({
        ...formData,
        age: formData.age ? parseInt(formData.age) : 30
      });
      login(res.data);
      navigate(formData.role === 'PATIENT' ? '/patient' : '/doctor');
    } catch (err) {
      setError(err.response?.data?.message || 'Registration failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ minHeight: '80vh', display: 'flex', alignItems: 'center', justifyContent: 'center', padding: '20px' }}>
      <div className="glass-card" style={{ width: '100%', maxWidth: '500px' }}>
        <div style={{ textAlign: 'center', marginBottom: '24px' }}>
          <h2 style={{ fontSize: '1.8rem', fontWeight: 800 }} className="gradient-text">Create Account</h2>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.85rem', marginTop: '4px' }}>
            Register as a patient to manage appointments & join live queues
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

        <form onSubmit={handleRegister}>
          <div className="form-group">
            <label>Full Name</label>
            <input type="text" name="fullName" className="form-control" placeholder="Jane Doe" value={formData.fullName} onChange={handleChange} required />
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
            <div className="form-group">
              <label>Email Address</label>
              <input type="email" name="email" className="form-control" placeholder="jane@example.com" value={formData.email} onChange={handleChange} required />
            </div>
            <div className="form-group">
              <label>Password</label>
              <input type="password" name="password" className="form-control" placeholder="••••••••" value={formData.password} onChange={handleChange} required />
            </div>
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '12px' }}>
            <div className="form-group">
              <label>Phone Number</label>
              <input type="text" name="phoneNumber" className="form-control" placeholder="+123456789" value={formData.phoneNumber} onChange={handleChange} />
            </div>
            <div className="form-group">
              <label>Age</label>
              <input type="number" name="age" className="form-control" placeholder="30" value={formData.age} onChange={handleChange} required />
            </div>
            <div className="form-group">
              <label>Blood Group</label>
              <select name="bloodGroup" className="form-control" value={formData.bloodGroup} onChange={handleChange}>
                <option value="O+">O+</option>
                <option value="A+">A+</option>
                <option value="B+">B+</option>
                <option value="AB+">AB+</option>
                <option value="O-">O-</option>
              </select>
            </div>
          </div>

          <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '8px', padding: '12px' }} disabled={loading}>
            {loading ? 'Creating Account...' : 'Complete Registration'}
          </button>
        </form>

        <div style={{ textAlign: 'center', marginTop: '20px', fontSize: '0.85rem', color: 'var(--text-muted)' }}>
          Already have an account? <Link to="/login" style={{ color: 'var(--primary-emerald)', fontWeight: 600 }}>Sign In</Link>
        </div>
      </div>
    </div>
  );
}
