package com.famevently.monolith.logout;

import com.famevently.monolith.logintoken.LoginTokenService;
import com.famevently.monolith.usersession.UserSessionRestClient;
import org.springframework.stereotype.Service;

@Service
public class LogoutService {
    private final UserSessionRestClient userSessionRestClient;
    private final LoginTokenService loginTokenService;

    public LogoutService(final UserSessionRestClient userSessionRestClient, final LoginTokenService loginTokenService) {
        this.userSessionRestClient = userSessionRestClient;
        this.loginTokenService = loginTokenService;
    }

    public void logout(final Long userId, final String deviceUuid)
    {
        userSessionRestClient.deleteSession(deviceUuid);
        loginTokenService.deleteTokenByUserId(userId);
    }
}
