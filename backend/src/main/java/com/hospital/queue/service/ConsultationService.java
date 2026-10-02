package com.hospital.queue.service;

import com.hospital.queue.dto.ConsultationDTOs;
import com.hospital.queue.engine.HospitalQueueManager;
import com.hospital.queue.entity.*;
import com.hospital.queue.exception.ResourceNotFoundException;
import com.hospital.queue.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class ConsultationService {

    private final ConsultationRepository consultationRepository;
    private final QueueRepository queueRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final HospitalQueueManager queueManager;

    @Autowired
    public ConsultationService(ConsultationRepository consultationRepository,
                               QueueRepository queueRepository,
                               DoctorRepository doctorRepository,
                               AppointmentRepository appointmentRepository,
                               HospitalQueueManager queueManager) {
        this.consultationRepository = consultationRepository;
        this.queueRepository = queueRepository;
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.queueManager = queueManager;
    }

    @Transactional
    public ConsultationDTOs.ConsultationResponse completeConsultation(ConsultationDTOs.CompleteConsultationRequest request) {
        QueueEntry entry = queueRepository.findById(request.getConsultationId())
                .orElseThrow(() -> new ResourceNotFoundException("Queue entry not found"));

        Doctor doctor = entry.getDoctor();
        if (doctor == null) {
            throw new ResourceNotFoundException("No doctor associated with queue entry");
        }

        LocalDateTime endedAt = LocalDateTime.now();
        LocalDateTime startedAt = entry.getCalledAt() != null ? entry.getCalledAt() : endedAt.minusMinutes(15);
        int duration = (int) Math.max(1, Duration.between(startedAt, endedAt).toMinutes());

        Consultation consultation = new Consultation(
                entry.getAppointment(),
                entry,
                doctor,
                entry.getPatient(),
                request.getDiagnosis(),
                request.getPrescription(),
                startedAt,
                endedAt,
                duration
        );

        consultation = consultationRepository.save(consultation);

        // Update Queue Entry status
        entry.setStatus(QueueStatus.COMPLETED);
        entry.setCompletedAt(endedAt);
        queueRepository.save(entry);

        // Remove from memory Heap
        queueManager.removeFromQueue(entry.getDepartment().getId(), entry.getId());

        // Update Appointment status if linked
        if (entry.getAppointment() != null) {
            entry.getAppointment().setStatus(AppointmentStatus.COMPLETED);
            appointmentRepository.save(entry.getAppointment());
        }

        return mapToDTO(consultation);
    }

    @Transactional
    public void markNoShow(Long queueEntryId) {
        QueueEntry entry = queueRepository.findById(queueEntryId)
                .orElseThrow(() -> new ResourceNotFoundException("Queue entry not found"));

        entry.setStatus(QueueStatus.NO_SHOW);
        queueRepository.save(entry);
        queueManager.removeFromQueue(entry.getDepartment().getId(), entry.getId());

        if (entry.getAppointment() != null) {
            entry.getAppointment().setStatus(AppointmentStatus.NO_SHOW);
            appointmentRepository.save(entry.getAppointment());
        }
    }

    private ConsultationDTOs.ConsultationResponse mapToDTO(Consultation c) {
        ConsultationDTOs.ConsultationResponse dto = new ConsultationDTOs.ConsultationResponse();
        dto.setId(c.getId());
        dto.setQueueEntryId(c.getQueueEntry().getId());
        dto.setTicketNumber(c.getQueueEntry().getTicketNumber());
        dto.setDoctorId(c.getDoctor().getId());
        dto.setDoctorName(c.getDoctor().getUser().getFullName());
        dto.setPatientId(c.getPatient().getId());
        dto.setPatientName(c.getPatient().getUser().getFullName());
        dto.setDiagnosis(c.getDiagnosis());
        dto.setPrescription(c.getPrescription());
        dto.setStartedAt(c.getStartedAt());
        dto.setEndedAt(c.getEndedAt());
        dto.setDurationMinutes(c.getDurationMinutes());
        return dto;
    }
}
