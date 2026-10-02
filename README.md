# Intelligent Hospital Queue & Appointment Optimization System

[![Java](https://img.shields.io/badge/Java-21%2F25-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18-blue.svg)](https://react.dev/)
[![Build Status](https://img.shields.io/badge/Build-Passing-success.svg)]()
[![Tests](https://img.shields.io/badge/JUnit5-100%25%20Passed-success.svg)]()

> A production-style hospital queue and appointment management platform that solves long patient waiting times, inefficient patient allocation, appointment conflicts, overloaded doctors/departments, and poor visibility into hospital queues using **Data Structures & Algorithms (DSA)**.

---

## 🌟 Key Technical Highlights

- **DSA-Driven Scheduling Engine**: Real-time Java `PriorityQueue` (Binary Max-Heap) with $O(\log N)$ enqueue/dequeue operations and $O(1)$ `HashMap` indexing.
- **Starvation Prevention Mechanism**: Mathematical priority acceleration formula ($\alpha = 2.5 \text{ pts/min}$) ensuring low-priority walk-in patients are never blocked indefinitely by newer arrivals.
- **Race-Condition & Double-Booking Protection**: Concurrency control via `@Lock(LockModeType.PESSIMISTIC_WRITE)` and relational database unique constraints `uk_doctor_schedule UNIQUE(doctor_id, appointment_date, time_slot)`.
- **Dynamic Doctor Assignment**: Intelligent matching of available doctors based on specialization, department capacity, and live queue load balancing.
- **Data-Driven Wait Estimation**: Dynamic waiting time estimates calculated from doctor-specific rolling average consultation metrics.
- **Role-Based Security & Dashboards**: Full Spring Security + JWT authorization supporting **Patient**, **Doctor**, **Receptionist**, and **Admin** personas.

---

## 🏗️ Technology Stack

### Backend
- **Core**: Java 21 / 25, Spring Boot 3.2.4, Spring Web, Spring Data JPA, Hibernate
- **Security**: Spring Security, JWT (jjwt 0.12.5), BCrypt
- **Concurrency & DSA**: Java `PriorityQueue`, `HashMap`, `ReentrantReadWriteLock`, Pessimistic Locking
- **Build & Tools**: Maven, Bean Validation

### Frontend
- **Framework**: React 18, Vite 5, React Router DOM 6, Axios
- **Design System**: Vanilla CSS with glassmorphism clinical theme, dark mode, custom status badges, responsive layout

### Database & Testing
- **Database**: PostgreSQL (Production) + Embedded H2 PostgreSQL-compatibility mode (Development)
- **Testing**: JUnit 5, Mockito, Spring Boot Test

---

## 📊 Core DSA Queue Algorithm Formula

$$\text{Effective Priority} = P_{\text{severity}} + P_{\text{age}} + (T_{\text{waiting}} \times \alpha)$$

| Triage Level | Base Severity Score ($P_{\text{severity}}$) |
| :--- | :--- |
| **`EMERGENCY`** | **1000 pts** |
| **`URGENT`** | **500 pts** |
| **`HIGH_PRIORITY_APPOINTMENT`** | **250 pts** |
| **`REGULAR_APPOINTMENT`** | **150 pts** |
| **`WALK_IN`** | **50 pts** |

- **Pediatric (< 5 yrs) or Senior (> 65 yrs) Bonus**: $+30$ pts.
- **Starvation Prevention Acceleration ($\alpha$)**: $+2.5$ pts/min.

---

## 🚀 How to Run Locally

### Prerequisites
- **Java 21 / 25 JDK** installed
- **Node.js 18+** installed

### 1. Run Backend Server
```bash
cd backend
mvn spring-boot:run
```
*The Spring Boot server starts on `http://localhost:8080` with pre-seeded data.*

### 2. Run Frontend Web App
```bash
cd frontend
npm install
npm run dev
```
*The React app starts on `http://localhost:3000` with API proxying to port 8080.*

---

## 🧪 Running Automated Unit & Concurrency Tests

```bash
cd backend
mvn test
```

### Verified Test Suite Results
- ✅ `PriorityQueueTest.testEmergencyPriorityOverWalkIn`: Verifies Max-Heap emergency preemption ($O(\log N)$).
- ✅ `PriorityQueueTest.testStarvationPreventionMechanism`: Proves walk-in waiting 60 minutes (200 pts) surpasses fresh regular appointment (152.5 pts).
- ✅ `AppointmentConcurrencyTest.testDoubleBookingConcurrencyPrevention`: Validates slot lock conflict prevention.
- ✅ `AppointmentConcurrencyTest.testDatabaseConstraintConcurrencyFallback`: Validates DB constraint violation handling.

---

## 👥 Demo Logins

| Role | Email | Password |
| :--- | :--- | :--- |
| **Admin** | `admin@hospital.com` | `password123` |
| **Receptionist** | `receptionist@hospital.com` | `password123` |
| **Doctor** | `sarah.jenkins@hospital.com` | `password123` |
| **Patient** | `john.doe@patient.com` | `password123` |

---

## 📁 Project Structure

```
.
├── backend/                  # Spring Boot 3 Java Backend
│   ├── src/main/java/        # Entities, Repositories, Services, Controllers, DSA Engine
│   ├── src/test/java/        # JUnit 5 & Mockito Unit + Concurrency Tests
│   └── pom.xml               # Maven Dependencies
├── frontend/                 # React 18 + Vite Frontend
│   ├── src/components/       # Navbar, Sidebar, StatCard, QueueVisualizer
│   ├── src/pages/            # Patient, Doctor, Receptionist, Admin Dashboards
│   └── package.json
├── database/                 # Schema & Seed SQL Scripts
├── docs/                     # Architecture, DSA & API Documentation
└── README.md
```
