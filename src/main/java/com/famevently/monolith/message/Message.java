package com.famevently.monolith.message;

import java.time.OffsetDateTime;

public record Message(
    String messageId,
    long userId,
    long eventId,
    String description,
    int imageCount,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {
}

//UUID message_id PK
//        TIMESTAMPTZ timestamp
//        BIGINT user_id FK "FK -> user(user_id), NOT NULL, ON DELETE CASCADE"
//        BIGINT event_id FK "FK (event_id, user_id) -> event(event_id, user_id), NULL"
//        TEXT description
//        INTEGER image_count