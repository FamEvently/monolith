package com.famevently.monolith.event;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
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

}
