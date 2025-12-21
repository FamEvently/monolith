package com.famevently.monolith.post;

import com.famevently.monolith.message.CreateMessageRequest;
import com.famevently.monolith.message.MessageService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final MessageService messageService;

    public PostService(final PostRepository postRepository, final MessageService messageService) {
        this.postRepository = postRepository;
        this.messageService = messageService;
    }

    public Post createPost(final CreatePostRequest postRequest) {
        //perform any necessary validations or business logic here
        final String messageId = UUID.randomUUID().toString();
        final CreateMessageRequest messageRequest = new CreateMessageRequest(
                messageId,
                postRequest.userId(),
                postRequest.eventId(),
                postRequest.description(),
                postRequest.imageCount()
        );
        messageService.createMessage(messageRequest);
        return postRepository.create(UUID.randomUUID().toString(), messageId).orElseThrow(IllegalStateException::new);
    }

    public Post getPostById(final String postId) {
        return postRepository.getPostById(postId).orElseThrow(IllegalStateException::new);
    }

    public List<Post> getPostsForUser(final long userId) {
        return postRepository.getPostsByUser(userId);
    }
}
