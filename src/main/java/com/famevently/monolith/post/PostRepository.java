package com.famevently.monolith.post;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.*;

@Repository
public class PostRepository extends NamedParameterJdbcDaoSupport {

    private static final DataClassRowMapper<Post> ROW_MAPPER = new DataClassRowMapper<>(Post.class);
    private static final String SELECT_POST = """
            SELECT p.post_id, m.user_id, m.event_id, pt.type_name AS post_type,
                   m.description, m.image_count, p.is_organizer, p.parent_post_id,
                   m.created_at, m.updated_at
            FROM post p
            JOIN message m ON p.message_id = m.message_id
            JOIN n_post_types pt ON p.post_type_id = pt.post_type_id
            """;
    private final PostTypesRepository postTypesRepository;

    public PostRepository(final DataSource dataSource, final PostTypesRepository postTypesRepository) {
        this.postTypesRepository = postTypesRepository;
        setDataSource(dataSource);

    }

    public Optional<Post> insert(final PostContext context, final String messageId) {
        final String postId = UUID.randomUUID().toString();
        final String sql = """
                INSERT INTO post (post_id, message_id, post_type_id, is_organizer, parent_post_id)
                VALUES (:post_id, :message_id, :post_type_id, :is_organizer, :parent_post_id)
                """;

        var params = new MapSqlParameterSource()
                .addValue("post_id", postId)
                .addValue("message_id", messageId)
                .addValue("post_type_id", postTypesRepository.getIdByType(context.postType()))
                .addValue("is_organizer", context.isOrganizer())
                .addValue("parent_post_id", context.parentPostId());

        getNamedParameterJdbcTemplate().update(sql, params);
        return findById(postId);
    }

    public Optional<Post> findById(final String postId) {
        final String sql = SELECT_POST + "WHERE p.post_id = :post_id";
        final Map<String, Object> params = Map.of("post_id", postId);

        try {
            return Optional.of(Objects.requireNonNull(
                    getNamedParameterJdbcTemplate().queryForObject(sql, params, ROW_MAPPER)));
        } catch (final EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Post> findByUserId(final long userId) {
        final String sql = SELECT_POST + "WHERE m.user_id = :user_id ORDER BY m.created_at DESC";
        final Map<String, Object> params = Map.of("user_id", userId);

        return getNamedParameterJdbcTemplate().query(sql, params, ROW_MAPPER);
    }

    public List<Post> findByEventId(final long eventId) {
        final String sql = SELECT_POST + """
                WHERE m.event_id = :event_id
                AND pt.type_name = 'EVENT'
                ORDER BY m.created_at DESC
                """;
        final Map<String, Object> params = Map.of("event_id", eventId);

        return getNamedParameterJdbcTemplate().query(sql, params, ROW_MAPPER);
    }

    public List<Post> findBlogPosts() {
        final String sql = SELECT_POST + """
                WHERE pt.type_name = 'BLOG'
                ORDER BY m.created_at DESC
                """;

        return getNamedParameterJdbcTemplate().query(sql, Map.of(), ROW_MAPPER);
    }

    public List<Post> findByParentPostId(final String parentPostId) {
        final String sql = SELECT_POST + """
                WHERE p.parent_post_id = :parent_post_id
                ORDER BY m.created_at ASC
                """;
        final Map<String, Object> params = Map.of("parent_post_id", parentPostId);

        return getNamedParameterJdbcTemplate().query(sql, params, ROW_MAPPER);
    }

    public void deleteByMessageId(final String messageId) {
        final String sql = "DELETE FROM post WHERE message_id = :message_id";
        final Map<String, Object> params = Map.of("message_id", messageId);

        getNamedParameterJdbcTemplate().update(sql, params);
    }
}
