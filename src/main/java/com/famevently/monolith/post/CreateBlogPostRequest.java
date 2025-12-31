package com.famevently.monolith.post;

public record CreateBlogPostRequest(
        String description,
        int imageCount
) {
}
