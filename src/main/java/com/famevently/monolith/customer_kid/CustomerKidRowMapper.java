package com.famevently.monolith.customer_kid;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;

@Component
public class CustomerKidRowMapper implements RowMapper<CustomerKid> {
    @Override
    public CustomerKid mapRow(ResultSet rs, int rowNum) throws SQLException {
        CustomerKid kid = new CustomerKid();

        kid.setId(rs.getLong("kid_id"));
        kid.setCustomerId(rs.getLong("user_id"));
        kid.setFirstName(rs.getString("kid_first_name"));
        kid.setLastName(rs.getString("kid_last_name"));
        kid.setBirthday(rs.getDate("kid_birthday").toLocalDate());
        kid.setGender(rs.getString("kid_gender"));
        kid.setDescription(rs.getString("kid_description"));
        kid.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class));

        return kid;
    }
}
