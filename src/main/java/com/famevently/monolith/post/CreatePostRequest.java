package com.famevently.monolith.post;

import com.famevently.monolith.message.CreateMessageRequest;

public record CreatePostRequest (
        long userId,
        Long eventId,
        String description,
        int imageCount
)
{
}
