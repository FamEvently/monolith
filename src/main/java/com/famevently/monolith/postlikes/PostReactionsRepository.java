package com.famevently.monolith.postlikes;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Map;

@Repository
public class PostReactionsRepository extends NamedParameterJdbcDaoSupport {

    public PostReactionsRepository(final DataSource dataSource) {
        setDataSource(dataSource);
    }

    public Long getIdByReaction(final ReactionType reactionType) {
        final String sql = "SELECT reaction_id FROM n_post_reactions WHERE reaction_name = :reaction_name";
        final Map<String, Object> params = Map.of("reaction_name", reactionType.name());

        return getNamedParameterJdbcTemplate().queryForObject(sql, params, Long.class);
    }
}

