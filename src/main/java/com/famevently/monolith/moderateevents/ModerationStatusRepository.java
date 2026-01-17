package com.famevently.monolith.moderateevents;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.stereotype.Repository;

@Repository
public class ModerationStatusRepository extends NamedParameterJdbcDaoSupport {
    
    public ModerationStatusRepository(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public long findByName(ModerationStatus status) {
        final String sql = """
            SELECT moderate_event_status_id
            FROM n_moderate_event_status
            WHERE status_name = :name
            """;

        final Map<String, Object> params = Map.of(
            "name", status.name()
        );

        return getNamedParameterJdbcTemplate().queryForObject(sql, params, Long.class);
    }
}
