package com.famevently.monolith.registration;

import com.famevently.monolith.customer.CustomerCreationRequest;
import com.famevently.monolith.customer.UserCoreInfo;
import com.famevently.monolith.customer.UserRepository;
import com.famevently.monolith.password.UserPasswordService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class RegistrationService {
     private final UserRepository userRepository;
    private final UserPasswordService userPasswordService;

    public RegistrationService(final UserRepository userRepository, final UserPasswordService userPasswordService) {
        this.userRepository = userRepository;
        this.userPasswordService = userPasswordService;
    }

    public CustomerCreationRequest register(final RegistrationRequest request){

        if (userRepository.getUserByEmail(request.email()).isPresent()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already used");
        }

        final CustomerCreationRequest customer = CustomerCreationRequest.fromRegistration(request);
        final UserCoreInfo user = userRepository.save(customer).orElseThrow(IllegalStateException::new);
        userPasswordService.savePassword(request.password(), user.userId(), request.email());

        return customer;
    }
}
