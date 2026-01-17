package com.famevently.monolith.event;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Repository
public class EventRepository extends NamedParameterJdbcDaoSupport {

    private static final DataClassRowMapper<Event> ROW_MAPPER = new DataClassRowMapper<>(
            Event.class);
    private final EventCategoriesRepository eventCategoriesRepository;

    public EventRepository(final DataSource dataSource, final EventCategoriesRepository eventCategoriesRepository) {
        setDataSource(dataSource);
        this.eventCategoriesRepository = eventCategoriesRepository;
    }

    public Optional<Event> save(final CreateEventRequest event, final Long userId) {
        final String sql = """
                INSERT INTO event (
                user_id, overview, event_category_id, date, address, location, additional_info, min_age, max_age, adults_only, created_at
                ) VALUES (:user_id, :overview, :event_category_id, :date, :address, :location, :additional_info, :min_age, :max_age, :adults_only, :created_at)
                """;

        var params = new MapSqlParameterSource()
                .addValue("user_id", userId)
                .addValue("overview", event.overview())
                .addValue("event_category_id", eventCategoriesRepository.getByName(event.category()))
                .addValue("date", event.eventDate())
                .addValue("address", event.address())
                .addValue("location", event.location())
                .addValue("additional_info", event.additionalInfo())
                .addValue("min_age", event.minAge())
                .addValue("max_age", event.maxAge())
                .addValue("adults_only", event.adultsOnly())
                .addValue("created_at", OffsetDateTime.now());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        getNamedParameterJdbcTemplate().update(sql, params, keyHolder);
        return getEventById(keyHolder.getKey().longValue());
    }

    public Optional<Event> getEventById(final long eventId)
    {
        final String sql = """
        SELECT 
            e.event_id, 
            e.user_id, 
            e.overview, 
            ec.category_name AS event_category, 
            e.date, 
            e.address, 
            e.location,
            e.additional_info, 
            e.min_age, 
            e.max_age, 
            e.adults_only, 
            e.created_at 
        FROM event e
        JOIN n_event_categories ec ON e.event_category_id = ec.event_category_id
        WHERE e.event_id = :event_id
    """;
        final Map<String, Object> params = Map.of("event_id", eventId);

        try
        {
            return Optional.of(Objects.requireNonNull(getNamedParameterJdbcTemplate().queryForObject(sql, params, ROW_MAPPER)));
        } catch (final EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
    public final Optional<Event> getEventByForOrganizer(final long eventId, final long userId)
    {
        final String sql = """
        SELECT 
            e.event_id, 
            e.user_id, 
            e.overview, 
            ec.category_name AS event_category, 
            e.date, 
            e.address, 
            e.location,
            e.additional_info, 
            e.min_age, 
            e.max_age, 
            e.adults_only, 
            e.created_at 
        FROM event e
        JOIN n_event_categories ec ON e.event_category_id = ec.event_category_id
        WHERE e.event_id = :event_id
        AND e.user_id = :user_id
    """;
        final Map<String, Object> params = Map.of("event_id", eventId,
                "user_id", userId);

        try
        {
            return Optional.of(Objects.requireNonNull(getNamedParameterJdbcTemplate().queryForObject(sql, params, ROW_MAPPER)));
        } catch (final EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    final List<Event> getEventsByCategory(final EventCategory categoryName)
    {
        final String sql = """
        SELECT 
            e.event_id, 
            e.user_id, 
            e.overview, 
            ec.category_name AS event_category, 
            e.date, 
            e.address, 
            e.location,
            e.additional_info, 
            e.min_age, 
            e.max_age, 
            e.adults_only, 
            e.created_at 
        FROM event e
        JOIN n_event_categories ec ON e.event_category_id = ec.event_category_id
        WHERE ec.category_name = :category_name
        """;
        final Map<String, Object> params = Map.of("category_name", categoryName.name());

        return getNamedParameterJdbcTemplate().query(sql, params, ROW_MAPPER);
    }

    final List<Event> getEventsForUser(final long userId)
    {
        final String sql = """
        SELECT 
            e.event_id, 
            e.user_id, 
            e.overview, 
            ec.category_name AS event_category, 
            e.date, 
            e.address, 
            e.location,
            e.additional_info, 
            e.min_age, 
            e.max_age, 
            e.adults_only, 
            e.created_at 
        FROM event e
        JOIN n_event_categories ec ON e.event_category_id = ec.event_category_id
        WHERE user_id = :user_id
        """;
        final Map<String, Object> params = Map.of("user_id", userId);

        return getNamedParameterJdbcTemplate().query(sql, params, ROW_MAPPER);
    }

    final List<Event> getEventsForLocation(final String location)
    {
        final String sql = """
        SELECT 
            e.event_id, 
            e.user_id, 
            e.overview, 
            ec.category_name AS event_category, 
            e.date, 
            e.address, 
            e.location,
            e.additional_info, 
            e.min_age, 
            e.max_age, 
            e.adults_only, 
            e.created_at 
        FROM event e
        JOIN n_event_categories ec ON e.event_category_id = ec.event_category_id
        WHERE e.location = :location
        """;
        final Map<String, Object> params = Map.of("location", location);

        return getNamedParameterJdbcTemplate().query(sql, params, ROW_MAPPER);
    }

    final void deleteEvent(final long eventId, final Long userId)
    {
        final String sql = "DELETE FROM event WHERE event_id = :event_id " +
                "AND user_id = :user_id";
        final Map<String, Object> params = Map.of("event_id", eventId,
                "user_id", userId);

        getNamedParameterJdbcTemplate().update(sql, params);
    }

    final void deleteEventsForUser(final long userId)
    {
        final String sql = "DELETE FROM event WHERE user_id = :user_id";
        final Map<String, Object> params = Map.of("user_id", userId);

        getNamedParameterJdbcTemplate().update(sql, params);
    }
}
