package com.famevently.monolith.event;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/events")
public class EventController {
    private final EventService eventService;

    public EventController(final EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping()
    public Event createEvent(@RequestBody final CreateEventRequest request)
    {
        return eventService.createEvent(request);
    }

    @GetMapping("/users/{userId}")
    public List<Event> getEventsForOrganizer(@PathVariable final Long userId)
    {
        return eventService.getEventsForOrganizer(userId);
    }

    @GetMapping("/by-category")
    public List<Event> getEventsByCategory(@RequestParam final String categoryName)
    {
        return eventService.getEventsByCategory(categoryName);
    }

    @GetMapping("/by-location")
    public List<Event> getEventsForLocation(@RequestParam final String location)
    {
        return eventService.getEventsForLocation(location);
    }

    @DeleteMapping("/{eventId}/users/{userId}")
    public void createEvent(@PathVariable final Long eventId, @PathVariable final Long userId)
    {
        eventService.deleteEvent(eventId, userId);
    }

    @DeleteMapping("/users/{userId}")
    public void createEvent(@PathVariable final Long userId)
    {
        eventService.deleteEventsForOrganizer(userId);
    }
}
