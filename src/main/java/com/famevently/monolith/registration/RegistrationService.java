package com.famevently.monolith.registration;

import com.famevently.monolith.customer.CustomerCreationRequest;
import com.famevently.monolith.customer.CustomerRepository;
import com.famevently.monolith.password.UserPasswordService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RegistrationService {
     private final CustomerRepository customerRepository;
    private final UserPasswordService userPasswordService;

    public RegistrationService(final CustomerRepository customerRepository, final PasswordEncoder passwordEncoder, final UserPasswordService userPasswordService) {
        this.customerRepository = customerRepository;
        this.userPasswordService = userPasswordService;
    }

    public CustomerCreationRequest register(final RegistrationRequest request){

        if (customerRepository.getUserByEmail(request.email()).isPresent()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already used");
        }

        final CustomerCreationRequest customer = CustomerCreationRequest.fromRegistration(request);
        final long userId = customerRepository.save(customer);
        userPasswordService.savePassword(request.password(), userId, request.email());

        return customer;
    }
}
