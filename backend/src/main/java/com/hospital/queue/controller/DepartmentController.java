package com.hospital.queue.controller;

import com.hospital.queue.entity.Department;
import com.hospital.queue.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DepartmentController {

    private final DepartmentRepository departmentRepository;

    @Autowired
    public DepartmentController(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @GetMapping
    public ResponseEntity<List<Department>> getAllActiveDepartments() {
        return ResponseEntity.ok(departmentRepository.findByIsActiveTrue());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Department> getDepartmentById(@PathVariable Long id) {
        return ResponseEntity.ok(
            departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with ID: " + id))
        );
    }
}
