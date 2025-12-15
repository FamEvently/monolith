package com.famevently.monolith.event;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class EventRepository {
    private final JdbcTemplate jdbcTemplate;
    private final EventRowMapper rowMapper = new EventRowMapper();

    public EventRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Event event) {
        String INSERT_SQL = """
        INSERT INTO event (
            user_id,
            event_overview,
            event_category_id,
            event_date,
            event_address,
            event_additional_info,
            min_age,
            max_age,
            is_for_adults_only
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        jdbcTemplate.update(
                INSERT_SQL,
                event.getUserId(),
                event.getOverview(),
                event.getCategoryId(),
                event.getEventDate(),
                event.getAddress(),
                event.getAdditionalInfo(),
                event.getMinAge(),
                event.getMaxAge(),
                event.getForAdultsOnly()
        );
    }


    public Optional<Event> getEvent(Long id) {
        String sql = "SELECT * FROM event WHERE event_id = ?";
        return jdbcTemplate.query(sql, rowMapper, id).stream().findFirst();
    }


    public List<Event> getUserEvents(Long userId) {
        String sql = "SELECT * FROM event WHERE user_id = ? ORDER BY event_date";
        return jdbcTemplate.query(sql, rowMapper, userId);
    }

    public void update(Event event) {
        String UPDATE_SQL = """
        UPDATE event SET
            event_overview = ?,
            event_category_id = ?,
            event_date = ?,
            event_address = ?,
            event_additional_info = ?,
            min_age = ?,
            max_age = ?,
            is_for_adults_only = ?
        WHERE event_id = ?
        """;
        jdbcTemplate.update(
                UPDATE_SQL,
                event.getOverview(),
                event.getCategoryId(),
                event.getEventDate(),
                event.getAddress(),
                event.getAdditionalInfo(),
                event.getMinAge(),
                event.getMaxAge(),
                event.getForAdultsOnly(),
                event.getId()
        );
    }


    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM event WHERE event_id = ?", id);
    }
}
