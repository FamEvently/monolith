package com.famevently.monolith.event;

public record UserAttendanceRequest(
        Long eventId,
        boolean isGoing
) {
}
