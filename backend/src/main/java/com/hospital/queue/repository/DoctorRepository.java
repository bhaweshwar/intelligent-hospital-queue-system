package com.hospital.queue.repository;

import com.hospital.queue.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByUserId(Long userId);
    List<Doctor> findByDepartmentIdAndIsAvailableTrue(Long departmentId);
    List<Doctor> findByDepartmentId(Long departmentId);
}
