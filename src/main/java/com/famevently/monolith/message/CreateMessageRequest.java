package com.famevently.monolith.message;

public record CreateMessageRequest(
    String messageId,
    long userId,
    Long eventId,
    String description,
    int imageCount
) {

}
