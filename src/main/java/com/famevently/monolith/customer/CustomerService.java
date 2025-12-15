package com.famevently.monolith.customer;


import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Optional<UserCoreInfo> getCustomer(final long userId){
        return customerRepository.getUserById(userId);
    }
}
