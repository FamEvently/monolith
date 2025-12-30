package com.famevently.monolith.postlikes;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/posts")
public class PostLikesController {

    private final PostLikesService postLikesService;

    public PostLikesController(final PostLikesService postLikesService) {
        this.postLikesService = postLikesService;
    }

    @PostMapping("/{postId}/likes/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void likePost(@PathVariable final String postId,
                         @PathVariable final long userId,
                         @RequestBody final LikePostRequest request) {
        postLikesService.likePost(new LikePostRequest(postId, request.reactionType()), userId);
    }

    @DeleteMapping("/{postId}/likes/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unlikePost(@PathVariable final String postId,
                           @PathVariable final long userId) {
        postLikesService.unlikePost(postId, userId);
    }

    @GetMapping("/{postId}/likes/users/{userId}")
    public Optional<PostLike> getUserLikeForPost(@PathVariable final String postId,
                                                 @PathVariable final long userId) {
        return postLikesService.getUserLikeForPost(postId, userId);
    }

    @GetMapping("/{postId}/likes")
    public List<PostLike> getLikesForPost(@PathVariable final String postId) {
        return postLikesService.getLikesForPost(postId);
    }

    @GetMapping("/likes/users/{userId}")
    public List<PostLike> getLikesByUser(@PathVariable final long userId) {
        return postLikesService.getLikesByUser(userId);
    }

    @GetMapping("/{postId}/likes/stats")
    public Optional<PostLikesStats> getStatsForPost(@PathVariable final String postId) {
        return postLikesService.getStatsForPost(postId);
    }
}
