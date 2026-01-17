package com.famevently.monolith.moderateevents;

import java.util.List;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/event-moderation")
public class ModerateEventController {

    private final ModerateEventService moderateEventService;

    public ModerateEventController(final ModerateEventService moderateEventService) {
        this.moderateEventService = moderateEventService;
    }

    @PostMapping("/{eventId}")
    public void moderateEvent(@PathVariable long eventId, 
                            @RequestParam ModerationStatus status,
                            @RequestParam ModerationReason reason) {
        moderateEventService.moderateEvent(eventId, status, reason);
    }

    @GetMapping("/{eventId}")
    public List<ModerateEvent> getEventModerations(@PathVariable long eventId) {
        return moderateEventService.getEventModerations(eventId);
    }

    @DeleteMapping("/{moderationId}")
    public void deleteModeration(@PathVariable long moderationId) {
        moderateEventService.deleteModeration(moderationId);
    }

    @GetMapping("/pending")
    public List<ModerateEvent> getPendingModerations() {
        return moderateEventService.getPendingModerations();
    }
}
