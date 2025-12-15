package com.famevently.monolith.event;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public void createEvent(CreateEventRequest request) {
        Event event = EventMapper.toEntity(request);
        eventRepository.save(event);
    }

    public Event getEvent(Long eventId){
        Event event = eventRepository.getEvent(eventId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Event not found"
                ));

        return event;
    }
    public List<Event> getUserEvents(Long userId){
        return eventRepository.getUserEvents(userId);
    }

    public void updateEvent(Long eventId, CreateEventRequest request,Long userId){
        Event event = eventRepository.getEvent(eventId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Event not found"
                ));
        if (!event.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        event = EventMapper.toEntity(request);

        eventRepository.update(event);
    }

    public void deleteEvent(Long eventId, Long userId) {

        Event existingEvent = eventRepository.getEvent(eventId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Event not found"
                ));
        if (!existingEvent.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        eventRepository.delete(eventId);
    }
}
