package com.famevently.monolith.password;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Optional;

@Repository
public class PasswordRepository extends NamedParameterJdbcDaoSupport
{
    private static final DataClassRowMapper<UserPassword> ROW_MAPPER = new DataClassRowMapper<>(
            UserPassword.class);
    public PasswordRepository(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public void save(final UserPassword userPasswordEntity)
    {
        String sql = "INSERT INTO `user_passwords` " +
                "( `user_id`, `email`, `password_hash` ) " +
                "VALUES (:user_id, :email, :password_hash)  " +
                "ON DUPLICATE KEY UPDATE user_id = VALUES(user_id) ";
        Map<String, Object> params = Map.of("user_id",
                userPasswordEntity.userId(),
                "email",
                userPasswordEntity.email(),
                "password_hash",
                userPasswordEntity.passwordHash());

        getNamedParameterJdbcTemplate().update(sql, params);
    }

    public Optional<UserPassword> getByEmail(final String email)
    {
        String sql = "SELECT * FROM `user_passwords`" +
                " WHERE `email` = :email";

        Map<String, Object> params = Map.of("email", email);

        try
        {
            return Optional.ofNullable(getNamedParameterJdbcTemplate().queryForObject(sql, params, ROW_MAPPER));
        } catch (EmptyResultDataAccessException e)
        {
            return Optional.empty();
        }

    }
}
