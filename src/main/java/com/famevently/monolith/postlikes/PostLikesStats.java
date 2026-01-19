package com.famevently.monolith.postlikes;

import java.time.OffsetDateTime;

public record PostLikesStats(
        String postId,
        int totalLikesCount,
        int heartCount,
        int likeCount,
        int amazeCount,
        int sadCount,
        int angryCount,
        int surprisedCount,
        OffsetDateTime updatedAt) {
}

