package com.famevently.monolith.attendance;

public enum AttendanceChange {
    INCREMENT(1),
    DECREMENT(-1),
    NONE(0);

    private final int delta;

    AttendanceChange(final int delta) {
        this.delta = delta;
    }

    public int getDelta() {
        return delta;
    }
}

