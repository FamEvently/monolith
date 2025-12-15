package com.famevently.monolith.customer_kid;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CustomerKidRepository {
    private final JdbcTemplate jdbcTemplate;
    private final CustomerKidRowMapper mapper;

    public CustomerKidRepository(JdbcTemplate jdbcTemplate, CustomerKidRowMapper mapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.mapper = mapper;
    }

    public void save(CustomerKid kid){
        String sql = """
            INSERT INTO customer_kids (customerId, first_name, last_name, gender, birthday, description)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        jdbcTemplate.update(
                sql,
                kid.getCustomerId(),
                kid.getFirstName(),
                kid.getLastName(),
                kid.getGender(),
                kid.getBirthday(),
                kid.getDescription()
        );
    }
}
