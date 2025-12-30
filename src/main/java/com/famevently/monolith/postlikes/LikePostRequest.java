package com.famevently.monolith.postlikes;

public record LikePostRequest(
        String postId,
        ReactionType reactionType) {
}

