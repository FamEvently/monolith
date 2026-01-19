package com.famevently.monolith.post;

public record PostContext(
        long userId,
        Long eventId,
        PostType postType,
        String description,
        int imageCount,
        boolean isOrganizer,
        String parentPostId) {

    public static PostContext eventPost(final long userId, final long eventId, final String description,
                                        final int imageCount, final boolean isOrganizer) {
        return new PostContext(userId, eventId, PostType.EVENT, description, imageCount, isOrganizer, null);
    }

    public static PostContext blogPost(final long userId, final String description, final int imageCount) {
        return new PostContext(userId, null, PostType.BLOG, description, imageCount, false, null);
    }

    public static PostContext repost(final long userId, final Long eventId, final String description, final boolean isOrganizer, final String parentPostId) {
        return new PostContext(userId, eventId, PostType.EVENT, description, 0, isOrganizer, parentPostId);
    }
}
