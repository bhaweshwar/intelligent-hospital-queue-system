package com.hospital.queue.controller;


import com.hospital.queue.dto.DashboardDTOs;
import com.hospital.queue.dto.QueueDTOs;
import com.hospital.queue.entity.Doctor;
import com.hospital.queue.exception.ResourceNotFoundException;
import com.hospital.queue.repository.DoctorRepository;
import com.hospital.queue.service.AppointmentService;
import com.hospital.queue.service.QueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DoctorController {

    private final DoctorRepository doctorRepository;
    private final QueueService queueService;
    private final AppointmentService appointmentService;

    @Autowired
    public DoctorController(DoctorRepository doctorRepository, QueueService queueService, AppointmentService appointmentService) {
        this.doctorRepository = doctorRepository;
        this.queueService = queueService;
        this.appointmentService = appointmentService;
    }

    @GetMapping
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        return ResponseEntity.ok(doctorRepository.findAll());
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<Doctor>> getDoctorsByDepartment(@PathVariable Long departmentId) {
        return ResponseEntity.ok(doctorRepository.findByDepartmentId(departmentId));
    }

    @GetMapping("/me/{userId}")
    public ResponseEntity<Doctor> getDoctorByUserId(@PathVariable Long userId) {
        Doctor doctor = doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor record not found for user ID: " + userId));
        return ResponseEntity.ok(doctor);
    }

    @PutMapping("/{doctorId}/availability")
    public ResponseEntity<Doctor> toggleAvailability(@PathVariable Long doctorId, @RequestParam Boolean available) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        doctor.setIsAvailable(available);
        doctor = doctorRepository.save(doctor);
        return ResponseEntity.ok(doctor);
    }

    @GetMapping("/dashboard/{doctorId}")
    public ResponseEntity<DashboardDTOs.DoctorDashboardResponse> getDoctorDashboard(@PathVariable Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        DashboardDTOs.DoctorDashboardResponse dashboard = new DashboardDTOs.DoctorDashboardResponse();
        dashboard.setDoctorId(doctor.getId());
        dashboard.setDoctorName(doctor.getUser().getFullName());
        dashboard.setIsAvailable(doctor.getIsAvailable());
        dashboard.setRoomNumber(doctor.getRoomNumber());
        dashboard.setDepartmentName(doctor.getDepartment().getName());

        List<QueueDTOs.QueueEntryDTO> waiting = queueService.getDepartmentQueue(doctor.getDepartment().getId());
        dashboard.setWaitingQueue(waiting);
        
        if (!waiting.isEmpty()) {
            dashboard.setNextPatientInQueue(waiting.get(0));
        }

        dashboard.setTodaysAppointments(appointmentService.getDoctorTodaysAppointments(doctorId));

        return ResponseEntity.ok(dashboard);
    }
}
