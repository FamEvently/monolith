package com.famevently.monolith.moderate_events;

import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;

import javax.sql.DataSource;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public class ModerateEventRepository extends NamedParameterJdbcDaoSupport {

    private static final DataClassRowMapper<ModerateEvent> ROW_MAPPER = new DataClassRowMapper<>(ModerateEvent.class);

    public ModerateEventRepository(final DataSource dataSource) {
        setDataSource(dataSource);
    }

      public List<ModerateEvent> getEventModerations(Long eventId) {
        final String sql = """
            SELECT
                moderation_id,
                event_id,
                moderation_status,
                moderation_reason,
                created_at,
                updated_at
            FROM event_moderation
            WHERE event_id = :event_id
            ORDER BY created_at ASC
            """;

        final Map<String, Object> params = Map.of("event_id", eventId);

        return getNamedParameterJdbcTemplate().query(sql, params, ROW_MAPPER);
    }

    public void insert(
        long moderationId,
        long eventId,
        ModerationStatus status,
        ModerationReason reason,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
    ) {
        final String sql = """
            INSERT INTO event_moderation (
                event_id,
                moderation_status,
                moderation_reason,
                created_at,
                updated_at
            ) VALUES (
                :event_id,
                :moderation_status,
                :moderation_reason,
                :created_at,
                :updated_at
            )
            """;

        final Map<String, Object> params = Map.of(
            "event_id", eventId,
            "moderation_status", status.name(),
            "moderation_reason", reason.name(),
            "created_at", createdAt,
            "updated_at", updatedAt
        );

        getNamedParameterJdbcTemplate().update(sql, params);
    }

    public void delete(long moderationId) {
        final String sql = "DELETE FROM event_moderation WHERE moderationId = :moderation_id";

        final Map<String, Object> params = Map.of(
            "moderation_id", moderationId
        );

        getNamedParameterJdbcTemplate().update(sql, params);
    }

     public List<ModerateEvent> getPendingModerations() {
        final String sql = """
            SELECT
                moderation_id,
                event_id,
                moderation_status,
                moderation_reason,
                created_at,
                updated_at
            FROM event_moderation
            WHERE moderation_status = :status
            ORDER BY created_at ASC
            """;

        final Map<String, Object> params = Map.of(
            "status", ModerationStatus.PENDING.name()
        );

        return getNamedParameterJdbcTemplate().query(sql, params, ROW_MAPPER);
    }
}