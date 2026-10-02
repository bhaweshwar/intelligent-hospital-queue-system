package com.hospital.queue.dto;

import com.hospital.queue.entity.AppointmentStatus;
import com.hospital.queue.entity.AppointmentType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class AppointmentDTOs {

    public static class BookAppointmentRequest {
        @NotNull(message = "Patient ID is required")
        private Long patientId;

        @NotNull(message = "Doctor ID is required")
        private Long doctorId;

        @NotNull(message = "Department ID is required")
        private Long departmentId;

        @NotNull(message = "Appointment date is required")
        @FutureOrPresent(message = "Appointment date must be today or in the future")
        private LocalDate appointmentDate;

        @NotBlank(message = "Time slot is required")
        private String timeSlot;

        @NotNull(message = "Appointment type is required")
        private AppointmentType appointmentType;

        private String reason;

        public BookAppointmentRequest() {}

        public Long getPatientId() { return patientId; }
        public void setPatientId(Long patientId) { this.patientId = patientId; }
        public Long getDoctorId() { return doctorId; }
        public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }
        public Long getDepartmentId() { return departmentId; }
        public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }
        public LocalDate getAppointmentDate() { return appointmentDate; }
        public void setAppointmentDate(LocalDate appointmentDate) { this.appointmentDate = appointmentDate; }
        public String getTimeSlot() { return timeSlot; }
        public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }
        public AppointmentType getAppointmentType() { return appointmentType; }
        public void setAppointmentType(AppointmentType appointmentType) { this.appointmentType = appointmentType; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }

    public static class AppointmentResponse {
        private Long id;
        private Long patientId;
        private String patientName;
        private Long doctorId;
        private String doctorName;
        private Long departmentId;
        private String departmentName;
        private LocalDate appointmentDate;
        private String timeSlot;
        private AppointmentType appointmentType;
        private AppointmentStatus status;
        private String reason;

        public AppointmentResponse() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getPatientId() { return patientId; }
        public void setPatientId(Long patientId) { this.patientId = patientId; }
        public String getPatientName() { return patientName; }
        public void setPatientName(String patientName) { this.patientName = patientName; }
        public Long getDoctorId() { return doctorId; }
        public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }
        public String getDoctorName() { return doctorName; }
        public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
        public Long getDepartmentId() { return departmentId; }
        public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }
        public String getDepartmentName() { return departmentName; }
        public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
        public LocalDate getAppointmentDate() { return appointmentDate; }
        public void setAppointmentDate(LocalDate appointmentDate) { this.appointmentDate = appointmentDate; }
        public String getTimeSlot() { return timeSlot; }
        public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }
        public AppointmentType getAppointmentType() { return appointmentType; }
        public void setAppointmentType(AppointmentType appointmentType) { this.appointmentType = appointmentType; }
        public AppointmentStatus getStatus() { return status; }
        public void setStatus(AppointmentStatus status) { this.status = status; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }
}
