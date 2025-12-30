package com.famevently.monolith.attendance;

import com.famevently.monolith.event.UserAttendanceRequest;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.stereotype.Repository;

import org.springframework.dao.EmptyResultDataAccessException;

import javax.sql.DataSource;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Repository
public class UserAttendanceRepository extends NamedParameterJdbcDaoSupport {

    private static final DataClassRowMapper<UserAttendance> ROW_MAPPER = new DataClassRowMapper<>(
            UserAttendance.class);

    public UserAttendanceRepository(final DataSource dataSource) {
        setDataSource(dataSource);
    }

    public void upsertAttendance(final long userId, final UserAttendanceRequest request) {
        final String sql = """
                INSERT INTO user_attendance (user_id, event_id, is_going)
                VALUES (:user_id, :event_id, :is_going)
                ON DUPLICATE KEY UPDATE
                    is_going = :is_going,
                    updated_at = :updated_at
                """;

        var params = new MapSqlParameterSource()
                .addValue("user_id", userId)
                .addValue("event_id", request.eventId())
                .addValue("is_going", request.isGoing())
                .addValue("updated_at", OffsetDateTime.now());

        getNamedParameterJdbcTemplate().update(sql, params);
    }

    public void deleteAttendance(final long userId, final long eventId) {
        final String sql = "DELETE FROM user_attendance WHERE user_id = :user_id AND event_id = :event_id";

        final Map<String, Object> params = Map.of(
                "user_id", userId,
                "event_id", eventId);

        getNamedParameterJdbcTemplate().update(sql, params);
    }

    public List<UserAttendance> getUserAttendance(final long userId) {
        final String sql = """
                SELECT user_id, event_id, is_going, created_at, updated_at
                FROM user_attendance
                WHERE user_id = :user_id
                """;

        final Map<String, Object> params = Map.of("user_id", userId);

        return getNamedParameterJdbcTemplate().query(sql, params, ROW_MAPPER);
    }

    public List<UserAttendance> getEventAttendance(final long eventId) {
        final String sql = """
                SELECT user_id, event_id, is_going, created_at, updated_at
                FROM user_attendance
                WHERE event_id = :event_id
                """;

        final Map<String, Object> params = Map.of("event_id", eventId);

        return getNamedParameterJdbcTemplate().query(sql, params, ROW_MAPPER);
    }

    public Optional<UserAttendance> getAttendance(final long userId, final long eventId) {
        final String sql = """
                SELECT user_id, event_id, is_going, created_at, updated_at
                FROM user_attendance
                WHERE user_id = :user_id AND event_id = :event_id
                """;

        final Map<String, Object> params = Map.of(
                "user_id", userId,
                "event_id", eventId);

        try {
            return Optional.of(Objects.requireNonNull(
                    getNamedParameterJdbcTemplate().queryForObject(sql, params, ROW_MAPPER)));
        } catch (final EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void upsertAttendance(final long userId, final long eventId, final boolean isGoing) {
        final String sql = """
                INSERT INTO user_attendance (user_id, event_id, is_going)
                VALUES (:user_id, :event_id, :is_going)
                ON DUPLICATE KEY UPDATE
                    is_going = :is_going,
                    updated_at = :updated_at
                """;

        var params = new MapSqlParameterSource()
                .addValue("user_id", userId)
                .addValue("event_id", eventId)
                .addValue("is_going", isGoing)
                .addValue("updated_at", OffsetDateTime.now());

        getNamedParameterJdbcTemplate().update(sql, params);
    }
}

