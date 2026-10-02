import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import Navbar from './components/Navbar';
import Sidebar from './components/Sidebar';
import Login from './pages/Login';
import Register from './pages/Register';
import PatientDashboard from './pages/PatientDashboard';
import DoctorDashboard from './pages/DoctorDashboard';
import ReceptionistDashboard from './pages/ReceptionistDashboard';
import AdminDashboard from './pages/AdminDashboard';
import BookAppointmentPage from './pages/BookAppointmentPage';
import './styles/global.css';

const ProtectedRoute = ({ children, allowedRoles }) => {
  const { user, isAuthenticated } = useAuth();
  if (!isAuthenticated) return <Navigate to="/login" replace />;
  if (allowedRoles && !allowedRoles.includes(user?.role)) {
    if (user?.role === 'PATIENT') return <Navigate to="/patient" replace />;
    if (user?.role === 'DOCTOR') return <Navigate to="/doctor" replace />;
    if (user?.role === 'RECEPTIONIST') return <Navigate to="/receptionist" replace />;
    if (user?.role === 'ADMIN') return <Navigate to="/admin" replace />;
  }
  return children;
};

const RootRedirect = () => {
  const { user, isAuthenticated } = useAuth();
  if (!isAuthenticated) return <Navigate to="/login" replace />;
  if (user?.role === 'PATIENT') return <Navigate to="/patient" replace />;
  if (user?.role === 'DOCTOR') return <Navigate to="/doctor" replace />;
  if (user?.role === 'RECEPTIONIST') return <Navigate to="/receptionist" replace />;
  if (user?.role === 'ADMIN') return <Navigate to="/admin" replace />;
  return <Navigate to="/login" replace />;
};

export default function App() {
  return (
    <AuthProvider>
      <Router>
        <div style={{ minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>
          <Navbar />
          <div className="app-container">
            <Sidebar />
            <main className="main-content">
              <Routes>
                <Route path="/" element={<RootRedirect />} />
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />
                
                <Route path="/patient" element={
                  <ProtectedRoute allowedRoles={['PATIENT']}>
                    <PatientDashboard />
                  </ProtectedRoute>
                } />

                <Route path="/doctor" element={
                  <ProtectedRoute allowedRoles={['DOCTOR']}>
                    <DoctorDashboard />
                  </ProtectedRoute>
                } />

                <Route path="/receptionist" element={
                  <ProtectedRoute allowedRoles={['RECEPTIONIST', 'ADMIN']}>
                    <ReceptionistDashboard />
                  </ProtectedRoute>
                } />

                <Route path="/admin" element={
                  <ProtectedRoute allowedRoles={['ADMIN']}>
                    <AdminDashboard />
                  </ProtectedRoute>
                } />

                <Route path="/book-appointment" element={
                  <ProtectedRoute allowedRoles={['PATIENT', 'RECEPTIONIST', 'ADMIN']}>
                    <BookAppointmentPage />
                  </ProtectedRoute>
                } />

                <Route path="*" element={<Navigate to="/" replace />} />
              </Routes>
            </main>
          </div>
        </div>
      </Router>
    </AuthProvider>
  );
}
