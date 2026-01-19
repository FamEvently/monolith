package com.famevently.monolith.logintoken;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.stereotype.Repository;

@Repository
public class LoginTokenRepository extends NamedParameterJdbcDaoSupport {
    
    public LoginTokenRepository(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public LoginToken upsert(final long userId) {
        final String loginToken = UUID.randomUUID().toString();
        final OffsetDateTime now = OffsetDateTime.now();

        final String sql = """
            INSERT INTO login_token (user_id, login_token, created_at, updated_at)
            VALUES (:user_id, :login_token, :created_at, :updated_at)
            ON DUPLICATE KEY UPDATE
                updated_at = :updated_at
            """;

        final Map<String, Object> params = Map.of(
            "user_id", userId,
            "login_token", loginToken,
            "created_at", now,
            "updated_at", now
        );

        getNamedParameterJdbcTemplate().update(sql, params);

        return new LoginToken(userId, loginToken, now, now);
    }

    public void deleteToken(final String loginToken) {
        final String sql = """
            DELETE FROM login_token
            WHERE login_token = :login_token
            """;

        final Map<String, Object> params = Map.of("login_token", loginToken);

        getNamedParameterJdbcTemplate().update(sql, params);
    }


    public Optional<LoginToken> getLoginToken(final String loginToken) {
        final String sql = """
            SELECT user_id, login_token, created_at, updated_at
            FROM login_token
            WHERE login_token = :login_token
            """;

        var params = Map.of("login_token", loginToken);

        try {
            return Optional.of(getNamedParameterJdbcTemplate().queryForObject(sql, params,
                    (rs, rowNum) -> new LoginToken(
                            rs.getLong("user_id"),
                            rs.getString("login_token"),
                            rs.getObject("created_at", OffsetDateTime.class),
                            rs.getObject("updated_at", OffsetDateTime.class)
                    )
            ));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<LoginToken> getLoginTokenByUserId(final long userId) {
        final String sql = """
            SELECT user_id, login_token, created_at, updated_at
            FROM login_token
            WHERE user_id = :user_id
            """;

        var params = Map.of("user_id", userId);

        try {
            return Optional.of(getNamedParameterJdbcTemplate().queryForObject(sql, params,
                    (rs, rowNum) -> new LoginToken(
                            rs.getLong("user_id"),
                            rs.getString("login_token"),
                            rs.getObject("created_at", OffsetDateTime.class),
                            rs.getObject("updated_at", OffsetDateTime.class)
                    )
            ));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void deleteTokenByUserId(long userId) {
        final String sql = """
            DELETE FROM login_token
            WHERE user_id = :user_id
        """;

        var params = Map.of("user_id", userId);
        getNamedParameterJdbcTemplate().update(sql, params);
    }
}
