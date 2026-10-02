package com.hospital.queue.dto;

import com.hospital.queue.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AuthDTOs {

    public static class LoginRequest {
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email address format")
        private String email;

        @NotBlank(message = "Password is required")
        private String password;

        public LoginRequest() {}
        public LoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class RegisterRequest {
        @NotBlank(message = "Full name is required")
        private String fullName;

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        private String email;

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        private String password;

        private String phoneNumber;

        @NotNull(message = "Role is required")
        private Role role;

        private Integer age;

        // Doctor specific fields (optional if role == DOCTOR)
        private Long departmentId;
        private String specialization;
        private String roomNumber;

        // Patient specific fields (optional if role == PATIENT)
        private String bloodGroup;
        private String emergencyContact;

        public RegisterRequest() {}

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        public Role getRole() { return role; }
        public void setRole(Role role) { this.role = role; }
        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }

        public Long getDepartmentId() { return departmentId; }
        public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }
        public String getSpecialization() { return specialization; }
        public void setSpecialization(String specialization) { this.specialization = specialization; }
        public String getRoomNumber() { return roomNumber; }
        public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

        public String getBloodGroup() { return bloodGroup; }
        public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }
        public String getEmergencyContact() { return emergencyContact; }
        public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }
    }

    public static class AuthResponse {
        private String token;
        private String tokenType = "Bearer";
        private Long id;
        private String email;
        private String fullName;
        private Role role;

        public AuthResponse(String token, Long id, String email, String fullName, Role role) {
            this.token = token;
            this.id = id;
            this.email = email;
            this.fullName = fullName;
            this.role = role;
        }

        public String getToken() { return token; }
        public String getTokenType() { return tokenType; }
        public Long getId() { return id; }
        public String getEmail() { return email; }
        public String getFullName() { return fullName; }
        public Role getRole() { return role; }
    }
}
