package com.famevently.monolith.event;

public class EventNotFoundException extends RuntimeException {
    public static final String EVENT_NOT_FOUND = "EventNotFound";
    private final Long eventId;

    public EventNotFoundException(final Long eventId) {
        super(EVENT_NOT_FOUND);
        this.eventId = eventId;
    }

    public Long getEventId() {
        return eventId;
    }
}
