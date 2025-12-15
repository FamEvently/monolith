package com.famevently.monolith.login;

import com.famevently.monolith.customer.Customer;
import com.famevently.monolith.customer.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class LoginService {
    private final CustomerService customerService;
    private final PasswordEncoder encoder;


    public LoginService(CustomerService customerService, PasswordEncoder encoder) {
        this.customerService = customerService;
        this.encoder = encoder;
    }

    public Customer login (LoginRequest request){
        Customer customer = customerService.getCustomer(request.getEmail())
                .orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid email or password"));

        if (!encoder.matches(request.getPassword(), customer.getPwdHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid email or password");}

        return customer;
    }
}
