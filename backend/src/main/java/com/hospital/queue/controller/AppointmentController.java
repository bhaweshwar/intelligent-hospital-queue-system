package com.hospital.queue.controller;

import com.hospital.queue.dto.AppointmentDTOs;
import com.hospital.queue.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AppointmentController {

    private final AppointmentService appointmentService;

    @Autowired
    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public ResponseEntity<AppointmentDTOs.AppointmentResponse> bookAppointment(@Valid @RequestBody AppointmentDTOs.BookAppointmentRequest request) {
        AppointmentDTOs.AppointmentResponse response = appointmentService.bookAppointment(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AppointmentDTOs.AppointmentResponse> cancelAppointment(@PathVariable Long id) {
        AppointmentDTOs.AppointmentResponse response = appointmentService.cancelAppointment(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AppointmentDTOs.AppointmentResponse>> getPatientAppointments(@PathVariable Long patientId) {
        return ResponseEntity.ok(appointmentService.getPatientAppointments(patientId));
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AppointmentDTOs.AppointmentResponse>> getDoctorAppointments(@PathVariable Long doctorId) {
        return ResponseEntity.ok(appointmentService.getDoctorTodaysAppointments(doctorId));
    }
}
