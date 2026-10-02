package com.hospital.queue.service;

import com.hospital.queue.entity.Department;
import com.hospital.queue.repository.ConsultationRepository;
import com.hospital.queue.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WaitingTimeService {

    private final ConsultationRepository consultationRepository;
    private final DepartmentRepository departmentRepository;

    @Autowired
    public WaitingTimeService(ConsultationRepository consultationRepository, DepartmentRepository departmentRepository) {
        this.consultationRepository = consultationRepository;
        this.departmentRepository = departmentRepository;
    }

    /**
     * Estimates waiting time in minutes for a given position ahead in queue.
     * Estimated Wait = (Position Ahead) * (Avg Consultation Mins)
     */
    public int estimateWaitMinutes(int positionInQueue, Long doctorId, Long departmentId) {
        if (positionInQueue <= 1) {
            return 0; // Next in line or currently being called
        }

        int patientsAhead = positionInQueue - 1;
        double avgMinutes = 15.0; // Default fallback

        if (doctorId != null) {
            Double doctorAvg = consultationRepository.findAvgConsultationTimeByDoctorId(doctorId);
            if (doctorAvg != null && doctorAvg > 0) {
                avgMinutes = doctorAvg;
            }
        } else if (departmentId != null) {
            Double deptAvg = consultationRepository.findAvgConsultationTimeByDepartmentId(departmentId);
            if (deptAvg != null && deptAvg > 0) {
                avgMinutes = deptAvg;
            } else {
                Department dept = departmentRepository.findById(departmentId).orElse(null);
                if (dept != null && dept.getAvgConsultationMinutes() != null) {
                    avgMinutes = dept.getAvgConsultationMinutes();
                }
            }
        }

        return (int) Math.ceil(patientsAhead * avgMinutes);
    }
}
