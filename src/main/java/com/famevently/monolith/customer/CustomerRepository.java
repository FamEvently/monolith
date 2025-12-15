package com.famevently.monolith.customer;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CustomerRepository {
    private final JdbcTemplate jdbcTemplate;
    private final CustomerRowMapper mapper;

    public CustomerRepository(JdbcTemplate jdbcTemplate, CustomerRowMapper mapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.mapper = mapper;
    }

    public Optional<Customer> getCustomer(String email){
        String sql = "SELECT * FROM user WHERE email = ?";
        return jdbcTemplate.query(sql, mapper, email).stream().findFirst();
    }
    public void save(Customer customer) {
        String sql = """
            INSERT INTO user (email, first_name, last_name, pwd_hash, language, gender, birthday)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        jdbcTemplate.update(
                sql,
                customer.getEmail(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getPwdHash(),
                customer.getLanguage(),
                customer.getGender(),
                customer.getBirthday()
        );
    }
}
