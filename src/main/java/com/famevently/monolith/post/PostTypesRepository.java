package com.famevently.monolith.post;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Map;

@Repository
public class PostTypesRepository extends NamedParameterJdbcDaoSupport {

    public PostTypesRepository(final DataSource dataSource) {
        setDataSource(dataSource);
    }

    public Long getIdByType(final PostType postType) {
        final String sql = "SELECT post_type_id FROM n_post_types WHERE type_name = :type_name";
        final Map<String, Object> params = Map.of("type_name", postType.name());

        return getNamedParameterJdbcTemplate().queryForObject(sql, params, Long.class);
    }
}

