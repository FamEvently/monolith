package com.famevently.monolith.postlikes;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Repository
public class PostLikesStatsRepository extends NamedParameterJdbcDaoSupport {

    private static final DataClassRowMapper<PostLikesStats> ROW_MAPPER = new DataClassRowMapper<>(PostLikesStats.class);

    public PostLikesStatsRepository(final DataSource dataSource) {
        setDataSource(dataSource);
    }

    public Optional<PostLikesStats> findByPostId(final String postId) {
        final String sql = """
                SELECT post_id, total_likes_count, heart_count, like_count, amaze_count,
                       sad_count, angry_count, surprised_count, updated_at
                FROM post_likes_stats
                WHERE post_id = :post_id
                """;
        final Map<String, Object> params = Map.of("post_id", postId);

        try {
            return Optional.of(Objects.requireNonNull(
                    getNamedParameterJdbcTemplate().queryForObject(sql, params, ROW_MAPPER)));
        } catch (final EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void adjustReactionCount(final String postId,
                                    final ReactionType reactionType,
                                    final LikeChange change) {
        final String col = getReactionColumn(reactionType);

        final String sql = "INSERT INTO post_likes_stats (post_id, total_likes_count, " + col + ") " +
                "VALUES (:post_id, GREATEST(0, :delta), GREATEST(0, :delta)) " +
                "ON DUPLICATE KEY UPDATE " +
                "total_likes_count = GREATEST(0, total_likes_count + :delta), " +
                col + " = GREATEST(0, " + col + " + :delta), " +
                "updated_at = :updated_at";

        var params = new MapSqlParameterSource()
                .addValue("post_id", postId)
                .addValue("delta", change.getDelta())
                .addValue("updated_at", OffsetDateTime.now());

        getNamedParameterJdbcTemplate().update(sql, params);
    }

    public void switchReaction(final String postId,
                               final ReactionType oldReaction,
                               final ReactionType newReaction) {
        final String oldCol = getReactionColumn(oldReaction);
        final String newCol = getReactionColumn(newReaction);

        final String sql = "UPDATE post_likes_stats SET " +
                oldCol + " = GREATEST(0, " + oldCol + " - 1), " +
                newCol + " = " + newCol + " + 1, " +
                "updated_at = :updated_at " +
                "WHERE post_id = :post_id";

        var params = new MapSqlParameterSource()
                .addValue("post_id", postId)
                .addValue("updated_at", OffsetDateTime.now());

        getNamedParameterJdbcTemplate().update(sql, params);
    }

    public void delete(final String postId) {
        final String sql = "DELETE FROM post_likes_stats WHERE post_id = :post_id";
        final Map<String, Object> params = Map.of("post_id", postId);

        getNamedParameterJdbcTemplate().update(sql, params);
    }

    private String getReactionColumn(final ReactionType reactionType) {
        return switch (reactionType) {
            case HEART -> "heart_count";
            case LIKE -> "like_count";
            case AMAZE -> "amaze_count";
            case SAD -> "sad_count";
            case ANGRY -> "angry_count";
            case SURPRISED -> "surprised_count";
        };
    }
}

