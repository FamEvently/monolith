package com.famevently.monolith.login;

import com.famevently.monolith.customer.CustomerService;
import com.famevently.monolith.customer.UserCoreInfo;
import com.famevently.monolith.password.AuthenticatedPassword;
import com.famevently.monolith.password.UserPasswordService;
import com.famevently.monolith.usersession.UserSession;
import com.famevently.monolith.usersession.UserSessionRestClient;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static java.util.Objects.isNull;

@Service
public class LoginService {
    private final CustomerService customerService;
    private final UserPasswordService userPasswordService;
    private final GoogleTokenVerifier googleTokenVerifier;
    private final UserSessionRestClient userSessionRestClient;

    public LoginService(final CustomerService customerService,
                        final UserPasswordService userPasswordService,
                        final GoogleTokenVerifier googleTokenVerifier, final UserSessionRestClient userSessionRestClient) {
        this.customerService = customerService;
        this.userPasswordService = userPasswordService;
        this.googleTokenVerifier = googleTokenVerifier;
        this.userSessionRestClient = userSessionRestClient;
    }

    public GeneralLoginResponse loginWithCredentials(final LoginRequest request, final String deviceUuid) {
        final AuthenticatedPassword userPassword = userPasswordService.validatePassword(request.email(), request.password());

        final Optional<UserCoreInfo> userInfoOpt = customerService.getCustomer(userPassword.userId());

        if (userInfoOpt.isEmpty()) {
            throw new IllegalStateException("User not found");
        }

        final UserCoreInfo userInfo = userInfoOpt.get();

        final CreateAuthenticatedUserRequest authenticatedUserRequest = CreateAuthenticatedUserRequest.withoutSession(userInfo.userId(),
                deviceUuid,
                userInfo.email(),
                userInfo.language(),
                userInfo.createdAt(),
                request.rememberMe(),
                AuthenticationMethod.CREDENTIALS);


        return createAuthenticatedUser(authenticatedUserRequest);
    }

    public GeneralLoginResponse loginWithSession(final String sessionId, final String deviceUuid) {
        if (isNull(sessionId))
        {
            throw new IllegalArgumentException("Session ID cannot be null");
        }

        final Optional<UserSession> session = userSessionRestClient.validateSession(sessionId, deviceUuid);

        if (session.isEmpty()) {
            throw new IllegalStateException("Session not found");
        }

        final Optional<UserCoreInfo> userInfoOpt = customerService.getCustomer(session.get().userId());

        if (userInfoOpt.isEmpty()) {
            throw new IllegalStateException("User not found");
        }

        final UserCoreInfo userInfo = userInfoOpt.get();

        final CreateAuthenticatedUserRequest authenticatedUserRequest = new CreateAuthenticatedUserRequest(userInfo.userId(),
                deviceUuid,
                userInfo.email(),
                userInfo.language(),
                userInfo.createdAt(),
                false,
                AuthenticationMethod.SESSION,
                sessionId);

        return createAuthenticatedUser(authenticatedUserRequest);
    }

    public GeneralLoginResponse googleLogin(final GoogleLoginRequest request, String deviceUuid) {
        final String email = googleTokenVerifier.verifyAndGetEmail(request.token());
        final Optional<UserCoreInfo> userInfoOpt = customerService.getCustomerByEmail(email);

        if (userInfoOpt.isEmpty()) {
            throw new IllegalStateException("User not found");
        }

        final UserCoreInfo userInfo = userInfoOpt.get();

        final CreateAuthenticatedUserRequest authenticatedUserRequest = CreateAuthenticatedUserRequest.withoutSession(userInfo.userId(),
                deviceUuid,
                userInfo.email(),
                userInfo.language(),
                userInfo.createdAt(),
                request.rememberMe(),
                AuthenticationMethod.CREDENTIALS);


        return createAuthenticatedUser(authenticatedUserRequest);
    }

    public GeneralLoginResponse createAuthenticatedUser(final CreateAuthenticatedUserRequest request)
    {
        final String sessionId = getSessionId(request);

        final GeneralLoginResponse.Builder responseBuilder = new GeneralLoginResponse.Builder(
                request.userId(),
                request.email(),
                request.language(),
                request.createdAt(),
                sessionId,
                request.authenticationMethod()
        );

        if (request.rememberMe())
        {
            //persist login token
            //responseBuilder.loginToken(loginToken);
        }

        return responseBuilder.build();
    }

    private String getSessionId(final CreateAuthenticatedUserRequest request) {
        if (isNull(request.sessionId())) {
            final UserSession session = userSessionRestClient.createSession(request.userId(), false);
            return session.sessionId();
        }
        return request.sessionId();
    }

}
