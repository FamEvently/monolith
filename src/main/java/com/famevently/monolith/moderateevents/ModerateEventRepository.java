package com.famevently.monolith.moderateevents;

import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Repository
public class ModerateEventRepository extends NamedParameterJdbcDaoSupport {

    private static final DataClassRowMapper<ModerateEvent> ROW_MAPPER = new DataClassRowMapper<>(ModerateEvent.class);
        private final ModerationReasonRespository moderationReasonRepository;
        private final ModerationStatusRepository moderationStatusRepository;
    
        public ModerateEventRepository(
            DataSource dataSource, 
            ModerationReasonRespository moderationReasonRepository,
            ModerationStatusRepository moderationStatusRepository
        ) {
            setDataSource(dataSource);
            this.moderationReasonRepository = moderationReasonRepository;
            this.moderationStatusRepository = moderationStatusRepository;
        }

      public List<ModerateEvent> getEventModerations(Long eventId) {
        final String sql = """
            SELECT
                moderation_id,
                event_id,
                moderation_status_id,
                moderation_reason_id,
                created_at,
                updated_at
            FROM moderate_event
            WHERE event_id = :event_id
            ORDER BY created_at ASC
            """;

        final Map<String, Object> params = Map.of("event_id", eventId);

        return getNamedParameterJdbcTemplate().query(sql, params, ROW_MAPPER);
    }

    public void insert(
        long eventId,
        ModerationStatus status,
        ModerationReason reason,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
    ) {
        final String sql = """
            INSERT INTO moderate_event (
                event_id,
                moderation_status_id,
                moderation_reason_id,
                created_at,
                updated_at
            ) VALUES (
                :event_id,
                :moderation_status_id,
                :moderation_reason_id,
                :created_at,
                :updated_at
            )
            """;

        final Map<String, Object> params = Map.of(
            "event_id", eventId,
            "moderation_status_id", moderationStatusRepository.findByName(status),
            "moderation_reason_id", moderationReasonRepository.findByName(reason),
            "created_at", createdAt,
            "updated_at", updatedAt
        );

        getNamedParameterJdbcTemplate().update(sql, params);
    }

    public void delete(long moderationId) {
        final String sql = "DELETE FROM moderate_event WHERE moderation_id = :moderation_id";

        final Map<String, Object> params = Map.of(
            "moderation_id", moderationId
        );

        getNamedParameterJdbcTemplate().update(sql, params);
    }

    public List<ModerateEvent> getPendingModerations() {
        final String sql = """
            SELECT moderation_id,
                me.event_id,
                me.moderation_status_id as status,
                me.moderation_reason_id as reason,
                me.created_at,
                me.updated_at
            FROM moderate_event me
            JOIN n_moderate_event_status s
            ON me.moderation_status_id = s.moderate_event_status_id
            WHERE s.status_name = :status
            ORDER BY me.created_at ASC
        """;

        final Map<String, Object> params = Map.of(
            "status", ModerationStatus.PENDING.name()
        );

        return getNamedParameterJdbcTemplate().query(sql, params, ROW_MAPPER);
    }
}
