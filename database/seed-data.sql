-- Seed Data for Intelligent Hospital Queue & Appointment Optimization System

-- Insert Departments
INSERT INTO departments (id, name, description, code, avg_consultation_minutes, is_active) VALUES
(1, 'Cardiology', 'Heart and cardiovascular system care', 'CARD', 20, TRUE),
(2, 'Pediatrics', 'Child and adolescent healthcare', 'PED', 15, TRUE),
(3, 'Orthopedics', 'Bone, joint, and musculoskeletal system', 'ORTHO', 18, TRUE),
(4, 'General Medicine', 'General diagnosis, internal medicine and wellness', 'GEN', 12, TRUE),
(5, 'Emergency & Trauma', '24/7 Acute critical patient management', 'EMERG', 10, TRUE);

-- Users: Admin, Receptionist, Doctors, Patients (BCrypt password for 'password123': $2a$10$XPS9QjOkgE9IsRMS6pQEVeJ2lFOkYiX79QUqOp5ssE.S5mN4iwHyC)
INSERT INTO users (id, full_name, email, password, phone_number, role, age) VALUES
(1, 'System Admin', 'admin@hospital.com', '$2a$10$XPS9QjOkgE9IsRMS6pQEVeJ2lFOkYiX79QUqOp5ssE.S5mN4iwHyC', '+1234567890', 'ADMIN', 40),
(2, 'Clara Receptionist', 'receptionist@hospital.com', '$2a$10$XPS9QjOkgE9IsRMS6pQEVeJ2lFOkYiX79QUqOp5ssE.S5mN4iwHyC', '+1234567891', 'RECEPTIONIST', 32),
(3, 'Dr. Sarah Jenkins', 'sarah.jenkins@hospital.com', '$2a$10$XPS9QjOkgE9IsRMS6pQEVeJ2lFOkYiX79QUqOp5ssE.S5mN4iwHyC', '+1234567892', 'DOCTOR', 45),
(4, 'Dr. Marcus Vance', 'marcus.vance@hospital.com', '$2a$10$XPS9QjOkgE9IsRMS6pQEVeJ2lFOkYiX79QUqOp5ssE.S5mN4iwHyC', '+1234567893', 'DOCTOR', 50),
(5, 'Dr. Emily Chen', 'emily.chen@hospital.com', '$2a$10$XPS9QjOkgE9IsRMS6pQEVeJ2lFOkYiX79QUqOp5ssE.S5mN4iwHyC', '+1234567894', 'DOCTOR', 38),
(6, 'John Doe (Patient)', 'john.doe@patient.com', '$2a$10$XPS9QjOkgE9IsRMS6pQEVeJ2lFOkYiX79QUqOp5ssE.S5mN4iwHyC', '+1234567895', 'PATIENT', 29),
(7, 'Alice Smith (Senior)', 'alice.smith@patient.com', '$2a$10$XPS9QjOkgE9IsRMS6pQEVeJ2lFOkYiX79QUqOp5ssE.S5mN4iwHyC', '+1234567896', 'PATIENT', 68),
(8, 'Robert Johnson', 'robert.johnson@patient.com', '$2a$10$XPS9QjOkgE9IsRMS6pQEVeJ2lFOkYiX79QUqOp5ssE.S5mN4iwHyC', '+1234567897', 'PATIENT', 4),
(9, 'Emergency Patient A', 'emerg.a@patient.com', '$2a$10$XPS9QjOkgE9IsRMS6pQEVeJ2lFOkYiX79QUqOp5ssE.S5mN4iwHyC', '+1234567898', 'PATIENT', 52);

-- Doctors
INSERT INTO doctors (id, user_id, department_id, specialization, room_number, is_available, max_daily_capacity) VALUES
(1, 3, 1, 'Cardiovascular Specialist', 'Room 101', TRUE, 30),
(2, 4, 2, 'Pediatric Emergency & Care', 'Room 202', TRUE, 40),
(3, 5, 4, 'Internal Medicine & Diagnostics', 'Room 303', TRUE, 35);

-- Patients
INSERT INTO patients (id, user_id, medical_record_number, blood_group, emergency_contact) VALUES
(1, 6, 'MRN-1001', 'O+', '+1999888777'),
(2, 7, 'MRN-1002', 'A+', '+1999888778'),
(3, 8, 'MRN-1003', 'B+', '+1999888779'),
(4, 9, 'MRN-1004', 'AB-', '+1999888780');
