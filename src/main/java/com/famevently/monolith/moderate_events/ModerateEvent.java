package com.famevently.monolith.moderate_events;

import java.time.OffsetDateTime;

public record ModerateEvent(
    long moderationId,
    long statusId,
    long reasonId, 
    long eventId,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {}
