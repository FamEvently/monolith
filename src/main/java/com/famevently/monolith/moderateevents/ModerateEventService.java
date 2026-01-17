package com.famevently.monolith.moderateevents;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ModerateEventService {
    private final ModerateEventRepository moderateEventRepository;

    public ModerateEventService(final ModerateEventRepository moderateEventRepository) {
        this.moderateEventRepository = moderateEventRepository;
    }

    public List<ModerateEvent> getEventModerations(long eventId) {
        return moderateEventRepository.getEventModerations(eventId);
    }

    public void moderateEvent(long eventId, long statusId, long reasonId) {
        moderateEventRepository.insert(
            eventId,
            statusId,
            reasonId,
            OffsetDateTime.now(),
            OffsetDateTime.now()
        );
    }

    public List<ModerateEvent> getPendingModerations() {
        return moderateEventRepository.getPendingModerations();
    }

    public void deleteModeration(long moderationId) {
        moderateEventRepository.delete(moderationId);
    }
}
