package com.famevently.monolith.post;

public record CreateRepostRequest(
        Long eventId,
        String description,
        String parentPostId)
{
}
