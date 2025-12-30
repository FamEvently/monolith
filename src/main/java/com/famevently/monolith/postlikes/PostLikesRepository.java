package com.famevently.monolith.postlikes;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Repository
public class PostLikesRepository extends NamedParameterJdbcDaoSupport {

    private static final DataClassRowMapper<PostLike> ROW_MAPPER = new DataClassRowMapper<>(PostLike.class);
    private static final String SELECT_LIKE = """
            SELECT pl.post_id, pl.user_id, r.reaction_name, pl.created_at
            FROM post_likes pl
            JOIN n_post_reactions r ON pl.reaction_id = r.reaction_id
            """;

    private final PostReactionsRepository postReactionsRepository;

    public PostLikesRepository(final DataSource dataSource,
                               final PostReactionsRepository postReactionsRepository) {
        setDataSource(dataSource);
        this.postReactionsRepository = postReactionsRepository;
    }

    public void upsert(final String postId, final long userId, final ReactionType reactionType) {
        final Long reactionId = postReactionsRepository.getIdByReaction(reactionType);

        final String sql = """
                INSERT INTO post_likes (post_id, user_id, reaction_id)
                VALUES (:post_id, :user_id, :reaction_id)
                ON DUPLICATE KEY UPDATE
                    reaction_id = :reaction_id
                """;

        var params = new MapSqlParameterSource()
                .addValue("post_id", postId)
                .addValue("user_id", userId)
                .addValue("reaction_id", reactionId);

        getNamedParameterJdbcTemplate().update(sql, params);
    }

    public void delete(final String postId, final long userId) {
        final String sql = "DELETE FROM post_likes WHERE post_id = :post_id AND user_id = :user_id";

        final Map<String, Object> params = Map.of(
                "post_id", postId,
                "user_id", userId);

        getNamedParameterJdbcTemplate().update(sql, params);
    }

    public Optional<PostLike> findByPostIdAndUserId(final String postId, final long userId) {
        final String sql = SELECT_LIKE + "WHERE pl.post_id = :post_id AND pl.user_id = :user_id";

        final Map<String, Object> params = Map.of(
                "post_id", postId,
                "user_id", userId);

        try {
            return Optional.of(Objects.requireNonNull(
                    getNamedParameterJdbcTemplate().queryForObject(sql, params, ROW_MAPPER)));
        } catch (final EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<PostLike> findByPostId(final String postId) {
        final String sql = SELECT_LIKE + "WHERE pl.post_id = :post_id ORDER BY pl.created_at DESC";
        final Map<String, Object> params = Map.of("post_id", postId);

        return getNamedParameterJdbcTemplate().query(sql, params, ROW_MAPPER);
    }

    public List<PostLike> findByUserId(final long userId) {
        final String sql = SELECT_LIKE + "WHERE pl.user_id = :user_id ORDER BY pl.created_at DESC";
        final Map<String, Object> params = Map.of("user_id", userId);

        return getNamedParameterJdbcTemplate().query(sql, params, ROW_MAPPER);
    }
}

