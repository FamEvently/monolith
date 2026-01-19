package com.famevently.monolith.login;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/private/login")
public class LoginController {
    private final LoginService loginService;

    public LoginController(final LoginService service) {
        this.loginService = service;
    }

    @PostMapping()
    public GeneralLoginResponse loginWithCredentials(@RequestBody final LoginRequest request,
                                                     @RequestParam final String deviceUuid) {
        return loginService.loginWithCredentials(request, deviceUuid);
    }

    @PostMapping("/session/{sessionId}")
    public GeneralLoginResponse loginWithSession(@PathVariable final String sessionId,
                                                 @RequestParam final String deviceUuid) {
        return loginService.loginWithSession(sessionId, deviceUuid);
    }

    @PostMapping("/google")
    public GeneralLoginResponse googleLogin(@RequestBody final GoogleLoginRequest request,
                                         @RequestParam final String deviceUuid) {
        return loginService.googleLogin(request,deviceUuid);
    }
}
