package com.famevently.monolith.message;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class MessageRepository extends NamedParameterJdbcDaoSupport {


    public MessageRepository(final DataSource dataSource) {
        setDataSource(dataSource);
    }

    public String upsert(final CreateMessageRequest request)
    {
        String sql = """
            INSERT INTO message (
                message_id, user_id, event_id, description, image_count, created_at, updated_at
            ) VALUES (
                :message_id, :user_id, :event_id, :description, :image_count, :created_at, :updated_at
            ) 
            ON DUPLICATE KEY UPDATE 
                description = :description, 
                image_count = :image_count, 
                updated_at = :updated_at
            """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("message_id", request.messageId());
        params.addValue("user_id", request.userId());
        params.addValue("event_id", request.eventId());
        params.addValue("description", request.description());
        params.addValue("image_count", request.imageCount());
        params.addValue("created_at", java.time.OffsetDateTime.now());
        params.addValue("updated_at", java.time.OffsetDateTime.now());

        getNamedParameterJdbcTemplate().update(sql, params);

        return request.messageId();
    }
}


//UUID message_id PK
//        TIMESTAMPTZ timestamp
//        BIGINT user_id FK "FK -> user(user_id), NOT NULL, ON DELETE CASCADE"
//        BIGINT event_id FK "FK (event_id, user_id) -> event(event_id, user_id), NULL"
//        TEXT description
//        INTEGER image_count