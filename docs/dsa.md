# Data Structures & Algorithms (DSA) Specifications

## 1. Overview of Data Structures Used

| Data Structure | Purpose | Primary Operation | Time Complexity | Space Complexity |
| :--- | :--- | :--- | :--- | :--- |
| **`PriorityQueue<QueueEntry>`** | Binary Max-Heap keeping highest priority patient at head | `offer()` / `poll()` | $O(\log N)$ | $O(N)$ |
| **`HashMap<Long, QueueEntry>`** | Direct lookup of patient queue tickets | `get(entryId)` | $O(1)$ | $O(N)$ |
| **`ReentrantReadWriteLock`** | Thread-safe concurrent access to department heaps | `lock()` / `unlock()` | $O(1)$ | $O(1)$ |
| **Custom `Comparator`** | Priority order evaluation with FIFO tie-breaker | `compare(e1, e2)` | $O(1)$ | $O(1)$ |

---

## 2. Dynamic Priority Score Formula

$$\text{Effective Priority} = P_{\text{severity}} + P_{\text{age}} + (T_{\text{waiting}} \times \alpha)$$

### Severity Weights ($P_{\text{severity}}$):
- **`EMERGENCY`**: $1000$ points
- **`URGENT`**: $500$ points
- **`HIGH_PRIORITY_APPOINTMENT`**: $250$ points
- **`REGULAR_APPOINTMENT`**: $150$ points
- **`WALK_IN`**: $50$ points

### Age Weight ($P_{\text{age}}$):
- **Infant ($\le 5$ years) or Senior ($\ge 65$ years)**: $+30$ points.

### Starvation Prevention Factor ($\alpha$):
- $\alpha = 2.5$ points per minute spent in `WAITING` status.

---

## 3. Mathematical Proof of Starvation Prevention

Consider a Walk-In patient ($P_{\text{severity}} = 50$) and a newly arriving Regular Appointment patient ($P_{\text{severity}} = 150$).

Without starvation prevention, the walk-in patient would stay behind every newly arriving appointment indefinitely.

With $\alpha = 2.5$ points/minute:
$$\text{Priority}_{\text{walk-in}}(t) = 50 + (t \times 2.5)$$
$$\text{Priority}_{\text{appt}}(0) = 150$$

Setting them equal:
$$50 + 2.5t = 150 \implies 2.5t = 100 \implies t = 40 \text{ minutes}$$

**Conclusion**: After 40 minutes of waiting, a normal walk-in patient's effective priority score reaches $150$ points, matching a fresh regular appointment. At minute 41 ($152.5$ pts), the walk-in patient surpasses newly arriving appointments in the Max-Heap, preventing starvation.

---

## 4. Complexity Analysis

- **Enqueue (Patient Arrival / Check-in)**: $O(\log N)$ heap push + $O(1)$ map insert.
- **Dequeue Next Patient (Doctor Call)**: $O(\log N)$ heap poll + status update.
- **Queue Position Lookup**: $O(N)$ heap stream scan or $O(N \log N)$ full re-sort snapshot.
- **Space Complexity**: $O(N)$ per department queue where $N$ is active waiting patients.
