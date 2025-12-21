package com.famevently.monolith.login;

import com.famevently.monolith.customer.CustomerService;
import com.famevently.monolith.customer.UserCoreInfo;
import com.famevently.monolith.password.AuthenticatedPassword;
import com.famevently.monolith.password.UserPasswordService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService {
    private final CustomerService customerService;
    private final UserPasswordService userPasswordService;
    private final GoogleTokenVerifier googleTokenVerifier;

    public LoginService(final CustomerService customerService,
                        final UserPasswordService userPasswordService,
                        final GoogleTokenVerifier googleTokenVerifier) {
        this.customerService = customerService;
        this.userPasswordService = userPasswordService;
        this.googleTokenVerifier = googleTokenVerifier;
    }

    public UserCoreInfo login(final LoginRequest request) {
        final AuthenticatedPassword userPassword = userPasswordService.validatePassword(request.email(), request.password());

        final Optional<UserCoreInfo> userInfo = customerService.getCustomer(userPassword.userId());

        if (userInfo.isEmpty()) {
            throw new IllegalStateException("User not found");
        }

        return userInfo.get();
    }

    public UserCoreInfo googleLogin(final GoogleLoginRequest request) {
        final String email = googleTokenVerifier.verifyAndGetEmail(request.token());
        final Optional<UserCoreInfo> userInfo = customerService.getCustomerByEmail(email);

        if (userInfo.isPresent()) {
            return userInfo.get();
        }

        //register new user
        return null;
    }
}
