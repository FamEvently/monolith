package com.famevently.monolith.post;

public record CreateBlogPostRequest(
        long userId,
        String description,
        int imageCount
) {
}
