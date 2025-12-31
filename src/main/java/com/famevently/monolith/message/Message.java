package com.famevently.monolith.message;

import java.time.OffsetDateTime;

public record Message(
    String messageId,
    long userId,
    Long eventId,
    String description,
    int imageCount,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {
}