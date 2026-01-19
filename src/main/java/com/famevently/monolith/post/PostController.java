package com.famevently.monolith.post;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/posts")
public class PostController {
    private final PostService postService;

    public PostController(final PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/users/{userId}/event")
    public Post createEventPost(@PathVariable final Long userId, @RequestBody final CreatePostRequest request) {
        return postService.createEventPost(request, userId);
    }

    @PostMapping("/users/{userId}/blog")
    public Post createBlogPost(@PathVariable final Long userId, @RequestBody final CreateBlogPostRequest request) {
        return postService.createBlogPost(request, userId);
    }

    @PostMapping("/users/{userId}/repost")
    public Post createPost(@PathVariable final Long userId, @RequestBody final CreateRepostRequest request) {
        return postService.createRepost(request, userId);
    }

    @GetMapping("/{postId}")
    public Post getPostById(@PathVariable final String postId) {
        return postService.getPostById(postId);
    }

    @GetMapping("/users/{userId}")
    public List<Post> getPostsByUser(@PathVariable final long userId) {
        return postService.getPostsByUser(userId);
    }

    @GetMapping("/events/{eventId}")
    public List<Post> getEventPosts(@PathVariable final long eventId) {
        return postService.getEventPosts(eventId);
    }

    @GetMapping("/blog")
    public List<Post> getBlogPosts() {
        return postService.getBlogPosts();
    }

    @GetMapping("/{postId}/reposts")
    public List<Post> getReposts(@PathVariable final String postId) {
        return postService.getReplies(postId);
    }

    @DeleteMapping("/{postId}")
    public void deletePost(@PathVariable final String postId) {
        postService.deletePost(postId);
    }
}
