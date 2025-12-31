package com.famevently.monolith.attendance;

import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.stereotype.Repository;

import org.springframework.dao.EmptyResultDataAccessException;

import javax.sql.DataSource;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Repository
public class EventAttendanceStatsRepository extends NamedParameterJdbcDaoSupport {
    private static final DataClassRowMapper<EventAttendanceStats> ROW_MAPPER = new DataClassRowMapper<>(
            EventAttendanceStats.class);

    public EventAttendanceStatsRepository(final DataSource dataSource) {
        setDataSource(dataSource);
    }

    public void deleteAttendanceStats(final long eventId) {
        final String sql = """
                        DELETE FROM event_attendance_stats 
                               WHERE event_id = :event_id""";

        final Map<String, Object> params = Map.of("event_id", eventId);

        getNamedParameterJdbcTemplate().update(sql, params);
    }

    public Optional<EventAttendanceStats> getEventStats(final long eventId) {
        final String sql = """
                SELECT event_id, attendees_count, going_count, updated_at
                FROM event_attendance_stats
                WHERE event_id = :event_id
                """;
        final Map<String, Object> params = Map.of("event_id", eventId);

        try {
            return Optional.of(Objects.requireNonNull(
                    getNamedParameterJdbcTemplate().queryForObject(sql, params, ROW_MAPPER)));
        } catch (final EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void adjustAttendanceCounts(final long eventId,
                                       final AttendanceChange attendeesChange,
                                       final AttendanceChange goingChange) {
        final String sql = """
                INSERT INTO event_attendance_stats (event_id, attendees_count, going_count)
                VALUES (:event_id, GREATEST(0, :attendees_delta), GREATEST(0, :going_delta))
                ON DUPLICATE KEY UPDATE
                    attendees_count = GREATEST(0, attendees_count + :attendees_delta),
                    going_count = GREATEST(0, going_count + :going_delta),
                    updated_at = :updated_at
                """;

        var params = new MapSqlParameterSource()
                .addValue("event_id", eventId)
                .addValue("attendees_delta", attendeesChange.getDelta())
                .addValue("going_delta", goingChange.getDelta())
                .addValue("updated_at", OffsetDateTime.now());

        getNamedParameterJdbcTemplate().update(sql, params);
    }
}

