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

    public Long getByName(final String name)
    {
        final String sql = "SELECT id FROM n_event_categories WHERE name = :name";
        final var params = Map.of("name", name);

        return getNamedParameterJdbcTemplate().queryForObject(sql, params, Long.class);
    }
}
