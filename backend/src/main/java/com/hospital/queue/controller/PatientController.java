package com.hospital.queue.controller;

import com.hospital.queue.dto.DashboardDTOs;
import com.hospital.queue.entity.Patient;
import com.hospital.queue.exception.ResourceNotFoundException;
import com.hospital.queue.repository.PatientRepository;
import com.hospital.queue.service.AppointmentService;
import com.hospital.queue.service.QueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PatientController {

    private final PatientRepository patientRepository;
    private final AppointmentService appointmentService;
    private final QueueService queueService;

    @Autowired
    public PatientController(PatientRepository patientRepository, AppointmentService appointmentService, QueueService queueService) {
        this.patientRepository = patientRepository;
        this.appointmentService = appointmentService;
        this.queueService = queueService;
    }

    @GetMapping("/me/{userId}")
    public ResponseEntity<Patient> getPatientProfile(@PathVariable Long userId) {
        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found for user ID: " + userId));
        return ResponseEntity.ok(patient);
    }

    @GetMapping("/dashboard/{patientId}")
    public ResponseEntity<DashboardDTOs.PatientDashboardResponse> getPatientDashboard(@PathVariable Long patientId) {
        DashboardDTOs.PatientDashboardResponse dashboard = new DashboardDTOs.PatientDashboardResponse();
        
        var appointments = appointmentService.getPatientAppointments(patientId);
        dashboard.setAppointmentHistory(appointments);
        
        if (!appointments.isEmpty()) {
            dashboard.setUpcomingAppointment(appointments.get(0));
        }

        try {
            var position = queueService.getPatientQueuePosition(patientId);
            dashboard.setCurrentQueuePosition(position);
        } catch (Exception ignored) {
            // No active queue ticket
        }

        return ResponseEntity.ok(dashboard);
    }
}
