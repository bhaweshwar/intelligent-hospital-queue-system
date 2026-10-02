import axios from 'axios';

const API = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json'
  }
});

// Interceptor to inject JWT token in request headers
API.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('jwtToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

export const authService = {
  login: (data) => API.post('/auth/login', data),
  register: (data) => API.post('/auth/register', data)
};

export const departmentService = {
  getAll: () => API.get('/departments'),
  getById: (id) => API.get(`/departments/${id}`)
};

export const doctorService = {
  getAll: () => API.get('/doctors'),
  getByDept: (deptId) => API.get(`/doctors/department/${deptId}`),
  getByUserId: (userId) => API.get(`/doctors/me/${userId}`),
  getDashboard: (doctorId) => API.get(`/doctors/dashboard/${doctorId}`),
  toggleAvailability: (doctorId, available) => API.put(`/doctors/${doctorId}/availability?available=${available}`)
};

export const patientService = {
  getByUserId: (userId) => API.get(`/patients/me/${userId}`),
  getDashboard: (patientId) => API.get(`/patients/dashboard/${patientId}`)
};

export const appointmentService = {
  book: (data) => API.post('/appointments', data),
  cancel: (id) => API.delete(`/appointments/${id}`),
  getPatientAppts: (patientId) => API.get(`/appointments/patient/${patientId}`)
};

export const queueService = {
  join: (data) => API.post('/queues/join', data),
  getDeptQueue: (deptId) => API.get(`/queues/department/${deptId}`),
  getPosition: (patientId) => API.get(`/queues/position/patient/${patientId}`),
  callNext: (deptId, doctorId) => API.post(`/queues/next?departmentId=${deptId}${doctorId ? `&doctorId=${doctorId}` : ''}`)
};

export const consultationService = {
  complete: (data) => API.post('/consultations/complete', data),
  markNoShow: (queueEntryId) => API.post(`/consultations/no-show/${queueEntryId}`)
};

export const adminService = {
  getAnalytics: () => API.get('/admin/analytics')
};

export default API;
