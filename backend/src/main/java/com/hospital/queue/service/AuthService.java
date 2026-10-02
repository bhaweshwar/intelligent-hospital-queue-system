package com.hospital.queue.service;

import com.hospital.queue.config.JwtUtils;
import com.hospital.queue.config.UserDetailsImpl;
import com.hospital.queue.dto.AuthDTOs;
import com.hospital.queue.entity.*;
import com.hospital.queue.exception.BadRequestException;
import com.hospital.queue.exception.ResourceNotFoundException;
import com.hospital.queue.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Autowired
    public AuthService(UserRepository userRepository, DoctorRepository doctorRepository,
                       PatientRepository patientRepository, DepartmentRepository departmentRepository,
                       PasswordEncoder encoder, AuthenticationManager authenticationManager,
                       JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.departmentRepository = departmentRepository;
        this.encoder = encoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    public AuthDTOs.AuthResponse authenticateUser(AuthDTOs.LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return new AuthDTOs.AuthResponse(
                jwt,
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole()
        );
    }

    @Transactional
    public AuthDTOs.AuthResponse registerUser(AuthDTOs.RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BadRequestException("Email is already registered!");
        }

        User user = new User(
                registerRequest.getFullName(),
                registerRequest.getEmail(),
                encoder.encode(registerRequest.getPassword()),
                registerRequest.getPhoneNumber(),
                registerRequest.getRole(),
                registerRequest.getAge()
        );

        user = userRepository.save(user);

        // Role-specific setup
        if (user.getRole() == Role.PATIENT) {
            String mrn = "MRN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            Patient patient = new Patient(user, mrn, registerRequest.getBloodGroup(), registerRequest.getEmergencyContact());
            patientRepository.save(patient);
        } else if (user.getRole() == Role.DOCTOR) {
            Department dept = departmentRepository.findById(registerRequest.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + registerRequest.getDepartmentId()));
            String spec = registerRequest.getSpecialization() != null ? registerRequest.getSpecialization() : "General Practice";
            String room = registerRequest.getRoomNumber() != null ? registerRequest.getRoomNumber() : "Room 101";
            Doctor doctor = new Doctor(user, dept, spec, room, 40);
            doctorRepository.save(doctor);
        }

        // Auto-login upon registration
        return authenticateUser(new AuthDTOs.LoginRequest(registerRequest.getEmail(), registerRequest.getPassword()));
    }
}
