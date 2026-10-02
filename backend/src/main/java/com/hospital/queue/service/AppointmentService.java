package com.hospital.queue.service;

import com.hospital.queue.dto.AppointmentDTOs;
import com.hospital.queue.entity.*;
import com.hospital.queue.exception.BadRequestException;
import com.hospital.queue.exception.ConcurrencyException;
import com.hospital.queue.exception.ResourceNotFoundException;
import com.hospital.queue.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final DepartmentRepository departmentRepository;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository,
                              PatientRepository patientRepository,
                              DoctorRepository doctorRepository,
                              DepartmentRepository departmentRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.departmentRepository = departmentRepository;
    }

    /**
     * Transactional appointment booking with Concurrency & Double-Booking Protection.
     * Uses Pessimistic Slot Lock & Unique DB Constraints.
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public AppointmentDTOs.AppointmentResponse bookAppointment(AppointmentDTOs.BookAppointmentRequest request) {
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        if (Boolean.FALSE.equals(doctor.getIsAvailable())) {
            throw new BadRequestException("Doctor " + doctor.getUser().getFullName() + " is currently marked unavailable.");
        }

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        // 1. Double Booking Check via Pessimistic Lock Query
        Optional<Appointment> existingSlot = appointmentRepository.findForUpdateSlot(
                request.getDoctorId(), request.getAppointmentDate(), request.getTimeSlot());

        if (existingSlot.isPresent()) {
            throw new ConcurrencyException("Time slot " + request.getTimeSlot() + " on " + request.getAppointmentDate() + " for Dr. " + doctor.getUser().getFullName() + " is already booked!");
        }

        // 2. Persist Appointment with DB Unique Constraint Safety
        try {
            Appointment appointment = new Appointment(
                    patient, doctor, department,
                    request.getAppointmentDate(), request.getTimeSlot(),
                    request.getAppointmentType(), request.getReason()
            );
            appointment = appointmentRepository.saveAndFlush(appointment);
            return mapToResponse(appointment);
        } catch (DataIntegrityViolationException ex) {
            throw new ConcurrencyException("Concurrent booking conflict: Time slot already reserved by another patient.");
        }
    }

    @Transactional
    public AppointmentDTOs.AppointmentResponse cancelAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment = appointmentRepository.save(appointment);
        return mapToResponse(appointment);
    }

    public List<AppointmentDTOs.AppointmentResponse> getPatientAppointments(Long patientId) {
        return appointmentRepository.findByPatientId(patientId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<AppointmentDTOs.AppointmentResponse> getDoctorTodaysAppointments(Long doctorId) {
        return appointmentRepository.findByDoctorIdAndAppointmentDate(doctorId, LocalDate.now()).stream()
                .map(this::mapToResponse)
                .toList();
    }

    public AppointmentDTOs.AppointmentResponse mapToResponse(Appointment appt) {
        AppointmentDTOs.AppointmentResponse dto = new AppointmentDTOs.AppointmentResponse();
        dto.setId(appt.getId());
        dto.setPatientId(appt.getPatient().getId());
        dto.setPatientName(appt.getPatient().getUser().getFullName());
        dto.setDoctorId(appt.getDoctor().getId());
        dto.setDoctorName(appt.getDoctor().getUser().getFullName());
        dto.setDepartmentId(appt.getDepartment().getId());
        dto.setDepartmentName(appt.getDepartment().getName());
        dto.setAppointmentDate(appt.getAppointmentDate());
        dto.setTimeSlot(appt.getTimeSlot());
        dto.setAppointmentType(appt.getAppointmentType());
        dto.setStatus(appt.getStatus());
        dto.setReason(appt.getReason());
        return dto;
    }
}
