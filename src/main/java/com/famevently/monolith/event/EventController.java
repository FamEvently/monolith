package com.famevently.monolith.event;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/event")
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<?> createEvent(@Valid @RequestBody CreateEventRequest request) {
        eventService.createEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // GET by ID
    @GetMapping("/{id}")
    public ResponseEntity<Event> getEvent(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEvent(id));
    }

    // GET user events
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Event>> getUserEvents(@PathVariable Long userId) {
        return ResponseEntity.ok(eventService.getUserEvents(userId));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateEvent(@PathVariable Long id,
                                            @Valid @RequestBody CreateEventRequest request,
                                            Long userId) {
        eventService.updateEvent(id, request, userId);
        return ResponseEntity.noContent().build();
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id, Long userId) {
        eventService.deleteEvent(id, userId);
        return ResponseEntity.noContent().build();
    }
}
