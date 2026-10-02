package com.hospital.queue.controller;

import com.hospital.queue.dto.QueueDTOs;
import com.hospital.queue.service.QueueService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/queues")
@CrossOrigin(origins = "*", maxAge = 3600)
public class QueueController {

    private final QueueService queueService;

    @Autowired
    public QueueController(QueueService queueService) {
        this.queueService = queueService;
    }

    @PostMapping("/join")
    public ResponseEntity<QueueDTOs.QueueEntryDTO> joinQueue(@Valid @RequestBody QueueDTOs.JoinQueueRequest request) {
        QueueDTOs.QueueEntryDTO response = queueService.joinQueue(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<QueueDTOs.QueueEntryDTO>> getDepartmentQueue(@PathVariable Long departmentId) {
        return ResponseEntity.ok(queueService.getDepartmentQueue(departmentId));
    }

    @GetMapping("/position/patient/{patientId}")
    public ResponseEntity<QueueDTOs.QueuePositionResponse> getPatientQueuePosition(@PathVariable Long patientId) {
        return ResponseEntity.ok(queueService.getPatientQueuePosition(patientId));
    }

    @PostMapping("/next")
    public ResponseEntity<QueueDTOs.QueueEntryDTO> callNextPatient(@RequestParam Long departmentId, @RequestParam(required = false) Long doctorId) {
        Optional<QueueDTOs.QueueEntryDTO> nextPatient = queueService.callNextPatient(departmentId, doctorId);
        return nextPatient.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }
}
