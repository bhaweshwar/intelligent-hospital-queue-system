package com.hospital.queue.service;

import com.hospital.queue.dto.AppointmentDTOs;
import com.hospital.queue.entity.*;
import com.hospital.queue.exception.ConcurrencyException;
import com.hospital.queue.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class AppointmentConcurrencyTest {

    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should throw ConcurrencyException when attempting to double-book an already reserved time slot")
    public void testDoubleBookingConcurrencyPrevention() {
        Long doctorId = 1L;
        Long patientId = 2L;
        Long deptId = 3L;
        LocalDate date = LocalDate.now().plusDays(1);
        String slot = "10:00 AM";

        User docUser = new User("Dr. Smith", "doc@test.com", "pass", "123", Role.DOCTOR, 40);
        Department dept = new Department("Cardiology", "Heart", "CARD", 15);
        Doctor doctor = new Doctor(docUser, dept, "Cardiologist", "101", 30);
        doctor.setId(doctorId);

        User patUser = new User("Patient Bob", "bob@test.com", "pass", "123", Role.PATIENT, 25);
        Patient patient = new Patient(patUser, "MRN-999", "O+", "911");
        patient.setId(patientId);

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(departmentRepository.findById(deptId)).thenReturn(Optional.of(dept));

        // Mock existing slot found in Pessimistic Lock check
        Appointment existingAppt = new Appointment(patient, doctor, dept, date, slot, AppointmentType.ROUTINE, "Checkup");
        when(appointmentRepository.findForUpdateSlot(doctorId, date, slot)).thenReturn(Optional.of(existingAppt));

        AppointmentDTOs.BookAppointmentRequest request = new AppointmentDTOs.BookAppointmentRequest();
        request.setDoctorId(doctorId);
        request.setPatientId(patientId);
        request.setDepartmentId(deptId);
        request.setAppointmentDate(date);
        request.setTimeSlot(slot);
        request.setAppointmentType(AppointmentType.ROUTINE);

        assertThrows(ConcurrencyException.class, () -> {
            appointmentService.bookAppointment(request);
        }, "Booking an occupied slot should throw a ConcurrencyException");
    }

    @Test
    @DisplayName("Should throw ConcurrencyException when DB unique constraint fails on race condition")
    public void testDatabaseConstraintConcurrencyFallback() {
        Long doctorId = 1L;
        Long patientId = 2L;
        Long deptId = 3L;
        LocalDate date = LocalDate.now().plusDays(1);
        String slot = "11:30 AM";

        User docUser = new User("Dr. Smith", "doc@test.com", "pass", "123", Role.DOCTOR, 40);
        Department dept = new Department("Cardiology", "Heart", "CARD", 15);
        Doctor doctor = new Doctor(docUser, dept, "Cardiologist", "101", 30);
        doctor.setId(doctorId);

        User patUser = new User("Patient Alice", "alice@test.com", "pass", "123", Role.PATIENT, 28);
        Patient patient = new Patient(patUser, "MRN-888", "A+", "911");
        patient.setId(patientId);

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(departmentRepository.findById(deptId)).thenReturn(Optional.of(dept));
        when(appointmentRepository.findForUpdateSlot(doctorId, date, slot)).thenReturn(Optional.empty());

        // Simulate DB Unique Constraint Violation (simultaneous parallel insert)
        when(appointmentRepository.saveAndFlush(any(Appointment.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate key constraint violated"));

        AppointmentDTOs.BookAppointmentRequest request = new AppointmentDTOs.BookAppointmentRequest();
        request.setDoctorId(doctorId);
        request.setPatientId(patientId);
        request.setDepartmentId(deptId);
        request.setAppointmentDate(date);
        request.setTimeSlot(slot);
        request.setAppointmentType(AppointmentType.ROUTINE);

        assertThrows(ConcurrencyException.class, () -> {
            appointmentService.bookAppointment(request);
        }, "DataIntegrityViolationException on save should be caught and translated to ConcurrencyException");
    }
}
