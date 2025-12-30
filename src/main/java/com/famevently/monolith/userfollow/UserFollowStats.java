package com.famevently.monolith.userfollow;

import java.time.OffsetDateTime;

public record UserFollowStats(
        long userId,
        int followersCount,
        int followingCount,
        OffsetDateTime updatedAt) {
}

