package com.famevently.monolith.post;

import java.time.OffsetDateTime;

public record Post(
        String postId,
        long userId,
        long eventId,
        String description,
        int imageCount,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
