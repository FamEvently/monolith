package com.famevently.monolith.post;

import java.time.OffsetDateTime;

public record Post(
        String postId,
        long userId,
        Long eventId,
        String postType,
        String description,
        int imageCount,
        boolean isOrganizer,
        String parentPostId,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {
}
