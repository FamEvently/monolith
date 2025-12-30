package com.famevently.monolith.logout;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LogoutController
{
    private final LogoutService logoutService;

    public LogoutController(LogoutService logoutService) {
        this.logoutService = logoutService;
    }

    @DeleteMapping("/v1/users/{userId}/devices/{deviceUuid}/logout")
    public void logout(@PathVariable final Long userId,
                       @PathVariable final String deviceUuid)
    {
        logoutService.logout(userId, deviceUuid);
    }
}
