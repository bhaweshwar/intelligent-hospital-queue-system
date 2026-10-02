package com.hospital.queue.repository;

import com.hospital.queue.entity.QueueEntry;
import com.hospital.queue.entity.QueueStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QueueRepository extends JpaRepository<QueueEntry, Long> {

    List<QueueEntry> findByDepartmentIdAndStatus(Long departmentId, QueueStatus status);

    List<QueueEntry> findByDoctorIdAndStatus(Long doctorId, QueueStatus status);

    Optional<QueueEntry> findByPatientIdAndStatus(Long patientId, QueueStatus status);

    Optional<QueueEntry> findByTicketNumber(String ticketNumber);

    List<QueueEntry> findByPatientId(Long patientId);

    long countByStatus(QueueStatus status);
}
