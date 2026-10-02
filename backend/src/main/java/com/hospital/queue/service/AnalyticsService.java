package com.hospital.queue.service;

import com.hospital.queue.dto.DashboardDTOs;
import com.hospital.queue.entity.*;
import com.hospital.queue.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalyticsService {

    private final QueueRepository queueRepository;
    private final DoctorRepository doctorRepository;
    private final DepartmentRepository departmentRepository;
    private final AppointmentRepository appointmentRepository;
    private final ConsultationRepository consultationRepository;

    @Autowired
    public AnalyticsService(QueueRepository queueRepository, DoctorRepository doctorRepository,
                             DepartmentRepository departmentRepository, AppointmentRepository appointmentRepository,
                             ConsultationRepository consultationRepository) {
        this.queueRepository = queueRepository;
        this.doctorRepository = doctorRepository;
        this.departmentRepository = departmentRepository;
        this.appointmentRepository = appointmentRepository;
        this.consultationRepository = consultationRepository;
    }

    public DashboardDTOs.AdminAnalyticsResponse getAdminAnalytics() {
        DashboardDTOs.AdminAnalyticsResponse metrics = new DashboardDTOs.AdminAnalyticsResponse();

        long totalQueueEntries = queueRepository.count();
        long waitingCount = queueRepository.countByStatus(QueueStatus.WAITING);
        long activeDocs = doctorRepository.findAll().stream().filter(Doctor::getIsAvailable).count();
        long completedCount = queueRepository.countByStatus(QueueStatus.COMPLETED);
        long cancelledCount = appointmentRepository.countByStatus(AppointmentStatus.CANCELLED);
        long noShowCount = queueRepository.countByStatus(QueueStatus.NO_SHOW);
        long emergencyCount = queueRepository.findAll().stream()
                .filter(e -> e.getSeverityLevel() == SeverityLevel.EMERGENCY)
                .count();

        double noShowRate = totalQueueEntries > 0 ? ((double) noShowCount / totalQueueEntries) * 100.0 : 0.0;

        List<Department> departments = departmentRepository.findAll();
        Map<String, Long> countByDept = new HashMap<>();
        Map<String, Double> avgWaitByDept = new HashMap<>();

        for (Department dept : departments) {
            long deptWaiting = queueRepository.findByDepartmentIdAndStatus(dept.getId(), QueueStatus.WAITING).size();
            countByDept.put(dept.getName(), deptWaiting);

            Double avgConsultation = consultationRepository.findAvgConsultationTimeByDepartmentId(dept.getId());
            double waitMins = deptWaiting * (avgConsultation != null ? avgConsultation : dept.getAvgConsultationMinutes());
            avgWaitByDept.put(dept.getName(), waitMins);
        }

        double overallAvgWait = avgWaitByDept.values().stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

        metrics.setTotalPatientsToday(totalQueueEntries);
        metrics.setActiveDoctors(activeDocs);
        metrics.setWaitingPatients(waitingCount);
        metrics.setEmergencyCasesToday(emergencyCount);
        metrics.setAvgWaitingTimeMinutes(overallAvgWait);
        metrics.setCompletedConsultationsToday(completedCount);
        metrics.setCancelledAppointmentsToday(cancelledCount);
        metrics.setNoShowRatePercentage(Math.round(noShowRate * 10.0) / 10.0);
        metrics.setPatientCountByDepartment(countByDept);
        metrics.setAvgWaitTimeByDepartment(avgWaitByDept);

        return metrics;
    }
}
