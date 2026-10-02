package com.hospital.queue.dto;

import com.hospital.queue.entity.QueueStatus;
import com.hospital.queue.entity.SeverityLevel;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class QueueDTOs {

    public static class JoinQueueRequest {
        @NotNull(message = "Patient ID is required")
        private Long patientId;

        @NotNull(message = "Department ID is required")
        private Long departmentId;

        private Long doctorId; // Optional preference
        private Long appointmentId; // Optional associated appointment

        @NotNull(message = "Severity level is required")
        private SeverityLevel severityLevel;

        public JoinQueueRequest() {}

        public Long getPatientId() { return patientId; }
        public void setPatientId(Long patientId) { this.patientId = patientId; }
        public Long getDepartmentId() { return departmentId; }
        public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }
        public Long getDoctorId() { return doctorId; }
        public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }
        public Long getAppointmentId() { return appointmentId; }
        public void setAppointmentId(Long appointmentId) { this.appointmentId = appointmentId; }
        public SeverityLevel getSeverityLevel() { return severityLevel; }
        public void setSeverityLevel(SeverityLevel severityLevel) { this.severityLevel = severityLevel; }
    }

    public static class QueueEntryDTO {
        private Long id;
        private String ticketNumber;
        private Long patientId;
        private String patientName;
        private Integer patientAge;
        private Long departmentId;
        private String departmentName;
        private Long doctorId;
        private String doctorName;
        private SeverityLevel severityLevel;
        private Integer basePriority;
        private Double calculatedPriority;
        private QueueStatus status;
        private LocalDateTime joinedAt;
        private Integer positionInQueue;
        private Integer estimatedWaitMinutes;

        public QueueEntryDTO() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTicketNumber() { return ticketNumber; }
        public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }
        public Long getPatientId() { return patientId; }
        public void setPatientId(Long patientId) { this.patientId = patientId; }
        public String getPatientName() { return patientName; }
        public void setPatientName(String patientName) { this.patientName = patientName; }
        public Integer getPatientAge() { return patientAge; }
        public void setPatientAge(Integer patientAge) { this.patientAge = patientAge; }
        public Long getDepartmentId() { return departmentId; }
        public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }
        public String getDepartmentName() { return departmentName; }
        public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
        public Long getDoctorId() { return doctorId; }
        public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }
        public String getDoctorName() { return doctorName; }
        public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
        public SeverityLevel getSeverityLevel() { return severityLevel; }
        public void setSeverityLevel(SeverityLevel severityLevel) { this.severityLevel = severityLevel; }
        public Integer getBasePriority() { return basePriority; }
        public void setBasePriority(Integer basePriority) { this.basePriority = basePriority; }
        public Double getCalculatedPriority() { return calculatedPriority; }
        public void setCalculatedPriority(Double calculatedPriority) { this.calculatedPriority = calculatedPriority; }
        public QueueStatus getStatus() { return status; }
        public void setStatus(QueueStatus status) { this.status = status; }
        public LocalDateTime getJoinedAt() { return joinedAt; }
        public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }
        public Integer getPositionInQueue() { return positionInQueue; }
        public void setPositionInQueue(Integer positionInQueue) { this.positionInQueue = positionInQueue; }
        public Integer getEstimatedWaitMinutes() { return estimatedWaitMinutes; }
        public void setEstimatedWaitMinutes(Integer estimatedWaitMinutes) { this.estimatedWaitMinutes = estimatedWaitMinutes; }
    }

    public static class QueuePositionResponse {
        private String ticketNumber;
        private Integer position;
        private Integer totalInQueue;
        private Integer estimatedWaitMinutes;
        private String departmentName;
        private String doctorName;
        private Double effectivePriority;
        private String statusMessage;

        public QueuePositionResponse() {}

        public String getTicketNumber() { return ticketNumber; }
        public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }
        public Integer getPosition() { return position; }
        public void setPosition(Integer position) { this.position = position; }
        public Integer getTotalInQueue() { return totalInQueue; }
        public void setTotalInQueue(Integer totalInQueue) { this.totalInQueue = totalInQueue; }
        public Integer getEstimatedWaitMinutes() { return estimatedWaitMinutes; }
        public void setEstimatedWaitMinutes(Integer estimatedWaitMinutes) { this.estimatedWaitMinutes = estimatedWaitMinutes; }
        public String getDepartmentName() { return departmentName; }
        public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
        public String getDoctorName() { return doctorName; }
        public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
        public Double getEffectivePriority() { return effectivePriority; }
        public void setEffectivePriority(Double effectivePriority) { this.effectivePriority = effectivePriority; }
        public String getStatusMessage() { return statusMessage; }
        public void setStatusMessage(String statusMessage) { this.statusMessage = statusMessage; }
    }
}
