package com.famevently.monolith.attendance;

import java.time.OffsetDateTime;

public record EventAttendanceStats(
        long eventId,
        int attendeesCount,
        int goingCount,
        OffsetDateTime updatedAt) {
}

