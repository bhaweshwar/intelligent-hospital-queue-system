package com.hospital.queue.entity;

public enum SeverityLevel {
    EMERGENCY(1000),
    URGENT(500),
    HIGH_PRIORITY_APPOINTMENT(250),
    REGULAR_APPOINTMENT(150),
    WALK_IN(50);

    private final int basePriority;

    SeverityLevel(int basePriority) {
        this.basePriority = basePriority;
    }

    public int getBasePriority() {
        return basePriority;
    }
}
