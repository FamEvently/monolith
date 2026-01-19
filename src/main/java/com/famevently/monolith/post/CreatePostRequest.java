package com.famevently.monolith.post;

public record CreatePostRequest(
        Long eventId,
        String description,
        int imageCount,
        String parentPostId
) {
}
