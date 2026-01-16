package com.famevently.monolith.moderate_events;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ModerateEventService {
    private final ModerateEventRepository moderateEventRepository;

    public ModerateEventService(final ModerateEventRepository moderateEventRepository) {
        this.moderateEventRepository = moderateEventRepository;
    }

    @Transactional(readOnly = true)
    public List<ModerateEvent> getEventModerations(long eventId) {
        return moderateEventRepository.getEventModerations(eventId);
    }

    @Transactional
    public void moderateEvent(long eventId, ModerationStatus status, ModerationReason reason) {

        if (status == null || reason == null) {
            throw new IllegalArgumentException("Moderation status and reason cannot be null");
        }

        moderateEventRepository.insert(
             0L,
            eventId,
            status,
            reason,
            OffsetDateTime.now(),
            OffsetDateTime.now()
        );
    }

    @Transactional(readOnly = true)
    public List<ModerateEvent> getPendingModerations() {
        return moderateEventRepository.getPendingModerations();
    }

    @Transactional
    public void deleteModeration(long moderationId) {
        moderateEventRepository.delete(moderationId);
    }
}
