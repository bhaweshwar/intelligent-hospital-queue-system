package com.hospital.queue.controller;

import com.hospital.queue.dto.ConsultationDTOs;
import com.hospital.queue.service.ConsultationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/consultations")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ConsultationController {

    private final ConsultationService consultationService;

    @Autowired
    public ConsultationController(ConsultationService consultationService) {
        this.consultationService = consultationService;
    }

    @PostMapping("/complete")
    public ResponseEntity<ConsultationDTOs.ConsultationResponse> completeConsultation(@Valid @RequestBody ConsultationDTOs.CompleteConsultationRequest request) {
        ConsultationDTOs.ConsultationResponse response = consultationService.completeConsultation(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/no-show/{queueEntryId}")
    public ResponseEntity<Void> markNoShow(@PathVariable Long queueEntryId) {
        consultationService.markNoShow(queueEntryId);
        return ResponseEntity.ok().build();
    }
}
