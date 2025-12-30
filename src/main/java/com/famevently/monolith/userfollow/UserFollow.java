package com.famevently.monolith.userfollow;

import java.time.OffsetDateTime;

public record UserFollow(
        long followerId,
        long followedId,
        OffsetDateTime createdAt) {
}

