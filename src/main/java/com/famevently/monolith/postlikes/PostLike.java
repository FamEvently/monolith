package com.famevently.monolith.postlikes;

import java.time.OffsetDateTime;

public record PostLike(
        String postId,
        long userId,
        ReactionType reactionName,
        OffsetDateTime createdAt) {
}

