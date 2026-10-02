package com.hospital.queue.service;

import com.hospital.queue.dto.QueueDTOs;
import com.hospital.queue.engine.HospitalQueueManager;
import com.hospital.queue.engine.PriorityCalculationEngine;
import com.hospital.queue.entity.*;
import com.hospital.queue.exception.BadRequestException;
import com.hospital.queue.exception.ResourceNotFoundException;
import com.hospital.queue.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class QueueService {

    private final QueueRepository queueRepository;
    private final PatientRepository patientRepository;
    private final DepartmentRepository departmentRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final HospitalQueueManager queueManager;
    private final DoctorAssignmentService doctorAssignmentService;
    private final WaitingTimeService waitingTimeService;
    private final PriorityCalculationEngine priorityEngine;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public QueueService(QueueRepository queueRepository, PatientRepository patientRepository,
                        DepartmentRepository departmentRepository, DoctorRepository doctorRepository,
                        AppointmentRepository appointmentRepository, HospitalQueueManager queueManager,
                        DoctorAssignmentService doctorAssignmentService, WaitingTimeService waitingTimeService,
                        PriorityCalculationEngine priorityEngine,
                        @Autowired(required = false) SimpMessagingTemplate messagingTemplate) {
        this.queueRepository = queueRepository;
        this.patientRepository = patientRepository;
        this.departmentRepository = departmentRepository;
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.queueManager = queueManager;
        this.doctorAssignmentService = doctorAssignmentService;
        this.waitingTimeService = waitingTimeService;
        this.priorityEngine = priorityEngine;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public QueueDTOs.QueueEntryDTO joinQueue(QueueDTOs.JoinQueueRequest request) {
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        // Check if patient is already waiting in an active queue
        Optional<QueueEntry> activeEntry = queueRepository.findByPatientIdAndStatus(patient.getId(), QueueStatus.WAITING);
        if (activeEntry.isPresent()) {
            throw new BadRequestException("Patient already has an active waiting ticket: " + activeEntry.get().getTicketNumber());
        }

        // Doctor assignment check/preference
        Doctor assignedDoctor = null;
        if (request.getDoctorId() != null) {
            assignedDoctor = doctorRepository.findById(request.getDoctorId()).orElse(null);
        }
        if (assignedDoctor == null) {
            assignedDoctor = doctorAssignmentService.assignBestAvailableDoctor(department.getId(), null).orElse(null);
        }

        // Appointment check
        Appointment appointment = null;
        if (request.getAppointmentId() != null) {
            appointment = appointmentRepository.findById(request.getAppointmentId()).orElse(null);
        }

        // Generate Ticket Number e.g., CARD-104
        String ticketNumber = department.getCode() + "-" + (System.currentTimeMillis() % 10000);

        QueueEntry entry = new QueueEntry(patient, department, assignedDoctor, appointment, ticketNumber, request.getSeverityLevel());
        entry = queueRepository.save(entry);

        // Push into Heap-based Queue Manager (DSA Engine)
        queueManager.enqueue(entry);

        // Broadcast real-time update if WebSocket configured
        notifyQueueChange(department.getId());

        return mapToDTO(entry);
    }

    @Transactional
    public Optional<QueueDTOs.QueueEntryDTO> callNextPatient(Long departmentId, Long doctorId) {
        Optional<QueueEntry> nextOpt = queueManager.dequeueNextPatient(departmentId, doctorId);
        if (nextOpt.isPresent()) {
            QueueEntry entry = nextOpt.get();
            entry.setStatus(QueueStatus.IN_CONSULTATION);
            entry.setCalledAt(LocalDateTime.now());
            if (doctorId != null && entry.getDoctor() == null) {
                doctorRepository.findById(doctorId).ifPresent(entry::setDoctor);
            }
            queueRepository.save(entry);

            if (entry.getAppointment() != null) {
                entry.getAppointment().setStatus(AppointmentStatus.IN_CONSULTATION);
                appointmentRepository.save(entry.getAppointment());
            }

            notifyQueueChange(departmentId);
            return Optional.of(mapToDTO(entry));
        }
        return Optional.empty();
    }

    public QueueDTOs.QueuePositionResponse getPatientQueuePosition(Long patientId) {
        Optional<QueueEntry> activeEntry = queueRepository.findByPatientIdAndStatus(patientId, QueueStatus.WAITING);
        if (activeEntry.isEmpty()) {
            throw new ResourceNotFoundException("No active waiting queue entry found for patient");
        }

        QueueEntry entry = activeEntry.get();
        Long departmentId = entry.getDepartment().getId();
        int position = queueManager.getQueuePosition(departmentId, entry.getId());
        List<QueueEntry> sortedQueue = queueManager.getSortedQueueForDepartment(departmentId);

        int waitMins = waitingTimeService.estimateWaitMinutes(
                position,
                entry.getDoctor() != null ? entry.getDoctor().getId() : null,
                departmentId
        );

        QueueDTOs.QueuePositionResponse response = new QueueDTOs.QueuePositionResponse();
        response.setTicketNumber(entry.getTicketNumber());
        response.setPosition(position);
        response.setTotalInQueue(sortedQueue.size());
        response.setEstimatedWaitMinutes(waitMins);
        response.setDepartmentName(entry.getDepartment().getName());
        response.setDoctorName(entry.getDoctor() != null ? entry.getDoctor().getUser().getFullName() : "Assigned Duty Doctor");
        response.setEffectivePriority(priorityEngine.calculateEffectivePriority(entry));
        response.setStatusMessage(position == 1 ? "You are next in line! Please head to consultation room." : "Waiting in queue");

        return response;
    }

    public List<QueueDTOs.QueueEntryDTO> getDepartmentQueue(Long departmentId) {
        return queueManager.getSortedQueueForDepartment(departmentId).stream()
                .map(this::mapToDTO)
                .toList();
    }

    private void notifyQueueChange(Long departmentId) {
        if (messagingTemplate != null) {
            try {
                messagingTemplate.convertAndSend("/topic/queue/" + departmentId, "QUEUE_UPDATED");
            } catch (Exception ignored) {}
        }
    }

    public QueueDTOs.QueueEntryDTO mapToDTO(QueueEntry entry) {
        QueueDTOs.QueueEntryDTO dto = new QueueDTOs.QueueEntryDTO();
        dto.setId(entry.getId());
        dto.setTicketNumber(entry.getTicketNumber());
        dto.setPatientId(entry.getPatient().getId());
        dto.setPatientName(entry.getPatient().getUser().getFullName());
        dto.setPatientAge(entry.getPatient().getUser().getAge());
        dto.setDepartmentId(entry.getDepartment().getId());
        dto.setDepartmentName(entry.getDepartment().getName());
        if (entry.getDoctor() != null) {
            dto.setDoctorId(entry.getDoctor().getId());
            dto.setDoctorName(entry.getDoctor().getUser().getFullName());
        }
        dto.setSeverityLevel(entry.getSeverityLevel());
        dto.setBasePriority(entry.getBasePriority());
        dto.setCalculatedPriority(priorityEngine.calculateEffectivePriority(entry));
        dto.setStatus(entry.getStatus());
        dto.setJoinedAt(entry.getJoinedAt());

        int pos = queueManager.getQueuePosition(entry.getDepartment().getId(), entry.getId());
        dto.setPositionInQueue(pos);
        dto.setEstimatedWaitMinutes(waitingTimeService.estimateWaitMinutes(pos, dto.getDoctorId(), dto.getDepartmentId()));

        return dto;
    }
}
