package com.famevently.monolith.userfollow;

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
public class UserFollowStatsRepository extends NamedParameterJdbcDaoSupport {

    private static final DataClassRowMapper<UserFollowStats> ROW_MAPPER = new DataClassRowMapper<>(UserFollowStats.class);

    public UserFollowStatsRepository(final DataSource dataSource) {
        setDataSource(dataSource);
    }

    public Optional<UserFollowStats> findByUserId(final long userId) {
        final String sql = """
                SELECT user_id, followers_count, following_count, updated_at
                FROM user_follow_stats
                WHERE user_id = :user_id
                """;
        final Map<String, Object> params = Map.of("user_id", userId);

        try {
            return Optional.of(Objects.requireNonNull(
                    getNamedParameterJdbcTemplate().queryForObject(sql, params, ROW_MAPPER)));
        } catch (final EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void adjustFollowersCount(final long userId, final FollowChange change) {
        final String sql = "INSERT INTO user_follow_stats (user_id, followers_count) " +
                "VALUES (:user_id, GREATEST(0, :delta)) " +
                "ON DUPLICATE KEY UPDATE " +
                "followers_count = GREATEST(0, followers_count + :delta), " +
                "updated_at = :updated_at";

        var params = new MapSqlParameterSource()
                .addValue("user_id", userId)
                .addValue("delta", change.getDelta())
                .addValue("updated_at", OffsetDateTime.now());

        getNamedParameterJdbcTemplate().update(sql, params);
    }

    public void adjustFollowingCount(final long userId, final FollowChange change) {
        final String sql = "INSERT INTO user_follow_stats (user_id, following_count) " +
                "VALUES (:user_id, GREATEST(0, :delta)) " +
                "ON DUPLICATE KEY UPDATE " +
                "following_count = GREATEST(0, following_count + :delta), " +
                "updated_at = :updated_at";

        var params = new MapSqlParameterSource()
                .addValue("user_id", userId)
                .addValue("delta", change.getDelta())
                .addValue("updated_at", OffsetDateTime.now());

        getNamedParameterJdbcTemplate().update(sql, params);
    }
}

