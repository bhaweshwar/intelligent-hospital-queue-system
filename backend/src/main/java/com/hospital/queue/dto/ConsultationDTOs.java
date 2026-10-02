package com.hospital.queue.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class ConsultationDTOs {

    public static class StartConsultationRequest {
        @NotNull(message = "Queue entry ID is required")
        private Long queueEntryId;

        @NotNull(message = "Doctor ID is required")
        private Long doctorId;

        public StartConsultationRequest() {}

        public Long getQueueEntryId() { return queueEntryId; }
        public void setQueueEntryId(Long queueEntryId) { this.queueEntryId = queueEntryId; }
        public Long getDoctorId() { return doctorId; }
        public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }
    }

    public static class CompleteConsultationRequest {
        @NotNull(message = "Consultation ID is required")
        private Long consultationId;

        private String diagnosis;
        private String prescription;

        public CompleteConsultationRequest() {}

        public Long getConsultationId() { return consultationId; }
        public void setConsultationId(Long consultationId) { this.consultationId = consultationId; }
        public String getDiagnosis() { return diagnosis; }
        public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
        public String getPrescription() { return prescription; }
        public void setPrescription(String prescription) { this.prescription = prescription; }
    }

    public static class ConsultationResponse {
        private Long id;
        private Long queueEntryId;
        private String ticketNumber;
        private Long doctorId;
        private String doctorName;
        private Long patientId;
        private String patientName;
        private String diagnosis;
        private String prescription;
        private LocalDateTime startedAt;
        private LocalDateTime endedAt;
        private Integer durationMinutes;

        public ConsultationResponse() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getQueueEntryId() { return queueEntryId; }
        public void setQueueEntryId(Long queueEntryId) { this.queueEntryId = queueEntryId; }
        public String getTicketNumber() { return ticketNumber; }
        public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }
        public Long getDoctorId() { return doctorId; }
        public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }
        public String getDoctorName() { return doctorName; }
        public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
        public Long getPatientId() { return patientId; }
        public void setPatientId(Long patientId) { this.patientId = patientId; }
        public String getPatientName() { return patientName; }
        public void setPatientName(String patientName) { this.patientName = patientName; }
        public String getDiagnosis() { return diagnosis; }
        public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
        public String getPrescription() { return prescription; }
        public void setPrescription(String prescription) { this.prescription = prescription; }
        public LocalDateTime getStartedAt() { return startedAt; }
        public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
        public LocalDateTime getEndedAt() { return endedAt; }
        public void setEndedAt(LocalDateTime endedAt) { this.endedAt = endedAt; }
        public Integer getDurationMinutes() { return durationMinutes; }
        public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
    }
}
