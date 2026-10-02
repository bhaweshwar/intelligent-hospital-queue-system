package com.hospital.queue.repository;

import com.hospital.queue.entity.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, Long> {

    List<Consultation> findByDoctorId(Long doctorId);

    List<Consultation> findByPatientId(Long patientId);

    @Query("SELECT AVG(c.durationMinutes) FROM Consultation c WHERE c.doctor.id = :doctorId")
    Double findAvgConsultationTimeByDoctorId(@Param("doctorId") Long doctorId);

    @Query("SELECT AVG(c.durationMinutes) FROM Consultation c WHERE c.doctor.department.id = :departmentId")
    Double findAvgConsultationTimeByDepartmentId(@Param("departmentId") Long departmentId);
}
