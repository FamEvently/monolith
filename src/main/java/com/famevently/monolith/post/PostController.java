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

    @PostMapping
    public Post createPost(@RequestBody final CreatePostRequest request) {
        return postService.createPost(request);
    }

    @GetMapping("/{postId}")
    public Post getPostById(@PathVariable final String postId) {
        return postService.getPostById(postId);
    }

    @GetMapping("/users/{userId}")
    public List<Post> getPostsForUser(@PathVariable final long userId) {
        return postService.getPostsForUser(userId);
    }
}
