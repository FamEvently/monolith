package com.famevently.monolith.event;

import com.famevently.monolith.attendance.EventAttendanceStats;
import com.famevently.monolith.attendance.UserAttendance;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/events")
public class EventController {
    private final EventService eventService;

    public EventController(final EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/users/{userId}")
    public Event createEvent(@PathVariable final Long userId, @RequestBody final CreateEventRequest request)
    {
        return eventService.createEvent(request, userId);
    }

    @GetMapping("/users/{userId}")
    public List<Event> getEventsForOrganizer(@PathVariable final Long userId)
    {
        return eventService.getEventsForOrganizer(userId);
    }

    @GetMapping("/by-category")
    public List<Event> getEventsByCategory(@RequestParam final EventCategory categoryName)
    {
        return eventService.getEventsByCategory(categoryName);
    }

    @GetMapping("/by-location")
    public List<Event> getEventsForLocation(@RequestParam final String location)
    {
        return eventService.getEventsForLocation(location);
    }

    @DeleteMapping("/{eventId}/users/{userId}")
    public void deleteEvent(@PathVariable final Long eventId, @PathVariable final Long userId)
    {
        eventService.deleteEvent(eventId, userId);
    }

    @DeleteMapping("/users/{userId}")
    public void deleteEventsForOrganizer(@PathVariable final Long userId)
    {
        eventService.deleteEventsForOrganizer(userId);
    }

    @PostMapping("/{eventId}/users/{userId}/attendance")
    public void markAttendance(@PathVariable final Long eventId, @PathVariable final Long userId, @RequestParam final boolean isGoing)
    {
        final UserAttendanceRequest request = new UserAttendanceRequest(eventId, isGoing);
        eventService.markUserAttendance(request, userId);
    }

    @GetMapping("/{eventId}/stats")
    public Optional<EventAttendanceStats> getEventAttendance(@PathVariable final Long eventId)
    {
       return eventService.getEventAttendanceStats(eventId);
    }

    @GetMapping("/{eventId}/users/{userId}/attendance")
    public Optional<UserAttendance> getAttendanceForEvent(@PathVariable final Long eventId, @PathVariable final Long userId)
    {
        return eventService.getUserAttendanceForEvent(userId, eventId);
    }

    @GetMapping("/users/{userId}/attendance")
    public List<UserAttendance> getUserAttendance(@PathVariable final Long userId)
    {
        return eventService.getUserAttendance(userId);
    }
}
