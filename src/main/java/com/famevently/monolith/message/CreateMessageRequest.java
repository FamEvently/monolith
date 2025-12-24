package com.famevently.monolith.message;

import org.springframework.stereotype.Component;


public record CreateMessageRequest(
    String messageId,
    long userId,
    Long eventId,
    String description,
    int imageCount
) {

}
