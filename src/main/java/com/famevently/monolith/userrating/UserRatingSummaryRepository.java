package com.famevently.monolith.userrating;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.stereotype.Repository;

@Repository
public class UserRatingSummaryRepository extends NamedParameterJdbcDaoSupport {

    public UserRatingSummaryRepository(final DataSource dataSource) {
        setDataSource(dataSource);
    }

    public UserRatingSummary getUserRatingSummary(long ratedUserId) {
        final String sql = """
            SELECT 
                rated_user_id AS id,
                COUNT(*) AS totalRatings,
                AVG(score) AS averageScore
            FROM user_rating
            WHERE rated_user_id = :rated_user_id
            GROUP BY rated_user_id
            """;

        var params = Map.of("rated_user_id", ratedUserId);

        return getNamedParameterJdbcTemplate().queryForObject(sql, params,
                (rs, rowNum) -> new UserRatingSummary(
                        rs.getLong("id"),
                        rs.getLong("totalRatings"),
                        rs.getBigDecimal("averageScore")
                )
        );
    }

    public UserRatingSummary getEventRatingSummary(long eventId) {
        final String sql = """
            SELECT 
                event_id AS id,
                COUNT(*) AS totalRatings,
                AVG(score) AS averageScore
            FROM user_rating
            WHERE event_id = :event_id
            GROUP BY event_id
            """;

        var params = Map.of("event_id", eventId);

        return getNamedParameterJdbcTemplate().queryForObject(sql, params,
                (rs, rowNum) -> new UserRatingSummary(
                        rs.getLong("id"),
                        rs.getLong("totalRatings"),
                        rs.getBigDecimal("averageScore")
                )
        );
    }
}
