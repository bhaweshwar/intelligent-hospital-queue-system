package com.hospital.queue.engine;

import com.hospital.queue.entity.QueueEntry;
import com.hospital.queue.entity.QueueStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * Hospital Queue Manager (DSA Core)
 * 
 * Data Structures Used:
 * 1. PriorityQueue<QueueEntry> (Max-Heap) - Keeps highest priority patient at head.
 *    Time Complexity: Enqueue O(log N), Dequeue O(log N), Peek O(1).
 * 2. HashMap<Long, QueueEntry> - Fast patient/ticket O(1) lookup.
 * 3. ReentrantReadWriteLock - Thread-safe concurrency control for parallel admissions.
 */
@Component
public class HospitalQueueManager {

    private final PriorityCalculationEngine priorityEngine;
    
    // Key: Department ID -> PriorityQueue of QueueEntries
    private final Map<Long, PriorityQueue<QueueEntry>> departmentHeaps = new ConcurrentHashMap<>();
    
    // Key: Entry ID -> QueueEntry for O(1) direct lookup
    private final Map<Long, QueueEntry> entryIndexMap = new ConcurrentHashMap<>();

    // Fine-grained lock per department
    private final Map<Long, ReentrantReadWriteLock> departmentLocks = new ConcurrentHashMap<>();

    @Autowired
    public HospitalQueueManager(PriorityCalculationEngine priorityEngine) {
        this.priorityEngine = priorityEngine;
    }

    private ReentrantReadWriteLock getLock(Long departmentId) {
        return departmentLocks.computeIfAbsent(departmentId, k -> new ReentrantReadWriteLock());
    }

    /**
     * Comparator for Max-Heap priority order.
     * Ties are broken by earlier arrival time (FIFO fallback).
     */
    private Comparator<QueueEntry> createQueueComparator() {
        return (e1, e2) -> {
            double p1 = priorityEngine.calculateEffectivePriority(e1);
            double p2 = priorityEngine.calculateEffectivePriority(e2);
            int comp = Double.compare(p2, p1); // Descending priority
            if (comp != 0) {
                return comp;
            }
            // Tie breaker: Earlier joinedAt comes first
            if (e1.getJoinedAt() != null && e2.getJoinedAt() != null) {
                return e1.getJoinedAt().compareTo(e2.getJoinedAt());
            }
            return 0;
        };
    }

    /**
     * Adds or updates a patient entry in the department heap. O(log N) complexity.
     */
    public void enqueue(QueueEntry entry) {
        if (entry == null || entry.getDepartment() == null) return;
        Long departmentId = entry.getDepartment().getId();
        ReentrantReadWriteLock lock = getLock(departmentId);
        lock.writeLock().lock();
        try {
            PriorityQueue<QueueEntry> heap = departmentHeaps.computeIfAbsent(
                departmentId, k -> new PriorityQueue<>(createQueueComparator())
            );
            
            // Remove existing if present (re-indexing/updating)
            heap.removeIf(e -> e.getId().equals(entry.getId()));
            
            // Recalculate priority before pushing
            double calculated = priorityEngine.calculateEffectivePriority(entry);
            entry.setCalculatedPriority(calculated);
            
            heap.offer(entry);
            entryIndexMap.put(entry.getId(), entry);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Retrieves and calls the highest priority next patient from the queue. O(log N) complexity.
     */
    public Optional<QueueEntry> dequeueNextPatient(Long departmentId, Long doctorId) {
        ReentrantReadWriteLock lock = getLock(departmentId);
        lock.writeLock().lock();
        try {
            PriorityQueue<QueueEntry> heap = departmentHeaps.get(departmentId);
            if (heap == null || heap.isEmpty()) {
                return Optional.empty();
            }

            // Filter optional doctor preference or take top
            QueueEntry selectedEntry = null;
            List<QueueEntry> tempBuffer = new ArrayList<>();

            while (!heap.isEmpty()) {
                QueueEntry candidate = heap.poll();
                if (candidate.getStatus() != QueueStatus.WAITING) {
                    continue; // Skip non-waiting entries
                }
                
                // If entry has a doctor assignment preference and matches doctorId, or doctorId is unassigned/matched
                if (doctorId == null || candidate.getDoctor() == null || candidate.getDoctor().getId().equals(doctorId)) {
                    selectedEntry = candidate;
                    break;
                } else {
                    tempBuffer.add(candidate);
                }
            }

            // Put back non-selected candidates
            heap.addAll(tempBuffer);

            if (selectedEntry != null) {
                selectedEntry.setStatus(QueueStatus.IN_CONSULTATION);
                selectedEntry.setCalledAt(LocalDateTime.now());
                entryIndexMap.put(selectedEntry.getId(), selectedEntry);
                return Optional.of(selectedEntry);
            }

            return Optional.empty();
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Re-sorts/refreshes department heap to apply starvation weights over time.
     */
    public List<QueueEntry> getSortedQueueForDepartment(Long departmentId) {
        ReentrantReadWriteLock lock = getLock(departmentId);
        lock.readLock().lock();
        try {
            PriorityQueue<QueueEntry> heap = departmentHeaps.get(departmentId);
            if (heap == null || heap.isEmpty()) {
                return Collections.emptyList();
            }

            return heap.stream()
                .filter(e -> e.getStatus() == QueueStatus.WAITING)
                .map(e -> {
                    e.setCalculatedPriority(priorityEngine.calculateEffectivePriority(e));
                    return e;
                })
                .sorted(createQueueComparator())
                .collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Calculates current 1-indexed position of patient in waiting queue.
     */
    public int getQueuePosition(Long departmentId, Long entryId) {
        List<QueueEntry> sortedQueue = getSortedQueueForDepartment(departmentId);
        for (int i = 0; i < sortedQueue.size(); i++) {
            if (sortedQueue.get(i).getId().equals(entryId)) {
                return i + 1; // 1-indexed
            }
        }
        return -1; // Not in waiting queue
    }

    /**
     * Removes/Cancels an entry from queue.
     */
    public void removeFromQueue(Long departmentId, Long entryId) {
        ReentrantReadWriteLock lock = getLock(departmentId);
        lock.writeLock().lock();
        try {
            PriorityQueue<QueueEntry> heap = departmentHeaps.get(departmentId);
            if (heap != null) {
                heap.removeIf(e -> e.getId().equals(entryId));
            }
            entryIndexMap.remove(entryId);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public QueueEntry getEntryById(Long entryId) {
        return entryIndexMap.get(entryId);
    }
}
