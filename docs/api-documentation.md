# REST API Documentation - Hospital Queue System

## Authentication Endpoints

### 1. User Registration
- **URL**: `POST /api/auth/register`
- **Access**: Public
- **Request Body**:
```json
{
  "fullName": "Jane Doe",
  "email": "jane@patient.com",
  "password": "password123",
  "phoneNumber": "+123456789",
  "role": "PATIENT",
  "age": 28,
  "bloodGroup": "O+"
}
```
- **Response (200 OK)**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "id": 6,
  "email": "jane@patient.com",
  "fullName": "Jane Doe",
  "role": "PATIENT"
}
```

### 2. User Login
- **URL**: `POST /api/auth/login`
- **Access**: Public
- **Request Body**:
```json
{
  "email": "admin@hospital.com",
  "password": "password123"
}
```

---

## Queue Management Endpoints

### 3. Join Department Queue
- **URL**: `POST /api/queues/join`
- **Access**: Authenticated (Patient / Receptionist)
- **Request Body**:
```json
{
  "patientId": 1,
  "departmentId": 1,
  "severityLevel": "WALK_IN"
}
```

### 4. Call Next Patient
- **URL**: `POST /api/queues/next?departmentId=1&doctorId=1`
- **Access**: Authenticated (Doctor / Receptionist)
- **Response**: Dequeued highest priority `QueueEntryDTO`

### 5. Get Live Department Queue
- **URL**: `GET /api/queues/department/{departmentId}`
- **Access**: Public / Authenticated

---

## Appointment Endpoints

### 6. Book Appointment
- **URL**: `POST /api/appointments`
- **Access**: Authenticated
- **Request Body**:
```json
{
  "patientId": 1,
  "doctorId": 1,
  "departmentId": 1,
  "appointmentDate": "2026-09-01",
  "timeSlot": "09:30 AM",
  "appointmentType": "ROUTINE",
  "reason": "General checkup"
}
```
- **Error Response (409 Conflict)**: Returned if doctor/slot is already reserved (Concurrency Protection).

---

## Admin Analytics Endpoint

### 7. Get System Analytics
- **URL**: `GET /api/admin/analytics`
- **Access**: Admin Role (`ROLE_ADMIN`)
