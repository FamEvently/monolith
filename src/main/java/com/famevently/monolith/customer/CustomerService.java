package com.famevently.monolith.customer;


import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerService {

    private final UserRepository userRepository;

    public CustomerService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<UserCoreInfo> getCustomer(final long userId){
        return userRepository.getUserById(userId);
    }
}
