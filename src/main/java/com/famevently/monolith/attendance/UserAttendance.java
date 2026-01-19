package com.famevently.monolith.attendance;

import java.time.OffsetDateTime;

public record UserAttendance(
        long userId,
        long eventId,
        boolean isGoing,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {
}

