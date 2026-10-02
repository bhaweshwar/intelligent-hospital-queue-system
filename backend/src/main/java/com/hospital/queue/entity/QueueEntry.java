package com.hospital.queue.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "queue_entries")
public class QueueEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @Column(name = "ticket_number", nullable = false, unique = true, length = 20)
    private String ticketNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity_level", nullable = false, length = 30)
    private SeverityLevel severityLevel;

    @Column(name = "base_priority", nullable = false)
    private Integer basePriority;

    @Column(name = "calculated_priority", nullable = false)
    private Double calculatedPriority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private QueueStatus status = QueueStatus.WAITING;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    @Column(name = "called_at")
    private LocalDateTime calledAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    public QueueEntry() {}

    public QueueEntry(Patient patient, Department department, Doctor doctor, Appointment appointment, String ticketNumber, SeverityLevel severityLevel) {
        this.patient = patient;
        this.department = department;
        this.doctor = doctor;
        this.appointment = appointment;
        this.ticketNumber = ticketNumber;
        this.severityLevel = severityLevel;
        this.basePriority = severityLevel.getBasePriority();
        this.calculatedPriority = (double) severityLevel.getBasePriority();
        this.status = QueueStatus.WAITING;
        this.joinedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (joinedAt == null) {
            joinedAt = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }

    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }

    public Appointment getAppointment() { return appointment; }
    public void setAppointment(Appointment appointment) { this.appointment = appointment; }

    public String getTicketNumber() { return ticketNumber; }
    public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }

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

    public LocalDateTime getCalledAt() { return calledAt; }
    public void setCalledAt(LocalDateTime calledAt) { this.calledAt = calledAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
