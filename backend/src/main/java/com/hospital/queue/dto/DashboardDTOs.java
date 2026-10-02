package com.hospital.queue.dto;

import java.util.List;
import java.util.Map;

public class DashboardDTOs {

    public static class AdminAnalyticsResponse {
        private long totalPatientsToday;
        private long activeDoctors;
        private long waitingPatients;
        private long emergencyCasesToday;
        private double avgWaitingTimeMinutes;
        private long completedConsultationsToday;
        private long cancelledAppointmentsToday;
        private double noShowRatePercentage;
        private Map<String, Long> patientCountByDepartment;
        private Map<String, Double> avgWaitTimeByDepartment;

        public AdminAnalyticsResponse() {}

        public long getTotalPatientsToday() { return totalPatientsToday; }
        public void setTotalPatientsToday(long totalPatientsToday) { this.totalPatientsToday = totalPatientsToday; }
        public long getActiveDoctors() { return activeDoctors; }
        public void setActiveDoctors(long activeDoctors) { this.activeDoctors = activeDoctors; }
        public long getWaitingPatients() { return waitingPatients; }
        public void setWaitingPatients(long waitingPatients) { this.waitingPatients = waitingPatients; }
        public long getEmergencyCasesToday() { return emergencyCasesToday; }
        public void setEmergencyCasesToday(long emergencyCasesToday) { this.emergencyCasesToday = emergencyCasesToday; }
        public double getAvgWaitingTimeMinutes() { return avgWaitingTimeMinutes; }
        public void setAvgWaitingTimeMinutes(double avgWaitingTimeMinutes) { this.avgWaitingTimeMinutes = avgWaitingTimeMinutes; }
        public long getCompletedConsultationsToday() { return completedConsultationsToday; }
        public void setCompletedConsultationsToday(long completedConsultationsToday) { this.completedConsultationsToday = completedConsultationsToday; }
        public long getCancelledAppointmentsToday() { return cancelledAppointmentsToday; }
        public void setCancelledAppointmentsToday(long cancelledAppointmentsToday) { this.cancelledAppointmentsToday = cancelledAppointmentsToday; }
        public double getNoShowRatePercentage() { return noShowRatePercentage; }
        public void setNoShowRatePercentage(double noShowRatePercentage) { this.noShowRatePercentage = noShowRatePercentage; }
        public Map<String, Long> getPatientCountByDepartment() { return patientCountByDepartment; }
        public void setPatientCountByDepartment(Map<String, Long> patientCountByDepartment) { this.patientCountByDepartment = patientCountByDepartment; }
        public Map<String, Double> getAvgWaitTimeByDepartment() { return avgWaitTimeByDepartment; }
        public void setAvgWaitTimeByDepartment(Map<String, Double> avgWaitTimeByDepartment) { this.avgWaitTimeByDepartment = avgWaitTimeByDepartment; }
    }

    public static class PatientDashboardResponse {
        private AppointmentDTOs.AppointmentResponse upcomingAppointment;
        private QueueDTOs.QueuePositionResponse currentQueuePosition;
        private List<AppointmentDTOs.AppointmentResponse> appointmentHistory;

        public PatientDashboardResponse() {}

        public AppointmentDTOs.AppointmentResponse getUpcomingAppointment() { return upcomingAppointment; }
        public void setUpcomingAppointment(AppointmentDTOs.AppointmentResponse upcomingAppointment) { this.upcomingAppointment = upcomingAppointment; }
        public QueueDTOs.QueuePositionResponse getCurrentQueuePosition() { return currentQueuePosition; }
        public void setCurrentQueuePosition(QueueDTOs.QueuePositionResponse currentQueuePosition) { this.currentQueuePosition = currentQueuePosition; }
        public List<AppointmentDTOs.AppointmentResponse> getAppointmentHistory() { return appointmentHistory; }
        public void setAppointmentHistory(List<AppointmentDTOs.AppointmentResponse> appointmentHistory) { this.appointmentHistory = appointmentHistory; }
    }

    public static class DoctorDashboardResponse {
        private Long doctorId;
        private String doctorName;
        private Boolean isAvailable;
        private String roomNumber;
        private String departmentName;
        private QueueDTOs.QueueEntryDTO currentPatientInConsultation;
        private QueueDTOs.QueueEntryDTO nextPatientInQueue;
        private List<QueueDTOs.QueueEntryDTO> waitingQueue;
        private List<AppointmentDTOs.AppointmentResponse> todaysAppointments;
        private int completedTodayCount;

        public DoctorDashboardResponse() {}

        public Long getDoctorId() { return doctorId; }
        public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }
        public String getDoctorName() { return doctorName; }
        public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
        public Boolean getIsAvailable() { return isAvailable; }
        public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }
        public String getRoomNumber() { return roomNumber; }
        public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
        public String getDepartmentName() { return departmentName; }
        public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
        public QueueDTOs.QueueEntryDTO getCurrentPatientInConsultation() { return currentPatientInConsultation; }
        public void setCurrentPatientInConsultation(QueueDTOs.QueueEntryDTO currentPatientInConsultation) { this.currentPatientInConsultation = currentPatientInConsultation; }
        public QueueDTOs.QueueEntryDTO getNextPatientInQueue() { return nextPatientInQueue; }
        public void setNextPatientInQueue(QueueDTOs.QueueEntryDTO nextPatientInQueue) { this.nextPatientInQueue = nextPatientInQueue; }
        public List<QueueDTOs.QueueEntryDTO> getWaitingQueue() { return waitingQueue; }
        public void setWaitingQueue(List<QueueDTOs.QueueEntryDTO> waitingQueue) { this.waitingQueue = waitingQueue; }
        public List<AppointmentDTOs.AppointmentResponse> getTodaysAppointments() { return todaysAppointments; }
        public void setTodaysAppointments(List<AppointmentDTOs.AppointmentResponse> todaysAppointments) { this.todaysAppointments = todaysAppointments; }
        public int getCompletedTodayCount() { return completedTodayCount; }
        public void setCompletedTodayCount(int completedTodayCount) { this.completedTodayCount = completedTodayCount; }
    }
}
