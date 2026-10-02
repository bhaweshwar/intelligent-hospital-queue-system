package com.hospital.queue.engine;

import com.hospital.queue.entity.QueueEntry;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Priority Calculation Engine
 * 
 * Computes Effective Priority using Data Structure & Algorithm principles:
 * Effective Priority = Base Severity Score + Age Weight + (Waiting Time Mins * Starvation Weight)
 * 
 * Base Severities:
 * - EMERGENCY: 1000 pts
 * - URGENT: 500 pts
 * - HIGH_PRIORITY_APPOINTMENT: 250 pts
 * - REGULAR_APPOINTMENT: 150 pts
 * - WALK_IN: 50 pts
 * 
 * Starvation Prevention:
 * Starvation Weight = 2.5 pts per minute of waiting.
 * Age Bonus: Pediatric (<5 yrs) or Senior (>65 yrs) +30 pts.
 */
@Component
public class PriorityCalculationEngine {

    public static final double STARVATION_FACTOR_PER_MINUTE = 2.5;
    public static final double AGE_PRIORITY_BONUS = 30.0;

    public double calculateEffectivePriority(QueueEntry entry) {
        if (entry == null) return 0.0;

        // 1. Base Priority from Severity Level
        double priority = entry.getBasePriority() != null ? entry.getBasePriority() : entry.getSeverityLevel().getBasePriority();

        // 2. Age Category Weight
        if (entry.getPatient() != null && entry.getPatient().getUser() != null) {
            Integer age = entry.getPatient().getUser().getAge();
            if (age != null && (age <= 5 || age >= 65)) {
                priority += AGE_PRIORITY_BONUS;
            }
        }

        // 3. Waiting Time Starvation Prevention Increment
        LocalDateTime joinedAt = entry.getJoinedAt();
        if (joinedAt != null) {
            long minutesWaiting = Math.max(0, Duration.between(joinedAt, LocalDateTime.now()).toMinutes());
            priority += minutesWaiting * STARVATION_FACTOR_PER_MINUTE;
        }

        return priority;
    }

    /**
     * Overloaded method for custom timestamp calculation in unit tests
     */
    public double calculateEffectivePriorityAtTime(QueueEntry entry, LocalDateTime evaluationTime) {
        if (entry == null) return 0.0;

        double priority = entry.getBasePriority() != null ? entry.getBasePriority() : entry.getSeverityLevel().getBasePriority();

        if (entry.getPatient() != null && entry.getPatient().getUser() != null) {
            Integer age = entry.getPatient().getUser().getAge();
            if (age != null && (age <= 5 || age >= 65)) {
                priority += AGE_PRIORITY_BONUS;
            }
        }

        LocalDateTime joinedAt = entry.getJoinedAt();
        if (joinedAt != null && evaluationTime != null) {
            long minutesWaiting = Math.max(0, Duration.between(joinedAt, evaluationTime).toMinutes());
            priority += minutesWaiting * STARVATION_FACTOR_PER_MINUTE;
        }

        return priority;
    }
}
