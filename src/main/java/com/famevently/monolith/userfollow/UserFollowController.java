package com.famevently.monolith.userfollow;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/users")
public class UserFollowController {

    private final UserFollowService userFollowService;

    public UserFollowController(final UserFollowService userFollowService) {
        this.userFollowService = userFollowService;
    }

    @PostMapping("/{followerId}/follow/{followedId}")
    public void follow(@PathVariable final long followerId,
                       @PathVariable final long followedId) {
        userFollowService.follow(followerId, followedId);
    }

    @DeleteMapping("/{followerId}/follow/{followedId}")
    public void unfollow(@PathVariable final long followerId,
                         @PathVariable final long followedId) {
        userFollowService.unfollow(followerId, followedId);
    }

    @GetMapping("/{followerId}/following/{followedId}")
    public boolean isFollowing(@PathVariable final long followerId,
                               @PathVariable final long followedId) {
        return userFollowService.isFollowing(followerId, followedId);
    }

    @GetMapping("/{userId}/following")
    public List<UserFollow> getFollowing(@PathVariable final long userId) {
        return userFollowService.getFollowing(userId);
    }

    @GetMapping("/{userId}/follow-stats")
    public Optional<UserFollowStats> getStats(@PathVariable final long userId) {
        return userFollowService.getStats(userId);
    }
}

