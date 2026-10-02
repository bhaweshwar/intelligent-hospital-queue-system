package com.hospital.queue.controller;

import com.hospital.queue.dto.AuthDTOs;
import com.hospital.queue.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDTOs.AuthResponse> authenticateUser(@Valid @RequestBody AuthDTOs.LoginRequest loginRequest) {
        AuthDTOs.AuthResponse response = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthDTOs.AuthResponse> registerUser(@Valid @RequestBody AuthDTOs.RegisterRequest registerRequest) {
        AuthDTOs.AuthResponse response = authService.registerUser(registerRequest);
        return ResponseEntity.ok(response);
    }
}
