-- PostgreSQL / H2 Relational Database Schema
-- Project: Intelligent Hospital Queue & Appointment Optimization System

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20),
    role VARCHAR(20) NOT NULL, -- PATIENT, DOCTOR, RECEPTIONIST, ADMIN
    age INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS departments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    code VARCHAR(10) NOT NULL UNIQUE,
    avg_consultation_minutes INT DEFAULT 15,
    is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS doctors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    department_id BIGINT NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    room_number VARCHAR(20),
    is_available BOOLEAN DEFAULT TRUE,
    max_daily_capacity INT DEFAULT 40,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (department_id) REFERENCES departments(id)
);

CREATE TABLE IF NOT EXISTS patients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    medical_record_number VARCHAR(50) NOT NULL UNIQUE,
    blood_group VARCHAR(10),
    emergency_contact VARCHAR(20),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS appointments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    department_id BIGINT NOT NULL,
    appointment_date DATE NOT NULL,
    time_slot VARCHAR(20) NOT NULL, -- e.g. "09:00", "09:30"
    appointment_type VARCHAR(30) NOT NULL, -- ROUTINE, FOLLOW_UP, URGENT
    status VARCHAR(20) NOT NULL, -- SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED, NO_SHOW
    reason TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_doctor_schedule UNIQUE (doctor_id, appointment_date, time_slot),
    FOREIGN KEY (patient_id) REFERENCES patients(id),
    FOREIGN KEY (doctor_id) REFERENCES doctors(id),
    FOREIGN KEY (department_id) REFERENCES departments(id)
);

CREATE TABLE IF NOT EXISTS queue_entries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    department_id BIGINT NOT NULL,
    doctor_id BIGINT,
    appointment_id BIGINT,
    ticket_number VARCHAR(20) NOT NULL UNIQUE,
    severity_level VARCHAR(30) NOT NULL, -- EMERGENCY, URGENT, HIGH_PRIORITY_APPOINTMENT, REGULAR_APPOINTMENT, WALK_IN
    base_priority INT NOT NULL,
    calculated_priority DOUBLE PRECISION NOT NULL,
    status VARCHAR(20) NOT NULL, -- WAITING, IN_CONSULTATION, COMPLETED, CANCELLED, NO_SHOW
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    called_at TIMESTAMP,
    completed_at TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id),
    FOREIGN KEY (department_id) REFERENCES departments(id),
    FOREIGN KEY (doctor_id) REFERENCES doctors(id),
    FOREIGN KEY (appointment_id) REFERENCES appointments(id)
);

CREATE TABLE IF NOT EXISTS consultations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    appointment_id BIGINT,
    queue_entry_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    patient_id BIGINT NOT NULL,
    diagnosis TEXT,
    prescription TEXT,
    started_at TIMESTAMP NOT NULL,
    ended_at TIMESTAMP NOT NULL,
    duration_minutes INT NOT NULL,
    FOREIGN KEY (appointment_id) REFERENCES appointments(id),
    FOREIGN KEY (queue_entry_id) REFERENCES queue_entries(id),
    FOREIGN KEY (doctor_id) REFERENCES doctors(id),
    FOREIGN KEY (patient_id) REFERENCES patients(id)
);

CREATE INDEX IF NOT EXISTS idx_queue_dept_status ON queue_entries(department_id, status);
CREATE INDEX IF NOT EXISTS idx_appts_doctor_date ON appointments(doctor_id, appointment_date);
