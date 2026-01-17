package com.famevently.monolith.event;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Map;

@Repository
public class EventCategoriesRepository extends NamedParameterJdbcDaoSupport {


    public EventCategoriesRepository(final DataSource dataSource) {
        setDataSource(dataSource);
    }

    public Long getByName(final EventCategory eventCategory)
    {
        final String sql = "SELECT event_category_id FROM n_event_categories WHERE category_name = :name";
        final var params = Map.of("name", eventCategory.name());

        return getNamedParameterJdbcTemplate().queryForObject(sql, params, Long.class);
    }
}
