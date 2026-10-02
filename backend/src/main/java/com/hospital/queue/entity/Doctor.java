package com.hospital.queue.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "doctors")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(nullable = false, length = 100)
    private String specialization;

    @Column(name = "room_number", length = 20)
    private String roomNumber;

    @Column(name = "is_available")
    private Boolean isAvailable = true;

    @Column(name = "max_daily_capacity")
    private Integer maxDailyCapacity = 40;

    public Doctor() {}

    public Doctor(User user, Department department, String specialization, String roomNumber, Integer maxDailyCapacity) {
        this.user = user;
        this.department = department;
        this.specialization = specialization;
        this.roomNumber = roomNumber;
        this.maxDailyCapacity = maxDailyCapacity != null ? maxDailyCapacity : 40;
        this.isAvailable = true;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public Boolean getIsAvailable() { return isAvailable; }
    public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }

    public Integer getMaxDailyCapacity() { return maxDailyCapacity; }
    public void setMaxDailyCapacity(Integer maxDailyCapacity) { this.maxDailyCapacity = maxDailyCapacity; }
}
