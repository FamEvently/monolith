package com.famevently.monolith.moderateevents;

import java.time.OffsetDateTime;

public record ModerateEvent(
    long moderationId,
    ModerationStatus status,
    ModerationReason reason, 
    long eventId,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {}
