package com.famevently.monolith.event;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public class EventRowMapper  implements RowMapper<Event> {

    @Override
    public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
        Event event = new Event();

        event.setId(rs.getLong("event_id"));
        event.setUserId(rs.getLong("user_id"));
        event.setOverview(rs.getString("event_overview"));
        event.setCategoryId(rs.getLong("event_category_id"));
        event.setEventDate(rs.getObject("event_date", LocalDate.class));
        event.setAddress(rs.getString("event_address"));
        event.setAdditionalInfo(rs.getString("event_additional_info"));
        event.setMinAge(rs.getObject("min_age", Integer.class));
        event.setMaxAge(rs.getObject("max_age", Integer.class));
        event.setForAdultsOnly(rs.getBoolean("is_for_adults_only"));
        event.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class));

        return event;
    }
}
