package com.famevently.monolith.userfollow;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserFollowService {

    private final UserFollowRepository userFollowRepository;
    private final UserFollowStatsRepository userFollowStatsRepository;

    public UserFollowService(final UserFollowRepository userFollowRepository,
                             final UserFollowStatsRepository userFollowStatsRepository) {
        this.userFollowRepository = userFollowRepository;
        this.userFollowStatsRepository = userFollowStatsRepository;
    }

    @Transactional
    public void follow(final long followerId, final long followedId) {
        if (followerId == followedId) {
            throw new IllegalArgumentException("User cannot follow themselves");
        }

        if (alreadyFollowing(followerId, followedId)) {
            return;
        }
        userFollowRepository.insert(followerId, followedId);

        // Update stats for both users
        userFollowStatsRepository.adjustFollowingCount(followerId, FollowChange.INCREMENT);
        userFollowStatsRepository.adjustFollowersCount(followedId, FollowChange.INCREMENT);
    }

    @Transactional
    public void unfollow(final long followerId, final long followedId) {
        if (!alreadyFollowing(followerId, followedId)) {
            return;
        }

        userFollowRepository.delete(followerId, followedId);

        userFollowStatsRepository.adjustFollowingCount(followerId, FollowChange.DECREMENT);
        userFollowStatsRepository.adjustFollowersCount(followedId, FollowChange.DECREMENT);
    }

    @Transactional(readOnly = true)
    public boolean isFollowing(final long followerId, final long followedId) {
        return alreadyFollowing(followerId, followedId);
    }

    @Transactional(readOnly = true)
    public List<UserFollow> getFollowing(final long userId) {
        return userFollowRepository.findFollowing(userId);
    }

    @Transactional(readOnly = true)
    public Optional<UserFollowStats> getStats(final long userId) {
        return userFollowStatsRepository.findByUserId(userId);
    }

    private boolean alreadyFollowing(final long followerId, final long followedId) {
        return userFollowRepository.findByFollowerAndFollowed(followerId, followedId).isPresent();
    }
}

