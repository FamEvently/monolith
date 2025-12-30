package com.famevently.monolith.event;

import com.famevently.monolith.attendance.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final UserAttendanceRepository userAttendanceRepository;
    private final EventAttendanceStatsRepository eventAttendanceStatsRepository;

    public EventService(EventRepository eventRepository, UserAttendanceRepository userAttendanceRepository, EventAttendanceStatsRepository eventAttendanceStatsRepository) {
        this.eventRepository = eventRepository;
        this.userAttendanceRepository = userAttendanceRepository;
        this.eventAttendanceStatsRepository = eventAttendanceStatsRepository;
    }

    final Event createEvent(final CreateEventRequest request)
    {
        final Optional<Event> savedEvent =  eventRepository.save(request);
        if (savedEvent.isEmpty()) {
            throw new IllegalStateException("Event not saved");
        }
        return savedEvent.get();
    }

    final List<Event> getEventsByCategory(final String categoryName)
    {
        return eventRepository.getEventsByCategory(categoryName);
    }

    final List<Event> getEventsForOrganizer(final Long userId)
    {
        return eventRepository.getEventsForUser(userId);
    }

    final List<Event> getEventsForLocation(final String location)
    {
        return eventRepository.getEventsForLocation(location);
    }

    final void deleteEvent(final Long eventId, final Long userId)
    {
        eventRepository.deleteEvent(eventId, userId);
    }

    final void deleteEventsForOrganizer(final Long userId)
    {
        //if organizer is restricted/banned
        eventRepository.deleteEventsForUser(userId);
    }

    final void markUserAttendance(final UserAttendanceRequest request, final Long userId)
    {
        // Get previous attendance state to calculate delta
        final var previousAttendance = userAttendanceRepository.getAttendance(userId, request.eventId());
        final boolean wasGoing = previousAttendance.map(UserAttendance::isGoing).orElse(false);
        final boolean hasResponded = previousAttendance.isPresent();

        // Upsert user attendance
        userAttendanceRepository.upsertAttendance(userId, request.eventId(), request.isGoing());

        // Calculate changes
        final AttendanceChange attendeesChange = hasResponded ? AttendanceChange.NONE : AttendanceChange.INCREMENT;
        final AttendanceChange goingChange = calculateGoingChange(wasGoing, request.isGoing());

        // Atomically adjust stats (creates record if doesn't exist)
        if (attendeesChange != AttendanceChange.NONE || goingChange != AttendanceChange.NONE) {
            eventAttendanceStatsRepository.adjustAttendanceCounts(request.eventId(), attendeesChange, goingChange);
        }
    }

    private AttendanceChange calculateGoingChange(final boolean wasGoing, final boolean isGoing) {
        if (wasGoing == isGoing) {
            return AttendanceChange.NONE;
        }
        return isGoing ? AttendanceChange.INCREMENT : AttendanceChange.DECREMENT;
    }

    final Optional<EventAttendanceStats> getEventAttendanceStats(final Long eventId)
    {
        return eventAttendanceStatsRepository.getEventStats(eventId);
    }

    final List<UserAttendance> getEventAttendance(final Long eventId)
    {
        return userAttendanceRepository.getEventAttendance(eventId);
    }

    final List<UserAttendance> getUserAttendance(final Long userId)
    {
        return userAttendanceRepository.getUserAttendance(userId);
    }

    final Optional<UserAttendance> getUserAttendanceForEvent(final Long userId, final Long eventId)
    {
        return userAttendanceRepository.getAttendance(userId, eventId);
    }

}
