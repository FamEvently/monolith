package com.famevently.monolith.customer;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Component
public class CustomerRowMapper implements RowMapper<Customer> {

    @Override
    public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
        Customer customer = new Customer();
        customer.setUserId(rs.getLong("user_id"));
        customer.setEmail(rs.getString("email"));
        customer.setIsGuest(rs.getBoolean("is_guest"));
        customer.setFirstName(rs.getString("first_name"));
        customer.setLastName(rs.getString("last_name"));
        customer.setPwdHash(rs.getString("pwd_hash"));
        customer.setLanguage(rs.getString("language"));
        customer.setGender(rs.getString("gender"));
        customer.setBirthday(rs.getObject("birthday", LocalDate.class));
        customer.setIsWhitelisted(rs.getBoolean("is_whitelisted"));
        customer.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class));
        return customer;
    }
}
