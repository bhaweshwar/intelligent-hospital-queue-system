package com.hospital.queue.service;

import com.hospital.queue.entity.Doctor;
import com.hospital.queue.entity.QueueStatus;
import com.hospital.queue.repository.DoctorRepository;
import com.hospital.queue.repository.QueueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class DoctorAssignmentService {

    private final DoctorRepository doctorRepository;
    private final QueueRepository queueRepository;

    @Autowired
    public DoctorAssignmentService(DoctorRepository doctorRepository, QueueRepository queueRepository) {
        this.doctorRepository = doctorRepository;
        this.queueRepository = queueRepository;
    }

    /**
     * Intelligently assigns an available doctor in the given department balancing current patient queue load.
     */
    public Optional<Doctor> assignBestAvailableDoctor(Long departmentId, String requiredSpecialization) {
        List<Doctor> availableDoctors = doctorRepository.findByDepartmentIdAndIsAvailableTrue(departmentId);
        if (availableDoctors.isEmpty()) {
            return Optional.empty();
        }

        // Filter by specialization if specified
        List<Doctor> candidateDoctors = availableDoctors;
        if (requiredSpecialization != null && !requiredSpecialization.trim().isEmpty()) {
            List<Doctor> specMatches = availableDoctors.stream()
                    .filter(d -> d.getSpecialization().toLowerCase().contains(requiredSpecialization.toLowerCase()))
                    .toList();
            if (!specMatches.isEmpty()) {
                candidateDoctors = specMatches;
            }
        }

        // Select doctor with the lowest waiting patient count
        return candidateDoctors.stream()
                .min(Comparator.comparingLong(doctor -> 
                        queueRepository.findByDoctorIdAndStatus(doctor.getId(), QueueStatus.WAITING).size()
                ));
    }
}
