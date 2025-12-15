package com.famevently.monolith.login;

import com.famevently.monolith.customer.CustomerCreationRequest;
import com.famevently.monolith.customer.CustomerService;
import com.famevently.monolith.customer.UserCoreInfo;
import com.famevently.monolith.password.AuthenticatedPassword;
import com.famevently.monolith.password.UserPassword;
import com.famevently.monolith.password.UserPasswordService;
import org.apache.catalina.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class LoginService {
    private final CustomerService customerService;
    private final UserPasswordService userPasswordService;


    public LoginService(final CustomerService customerService, final PasswordEncoder encoder, final UserPasswordService userPasswordService) {
        this.customerService = customerService;
        this.userPasswordService = userPasswordService;
    }

    public UserCoreInfo login(final LoginRequest request){
        final AuthenticatedPassword userPassword = userPasswordService.validatePassword(request.email(), request.password());

        final Optional<UserCoreInfo> userInfo = customerService.getCustomer(userPassword.userId());

        if (userInfo.isEmpty()) {
            throw new IllegalStateException("User not found");
        }

        return userInfo.get();
    }
}
