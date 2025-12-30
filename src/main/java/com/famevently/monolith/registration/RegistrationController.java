package com.famevently.monolith.registration;

import com.famevently.monolith.login.GeneralLoginResponse;
import org.springframework.web.bind.annotation.*;

@RestController
public class RegistrationController {
    private final RegistrationService registrationService;

    public RegistrationController(final RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/v1/private/registration")
    public GeneralLoginResponse registerUser(@RequestBody final RegistrationRequest request,
                                             @RequestParam final String deviceUuid){
        return registrationService.registerUser(request, deviceUuid);
    }
}
