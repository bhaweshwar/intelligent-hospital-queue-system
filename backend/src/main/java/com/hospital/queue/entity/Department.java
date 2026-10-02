package com.hospital.queue.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "departments")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, unique = true, length = 10)
    private String code;

    @Column(name = "avg_consultation_minutes")
    private Integer avgConsultationMinutes = 15;

    @Column(name = "is_active")
    private Boolean isActive = true;

    public Department() {}

    public Department(String name, String description, String code, Integer avgConsultationMinutes) {
        this.name = name;
        this.description = description;
        this.code = code;
        this.avgConsultationMinutes = avgConsultationMinutes != null ? avgConsultationMinutes : 15;
        this.isActive = true;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public Integer getAvgConsultationMinutes() { return avgConsultationMinutes; }
    public void setAvgConsultationMinutes(Integer avgConsultationMinutes) { this.avgConsultationMinutes = avgConsultationMinutes; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
