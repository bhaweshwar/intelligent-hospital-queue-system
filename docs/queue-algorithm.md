# Intelligent Queue & Scheduling Engine Documentation

## Core Problem Addressed
Traditional hospital queues rely on static First-In-First-Out (FIFO) or manual paper triage. This causes critical bottlenecks:
1. Emergency patients wait behind non-urgent walk-ins.
2. Low-priority patients starve indefinitely when high-priority patients keep arriving.
3. Patient waiting times are opaque and uncalculated.

## Solution Architecture
Our Intelligent Queue Engine uses a **dynamic Priority-Based Scheduling Engine** backed by a **Java Binary Max-Heap (`PriorityQueue`)** and **HashMap index**.

```
[Patient Arrival] ──► [Priority Calculation Engine] ──► [PriorityQueue Max-Heap]
                                                              │
                                                              ▼
[Doctor Ready] ◄──── [Dequeue Highest Priority] ◄───── [Starvation Acceleration]
```

---

## Starvation Prevention Breakdown
The starvation prevention mechanism increases a patient's effective priority score continuously as time elapses:

- Minute 0 (Walk-In): Priority = 50 pts
- Minute 20: Priority = 50 + (20 * 2.5) = 100 pts
- Minute 40: Priority = 50 + (40 * 2.5) = 150 pts (Surpasses fresh regular appointments)
- Minute 180: Priority = 50 + (180 * 2.5) = 500 pts (Surpasses fresh urgent patients)

This guarantees deterministic, explainable, and testable fairness.
