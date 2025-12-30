package com.famevently.monolith.message;

import org.springframework.stereotype.Service;

@Service
public class MessageService {
    private final MessageRepository messageRepository;

    public MessageService(final MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public String createMessage(final CreateMessageRequest request) {
        return messageRepository.upsert(request);
    }

    public void deleteMessageById(final String messageId) {
        messageRepository.deleteById(messageId);
    }
}
