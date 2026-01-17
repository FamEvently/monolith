package com.famevently.monolith.post;

import com.famevently.monolith.event.EventNotFoundException;
import com.famevently.monolith.event.EventRepository;
import com.famevently.monolith.message.CreateMessageRequest;
import com.famevently.monolith.message.MessageService;
import com.famevently.monolith.postlikes.PostNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final EventRepository eventRepository;
    private final MessageService messageService;

    public PostService(final PostRepository postRepository,
                       final EventRepository eventRepository,
                       final MessageService messageService) {
        this.postRepository = postRepository;
        this.eventRepository = eventRepository;
        this.messageService = messageService;
    }

    @Transactional
    public Post createEventPost(final CreatePostRequest request, final long userId) {
        validateEventExists(request.eventId());
        final boolean isOrganizer = eventRepository.getEventByForOrganizer(request.eventId(), userId).isPresent();
        final PostContext context = PostContext.eventPost(userId, request.eventId(), request.description(), request.imageCount(), isOrganizer);

        return createPost(context);
    }

    private void validateEventExists(final Long eventId) {
        if (eventId != null && eventRepository.getEventById(eventId).isEmpty()) {
            throw new EventNotFoundException(eventId);
        }
    }

    @Transactional
    public Post createBlogPost(final CreateBlogPostRequest request, final long userId) {
        final PostContext context = PostContext.blogPost(userId, request.description(), request.imageCount());

        return createPost(context);
    }

    @Transactional
    public Post createRepost(final CreateRepostRequest request, final long userId) {
        validateEventExists(request.eventId());
        validateParentPostExists(request.parentPostId());
        final boolean isOrganizer = eventRepository.getEventByForOrganizer(request.eventId(), userId).isPresent();
        final PostContext context = PostContext.repost(userId, request.eventId(), request.description(), isOrganizer, request.parentPostId());

        return createPost(context);
    }

    private void validateParentPostExists(final String parentPostId) {
        if (parentPostId != null && postRepository.findById(parentPostId).isEmpty()) {
            throw new PostNotFoundException(parentPostId);
        }
    }

    private Post createPost(final PostContext context) {
        final String messageId = UUID.randomUUID().toString();
        final CreateMessageRequest messageRequest = new CreateMessageRequest(
                messageId,
                context.userId(),
                context.eventId(),
                context.description(),
                context.imageCount()
        );
        messageService.createMessage(messageRequest);

        return postRepository.insert(context, messageId)
                .orElseThrow(() -> new IllegalStateException("Failed to create post"));
    }

    public Post getPostById(final String postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found"));
    }

    public List<Post> getPostsByUser(final long userId) {
        return postRepository.findByUserId(userId);
    }

    public List<Post> getEventPosts(final long eventId) {
        return postRepository.findByEventId(eventId);
    }

    public List<Post> getBlogPosts() {
        return postRepository.findBlogPosts();
    }

    public List<Post> getReplies(final String parentPostId) {
        return postRepository.findByParentPostId(parentPostId);
    }

    @Transactional
    public void deletePost(final String postId) {
        final Post post = getPostById(postId);

        postRepository.deleteByMessageId(post.messageId());

        messageService.deleteMessageById(post.messageId());
    }
}
