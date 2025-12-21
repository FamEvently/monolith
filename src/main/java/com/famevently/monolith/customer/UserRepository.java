package com.famevently.monolith.customer;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;

import javax.sql.DataSource;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Repository
public class UserRepository extends NamedParameterJdbcDaoSupport
{
    private static final DataClassRowMapper<UserCoreInfo> ROW_MAPPER = new DataClassRowMapper<>(
            UserCoreInfo.class);
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public UserRepository(final DataSource datasource) {
       setDataSource(datasource);
       this.namedParameterJdbcTemplate = getNamedParameterJdbcTemplate();
    }

    public Optional<UserCoreInfo> save(CustomerCreationRequest request) {
        String sql = "INSERT INTO users (email, first_name, last_name, language, gender, birthday, country, is_whitelisted, created_at)" +
                " VALUES (:email, :first_name, :last_name, :language, gender, :birthday, :country, :is_whitelisted, :created_at)";

        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("email", request.email());
        params.addValue("first_name", request.firstName());
        params.addValue("last_name", request.lastName());
        params.addValue("language", request.language());
        params.addValue("gender", request.gender());
        params.addValue("birthday", request.birthday());
        params.addValue("country", request.countryOfResidence());
        params.addValue("is_whitelisted", request.isWhitelisted());
        params.addValue("created_at", OffsetDateTime.now());

        final KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, params, keyHolder);
        final long userId = Objects.requireNonNull(keyHolder.getKey()).longValue();

        return getUserById(userId);
    }

    public Optional<UserCoreInfo> getUserById(final long userId) {
        String sql = "SELECT * FROM users WHERE user_id = :user_id";

        final Map<String, Object> args = Map.of("user_id", userId);

        try
        {
            return Optional.of(Objects.requireNonNull(namedParameterJdbcTemplate.queryForObject(sql, args, ROW_MAPPER)));
        } catch (final EmptyResultDataAccessException e)
        {
            return Optional.empty();
        }
    }
    public Optional<UserCoreInfo> getUserByEmail(final String email) {
        String sql = "SELECT * FROM users WHERE email = :email";

        final Map<String, Object> args = Map.of("email", email);

        try
        {
            return Optional.of(Objects.requireNonNull(namedParameterJdbcTemplate.queryForObject(sql, args, ROW_MAPPER)));
        } catch (final EmptyResultDataAccessException e)
        {
            return Optional.empty();
        }
    }
}


// {
//        BIGINT user_id PK "PK, AUTO_INCREMENT, NOT NULL"
//        VARCHAR_255 email UK "UNIQUE, NOT NULL, CHECK(email LIKE '%@%')"
//        VARCHAR_100 first_name "NOT NULL, CHECK(LENGTH(first_name) >= 1)"
//        VARCHAR_100 last_name "NOT NULL, CHECK(LENGTH(last_name) >= 1)"
//        TEXT pwd_hash "NOT NULL, CHECK(LENGTH(pwd_hash) >= 60)"
//        VARCHAR_10 language "NOT NULL, DEFAULT 'en', CHECK(LENGTH(language) = 2)"
//        VARCHAR_20 gender "NULL, CHECK(gender IN ('male', 'female', 'other', 'unspecified'))"
//        DATE birthday "NULL, CHECK(birthday <= CURRENT_DATE)"
//        BOOLEAN is_whitelisted "NOT NULL, DEFAULT FALSE"
//        TIMESTAMPTZ created_at "NOT NULL, DEFAULT CURRENT_TIMESTAMP"