package com.famevently.monolith.registration;

import java.time.LocalDate;

public record RegistrationRequest(
        String email,
        String firstName,
        String lastName,
        LocalDate birthday,
        String countryOfResidence,
        String language,
        String gender,
        String password
) {
}
