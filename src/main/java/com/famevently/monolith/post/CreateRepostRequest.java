package com.famevently.monolith.post;

public record CreateRepostRequest(
        long userId,
        Long eventId,
        String description,
        String parentPostId) {
}
