package com.famevently.monolith.post;

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
public class PostRepository extends NamedParameterJdbcDaoSupport {

    private static final DataClassRowMapper<Post> ROW_MAPPER = new DataClassRowMapper<>(
            Post.class);
    public PostRepository(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public Optional<Post> create(final String postId, final String messageId)
    {
        final String sql = """
                INSERT INTO post (
                post_id, message_id, parent_post_id)
                VALUES (
                :post_id, :message_id, :parent_post_id)
                """;

        var params = new MapSqlParameterSource()
                .addValue("post_id", postId)
                .addValue("message_id", messageId)
                .addValue("parent_post_id", null);
        getNamedParameterJdbcTemplate().update(sql, params);

        return getPostById(postId);
    }

    public Optional<Post> getPostById(final String postId) {
        final String sql = """
                SELECT p.post_id, m.user_id, m.event_id, m.description, m.image_count, m.created_at, m.updated_at FROM message m
                JOIN post p ON m.message_id = p.message_id
                WHERE p.post_id = :post_id
                """;

        final Map<String, Object> params = Map.of("post_id", postId);

        try
        {
            return Optional.of(Objects.requireNonNull(getNamedParameterJdbcTemplate().queryForObject(sql, params, ROW_MAPPER)));
        }
        catch (final EmptyResultDataAccessException e)
        {
            return Optional.empty();
        }

    }

    public List<Post> getPostsByUser(final long userId) {
        final String sql = """
                SELECT p.post_id, m.user_id, m.event_id, m.description, m.image_count, m.created_at, m.updated_at FROM message m
                JOIN post p ON m.message_id = p.message_id
                WHERE m.user_id = :user_id
                """;

        final Map<String, Object> params = Map.of("user_id", userId);

        return getNamedParameterJdbcTemplate().query(sql, params, ROW_MAPPER);
    }
}
