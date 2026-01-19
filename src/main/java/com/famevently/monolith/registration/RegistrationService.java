package com.famevently.monolith.registration;

import com.famevently.monolith.customer.CustomerCreationRequest;
import com.famevently.monolith.customer.CustomerService;
import com.famevently.monolith.customer.UserCoreInfo;
import com.famevently.monolith.customer.UserRepository;
import com.famevently.monolith.login.AuthenticationMethod;
import com.famevently.monolith.login.CreateAuthenticatedUserRequest;
import com.famevently.monolith.login.GeneralLoginResponse;
import com.famevently.monolith.login.LoginService;
import com.famevently.monolith.password.UserPasswordService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
public class RegistrationService {
     private final UserRepository userRepository;
    private final UserPasswordService userPasswordService;
    private final CustomerService customerService;
    private final LoginService loginService;

    public RegistrationService(final UserRepository userRepository,
                               final UserPasswordService userPasswordService,
                               final CustomerService customerService,
                               final LoginService loginService) {
        this.userRepository = userRepository;
        this.userPasswordService = userPasswordService;
        this.customerService = customerService;
        this.loginService = loginService;
    }

    public GeneralLoginResponse registerUser(final RegistrationRequest request, final String deviceUuid){

        if (customerService.getCustomerByEmail(request.email()).isPresent()){
            throw new EmailAlreadyUsedException();
        }

        final CustomerCreationRequest customer = CustomerCreationRequest.fromRegistration(request);
        final UserCoreInfo user = userRepository.save(customer).orElseThrow(IllegalStateException::new);
        userPasswordService.savePassword(request.password(), user.userId(), request.email());

        final CreateAuthenticatedUserRequest authenticatedUserRequest = CreateAuthenticatedUserRequest.withoutSession(
                user.userId(),
                deviceUuid,
                user.email(),
                user.firstName(),
                user.lastName(),
                user.language(),
                user.createdAt(),
                true,
                AuthenticationMethod.CREDENTIALS
        );
        return loginService.createAuthenticatedUser(authenticatedUserRequest);
    }
}
