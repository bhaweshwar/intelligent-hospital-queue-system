package com.hospital.queue.repository;

import com.hospital.queue.entity.Appointment;
import com.hospital.queue.entity.AppointmentStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatientId(Long patientId);

    List<Appointment> findByDoctorIdAndAppointmentDate(Long doctorId, LocalDate appointmentDate);

    List<Appointment> findByDepartmentIdAndAppointmentDate(Long departmentId, LocalDate appointmentDate);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND a.appointmentDate = :appointmentDate AND a.timeSlot = :timeSlot AND a.status <> 'CANCELLED'")
    Optional<Appointment> findForUpdateSlot(@Param("doctorId") Long doctorId,
                                            @Param("appointmentDate") LocalDate appointmentDate,
                                            @Param("timeSlot") String timeSlot);

    boolean existsByDoctorIdAndAppointmentDateAndTimeSlotAndStatusNot(Long doctorId, LocalDate appointmentDate, String timeSlot, AppointmentStatus status);

    long countByStatus(AppointmentStatus status);
}
