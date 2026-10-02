package com.hospital.queue.engine;

import com.hospital.queue.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PriorityQueueTest {

    private PriorityCalculationEngine priorityEngine;
    private HospitalQueueManager queueManager;

    @BeforeEach
    public void setUp() {
        priorityEngine = new PriorityCalculationEngine();
        queueManager = new HospitalQueueManager(priorityEngine);
    }

    @Test
    @DisplayName("Verify BCrypt password hashing for demo users")
    public void testPasswordHashingMatch() {
        org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
        String rawPassword = "password123";
        String encoded = encoder.encode(rawPassword);
        System.out.println("VALID BCRYPT HASH FOR 'password123': " + encoded);
        assertTrue(encoder.matches(rawPassword, encoded));
    }


    @Test
    @DisplayName("Should prioritize Emergency over Walk-in patient immediately")
    public void testEmergencyPriorityOverWalkIn() {
        Department dept = new Department("Cardiology", "Heart", "CARD", 15);
        dept.setId(1L);

        User u1 = new User("John WalkIn", "john@test.com", "pass", "123", Role.PATIENT, 30);
        Patient p1 = new Patient(u1, "MRN-1", "O+", "911");
        p1.setId(10L);
        QueueEntry entryWalkIn = new QueueEntry(p1, dept, null, null, "CARD-101", SeverityLevel.WALK_IN);
        entryWalkIn.setId(100L);

        User u2 = new User("Jane Emergency", "jane@test.com", "pass", "123", Role.PATIENT, 40);
        Patient p2 = new Patient(u2, "MRN-2", "A+", "911");
        p2.setId(20L);
        QueueEntry entryEmerg = new QueueEntry(p2, dept, null, null, "CARD-102", SeverityLevel.EMERGENCY);
        entryEmerg.setId(200L);

        // Enqueue walk-in first, then emergency
        queueManager.enqueue(entryWalkIn);
        queueManager.enqueue(entryEmerg);

        List<QueueEntry> sortedQueue = queueManager.getSortedQueueForDepartment(1L);

        assertEquals(2, sortedQueue.size());
        assertEquals("CARD-102", sortedQueue.get(0).getTicketNumber(), "Emergency patient should be at top of queue");
        assertEquals("CARD-101", sortedQueue.get(1).getTicketNumber());
    }

    @Test
    @DisplayName("Starvation Prevention: Long waiting walk-in patient should climb above newly arrived regular appointment")
    public void testStarvationPreventionMechanism() {
        Department dept = new Department("Pediatrics", "Children", "PED", 15);
        dept.setId(2L);

        // Patient 1: Walk-in who joined 60 minutes ago (Base 50 + 60 * 2.5 = 200 pts)
        User u1 = new User("Old Waiter", "old@test.com", "pass", "123", Role.PATIENT, 30);
        Patient p1 = new Patient(u1, "MRN-101", "O+", "911");
        p1.setId(11L);
        QueueEntry oldWalkIn = new QueueEntry(p1, dept, null, null, "PED-101", SeverityLevel.WALK_IN);
        oldWalkIn.setId(101L);
        oldWalkIn.setJoinedAt(LocalDateTime.now().minusMinutes(60));

        // Patient 2: Regular appointment just arrived 1 minute ago (Base 150 + 1 * 2.5 = 152.5 pts)
        User u2 = new User("New Appt", "new@test.com", "pass", "123", Role.PATIENT, 30);
        Patient p2 = new Patient(u2, "MRN-102", "B+", "911");
        p2.setId(22L);
        QueueEntry newAppt = new QueueEntry(p2, dept, null, null, "PED-102", SeverityLevel.REGULAR_APPOINTMENT);
        newAppt.setId(102L);
        newAppt.setJoinedAt(LocalDateTime.now().minusMinutes(1));

        queueManager.enqueue(oldWalkIn);
        queueManager.enqueue(newAppt);

        List<QueueEntry> sortedQueue = queueManager.getSortedQueueForDepartment(2L);

        assertEquals("PED-101", sortedQueue.get(0).getTicketNumber(),
                "Walk-in waiting 60 minutes (200 pts) should surpass fresh regular appointment (152.5 pts) due to Starvation Prevention!");
    }
}
