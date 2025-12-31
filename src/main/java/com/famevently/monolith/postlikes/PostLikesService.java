package com.famevently.monolith.postlikes;

import com.famevently.monolith.post.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PostLikesService {

    private final PostLikesRepository postLikesRepository;
    private final PostLikesStatsRepository postLikesStatsRepository;
    private final PostRepository postRepository;

    public PostLikesService(final PostLikesRepository postLikesRepository,
                            final PostLikesStatsRepository postLikesStatsRepository,
                            final PostRepository postRepository) {
        this.postLikesRepository = postLikesRepository;
        this.postLikesStatsRepository = postLikesStatsRepository;
        this.postRepository = postRepository;
    }

    @Transactional
    public void likePost(final LikePostRequest request, final long userId, final String postId) {
        postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));

        final Optional<PostLike> previousLike = postLikesRepository.findByPostIdAndUserId(postId, userId);

        postLikesRepository.upsert(postId, userId, request.reactionType());

        if (previousLike.isEmpty()) {
            postLikesStatsRepository.adjustReactionCount(
                    postId,
                    request.reactionType(),
                    LikeChange.INCREMENT);
        } else {
            final ReactionType oldReaction = previousLike.get().reactionName();
            if (oldReaction != request.reactionType()) {
                postLikesStatsRepository.switchReaction(postId, oldReaction, request.reactionType());
            }
        }
    }

    @Transactional
    public void unlikePost(final String postId, final long userId) {
        final Optional<PostLike> existingLike = postLikesRepository.findByPostIdAndUserId(postId, userId);

        if (existingLike.isEmpty()) {
            return;
        }

        postLikesRepository.delete(postId, userId);

        final ReactionType reactionType = existingLike.get().reactionName();
        postLikesStatsRepository.adjustReactionCount(
                postId,
                reactionType,
                LikeChange.DECREMENT);
    }

    @Transactional(readOnly = true)
    public Optional<PostLike> getUserLikeForPost(final String postId, final long userId) {
        return postLikesRepository.findByPostIdAndUserId(postId, userId);
    }

    @Transactional(readOnly = true)
    public List<PostLike> getLikesForPost(final String postId) {
        return postLikesRepository.findByPostId(postId);
    }

    @Transactional(readOnly = true)
    public List<PostLike> getLikesByUser(final long userId) {
        return postLikesRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Optional<PostLikesStats> getStatsForPost(final String postId) {
        return postLikesStatsRepository.findByPostId(postId);
    }
}

