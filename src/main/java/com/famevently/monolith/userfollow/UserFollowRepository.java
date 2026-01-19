package com.famevently.monolith.userfollow;

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
public class UserFollowRepository extends NamedParameterJdbcDaoSupport {

    private static final DataClassRowMapper<UserFollow> ROW_MAPPER = new DataClassRowMapper<>(UserFollow.class);

    public UserFollowRepository(final DataSource dataSource) {
        setDataSource(dataSource);
    }

    public void insert(final long followerId, final long followedId) {
        final String sql = """
                INSERT INTO user_follow (follower_id, followed_id)
                VALUES (:follower_id, :followed_id)
                """;

        var params = new MapSqlParameterSource()
                .addValue("follower_id", followerId)
                .addValue("followed_id", followedId);

        getNamedParameterJdbcTemplate().update(sql, params);
    }

    public void delete(final long followerId, final long followedId) {
        final String sql = "DELETE FROM user_follow WHERE follower_id = :follower_id AND followed_id = :followed_id";

        final Map<String, Object> params = Map.of(
                "follower_id", followerId,
                "followed_id", followedId);

        getNamedParameterJdbcTemplate().update(sql, params);
    }

    public Optional<UserFollow> findByFollowerAndFollowed(final long followerId, final long followedId) {
        final String sql = """
                SELECT follower_id, followed_id, created_at
                FROM user_follow
                WHERE follower_id = :follower_id AND followed_id = :followed_id
                """;

        final Map<String, Object> params = Map.of(
                "follower_id", followerId,
                "followed_id", followedId);

        try {
            return Optional.of(Objects.requireNonNull(
                    getNamedParameterJdbcTemplate().queryForObject(sql, params, ROW_MAPPER)));
        } catch (final EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<UserFollow> findFollowers(final long userId) {
        final String sql = """
                SELECT follower_id, followed_id, created_at
                FROM user_follow
                WHERE followed_id = :user_id
                ORDER BY created_at DESC
                """;
        final Map<String, Object> params = Map.of("user_id", userId);

        return getNamedParameterJdbcTemplate().query(sql, params, ROW_MAPPER);
    }

    public List<UserFollow> findFollowing(final long userId) {
        final String sql = """
                SELECT follower_id, followed_id, created_at
                FROM user_follow
                WHERE follower_id = :user_id
                ORDER BY created_at DESC
                """;
        final Map<String, Object> params = Map.of("user_id", userId);

        return getNamedParameterJdbcTemplate().query(sql, params, ROW_MAPPER);
    }
}

