package com.famevently.monolith.moderateevents;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.stereotype.Repository;

@Repository
public class ModerationReasonRespository extends NamedParameterJdbcDaoSupport {

    public ModerationReasonRespository(DataSource dataSource) {
        setDataSource(dataSource);
    }
    
    public long findByName(ModerationReason reason) {
        final String sql = """
            SELECT moderate_event_reason_id
            FROM n_moderate_event_reason
            WHERE reason_name = :name
            """;

        final Map<String, Object> params = Map.of(
            "name", reason.name()
        );

        return getNamedParameterJdbcTemplate().queryForObject(sql, params, Long.class);
    }
}
