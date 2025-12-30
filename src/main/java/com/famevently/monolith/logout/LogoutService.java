package com.famevently.monolith.logout;

import com.famevently.monolith.usersession.UserSessionRestClient;
import org.springframework.stereotype.Service;

@Service
public class LogoutService {
    private final UserSessionRestClient userSessionRestClient;

    public LogoutService(final UserSessionRestClient userSessionRestClient) {
        this.userSessionRestClient = userSessionRestClient;
    }

    public void logout(final Long userId, final String deviceUuid)
    {
        userSessionRestClient.deleteSession(deviceUuid);
        //delete login token
    }
}
