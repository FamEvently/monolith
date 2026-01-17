package com.famevently.monolith.login;

import com.famevently.monolith.customer.CustomerNotFoundException;
import com.famevently.monolith.customer.CustomerService;
import com.famevently.monolith.customer.UserCoreInfo;
import com.famevently.monolith.logintoken.LoginToken;
import com.famevently.monolith.logintoken.LoginTokenService;
import com.famevently.monolith.password.AuthenticatedPassword;
import com.famevently.monolith.password.UserPasswordService;
import com.famevently.monolith.usersession.UserSession;
import com.famevently.monolith.usersession.UserSessionRestClient;
import org.springframework.stereotype.Service;

import static java.util.Objects.isNull;

@Service
public class LoginService {
    private final CustomerService customerService;
    private final UserPasswordService userPasswordService;
    private final GoogleTokenVerifier googleTokenVerifier;
    private final UserSessionRestClient userSessionRestClient;
    private final LoginTokenService loginTokenService;

    public LoginService(final CustomerService customerService,
                        final UserPasswordService userPasswordService,
                        final GoogleTokenVerifier googleTokenVerifier, 
                        final UserSessionRestClient userSessionRestClient, final LoginTokenService loginTokenService) {
        this.customerService = customerService;
        this.userPasswordService = userPasswordService;
        this.googleTokenVerifier = googleTokenVerifier;
        this.userSessionRestClient = userSessionRestClient;
        this.loginTokenService = loginTokenService;
    }

    public GeneralLoginResponse loginWithCredentials(final LoginRequest request, final String deviceUuid) {
        final AuthenticatedPassword userPassword = userPasswordService.validatePassword(request.email(), request.password());

        final UserCoreInfo userInfo = customerService.getCustomer(userPassword.userId()).orElseThrow(CustomerNotFoundException::new);

        final CreateAuthenticatedUserRequest authenticatedUserRequest = CreateAuthenticatedUserRequest.withoutSession(userInfo.userId(),
                deviceUuid,
                userInfo.email(),
                userInfo.firstName(),
                userInfo.lastName(),
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

        final UserSession session = userSessionRestClient.validateSession(sessionId, deviceUuid);

        final UserCoreInfo userInfo = customerService.getCustomer(session.userId()).orElseThrow(CustomerNotFoundException::new);

        final CreateAuthenticatedUserRequest authenticatedUserRequest = new CreateAuthenticatedUserRequest(userInfo.userId(),
                deviceUuid,
                userInfo.email(),
                userInfo.firstName(),
                userInfo.lastName(),
                userInfo.language(),
                userInfo.createdAt(),
                false,
                AuthenticationMethod.SESSION,
                sessionId);

        return createAuthenticatedUser(authenticatedUserRequest);
    }

    public GeneralLoginResponse googleLogin(final GoogleLoginRequest request, String deviceUuid) {
        final String email = googleTokenVerifier.verifyAndGetEmail(request.token());
        final UserCoreInfo userInfo = customerService.getCustomerByEmail(email).orElseThrow(CustomerNotFoundException::new);

        final CreateAuthenticatedUserRequest authenticatedUserRequest = CreateAuthenticatedUserRequest.withoutSession(userInfo.userId(),
                deviceUuid,
                userInfo.email(),
                userInfo.firstName(),
                userInfo.lastName(),
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
                request.firstName(),
                request.lastName(),
                request.language(),
                request.createdAt(),
                sessionId,
                request.authenticationMethod()
        );

        if (request.rememberMe())
        {
            final LoginToken token = loginTokenService.createToken(request.userId());
            responseBuilder.loginToken(token.loginToken());
        }

        return responseBuilder.build();
    }

    public GeneralLoginResponse loginWithToken(String loginToken, String deviceUuid) {
        final LoginToken token = loginTokenService.getValidToken(loginToken);
        final long userId = token.userId();
        final UserCoreInfo userInfo = customerService.getCustomer(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        userSessionRestClient.createSession(userId, false, deviceUuid);

       final CreateAuthenticatedUserRequest authenticatedUserRequest = CreateAuthenticatedUserRequest.withoutSession(userInfo.userId(),
                deviceUuid,
                userInfo.email(),
                userInfo.firstName(),
                userInfo.lastName(),
                userInfo.language(),
                userInfo.createdAt(),
                true,
                AuthenticationMethod.LOGIN_TOKEN);

     return createAuthenticatedUser(authenticatedUserRequest);
    }

    private String getSessionId(final CreateAuthenticatedUserRequest request) {
        if (isNull(request.sessionId())) {
            final UserSession session = userSessionRestClient.createSession(request.userId(), false, request.deviceUuid());
            return session.sessionId();
        }
        return request.sessionId();
    }

}
