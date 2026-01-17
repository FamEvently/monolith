package com.famevently.monolith.userrating;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.stereotype.Repository;

@Repository
public class UserRatingRepository extends NamedParameterJdbcDaoSupport {
    
    private static final DataClassRowMapper<UserRating> ROW_MAPPER = new DataClassRowMapper<>(
            UserRating.class);
        public UserRatingRepository(final DataSource dataSource) {
           setDataSource(dataSource);
        }

    public void insert(
        long ratedUserId,
        long ratedByUserId,
        long eventId,
        BigDecimal score,
        OffsetDateTime createdAt
    ) {
        final String sql = """
            INSERT INTO user_rating (
                rated_user_id,
                rated_by_user_id,
                event_id,
                score,
                created_at
            ) VALUES (
                :rated_user_id,
                :rated_by_user_id,
                :event_id,
                :score,
                :created_at
            )
            """;

        final Map<String, Object> params = Map.of(
                "rated_user_id", ratedUserId,
                "rated_by_user_id", ratedByUserId,
                "event_id", eventId,
                "score", score,
                "created_at", createdAt
        );

        getNamedParameterJdbcTemplate().update(sql, params);
    }

    public List<UserRating> findByUserId(long userId) {
        final String sql = """
            SELECT rating_id,
                   rated_user_id,
                   rated_by_user_id,
                   event_id,
                   score,
                   created_at
            FROM user_rating
            WHERE rated_user_id = :user_id
            ORDER BY created_at DESC
            """;

        final Map<String, Object> params = Map.of("user_id", userId);

        return getNamedParameterJdbcTemplate().query(sql, params, ROW_MAPPER);
    }

    public List<UserRating> findByEventId(long eventId) {
        final String sql = """
            SELECT rating_id,
                   rated_user_id,
                   rated_by_user_id,
                   event_id,
                   score,
                   created_at
            FROM user_rating
            WHERE event_id = :event_id
            ORDER BY created_at DESC
            """;

        final Map<String, Object> params = Map.of("event_id", eventId);

        return getNamedParameterJdbcTemplate().query(sql, params, ROW_MAPPER);
    }

    public Optional<UserRating> findByRatedAndRater(final long ratedUserId, final long ratedByUserId, final long eventId) {
        final String sql = """
                SELECT rating_id,
                    rated_user_id,
                    rated_by_user_id,
                    event_id,
                    score,
                    created_at
                FROM user_rating
                WHERE rated_user_id = :rated_user_id
                AND rated_by_user_id = :rated_by_user_id
                AND event_id = :event_id
                """;

        final Map<String, Object> params = Map.of(
                "rated_user_id", ratedUserId,
                "rated_by_user_id", ratedByUserId,
                "event_id", eventId
        );

        try {
            return Optional.ofNullable(
                    getNamedParameterJdbcTemplate().queryForObject(sql, params, ROW_MAPPER));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
