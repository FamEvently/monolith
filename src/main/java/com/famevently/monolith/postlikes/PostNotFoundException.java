package com.famevently.monolith.postlikes;

public class PostNotFoundException extends RuntimeException {

    private final String postId;

    public static final String POST_NOT_FOUND = "PostNotFound";

    public PostNotFoundException(final String postId) {
        super(POST_NOT_FOUND);
        this.postId = postId;
    }

    public String getPostId() {
        return postId;
    }
}
