package com.famevently.monolith.attendance;

public class CannotAttendOwnEventException extends RuntimeException {
    public CannotAttendOwnEventException() {
        super("Organizers cannot mark attendance on their own events");
    }
}
