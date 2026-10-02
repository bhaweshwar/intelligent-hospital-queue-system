package com.hospital.queue.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments", uniqueConstraints = {
    @UniqueConstraint(name = "uk_doctor_schedule", columnNames = {"doctor_id", "appointment_date", "time_slot"})
})
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;

    @Column(name = "time_slot", nullable = false, length = 20)
    private String timeSlot;

    @Enumerated(EnumType.STRING)
    @Column(name = "appointment_type", nullable = false, length = 30)
    private AppointmentType appointmentType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AppointmentStatus status = AppointmentStatus.SCHEDULED;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Appointment() {}

    public Appointment(Patient patient, Doctor doctor, Department department, LocalDate appointmentDate, String timeSlot, AppointmentType appointmentType, String reason) {
        this.patient = patient;
        this.doctor = doctor;
        this.department = department;
        this.appointmentDate = appointmentDate;
        this.timeSlot = timeSlot;
        this.appointmentType = appointmentType;
        this.reason = reason;
        this.status = AppointmentStatus.SCHEDULED;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }

    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }

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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
