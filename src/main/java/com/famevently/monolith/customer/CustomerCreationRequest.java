package com.famevently.monolith.customer;

import com.famevently.monolith.registration.RegistrationRequest;

import java.time.LocalDate;

public record CustomerCreationRequest(
     String email,
     String firstName,
     String lastName,
     String language,
     String gender,
     LocalDate birthday,
     String countryOfResidence,
     boolean isWhitelisted) {

    public static CustomerCreationRequest fromRegistration(RegistrationRequest request)
    {
        return new CustomerCreationRequest(
            request.email(),
            request.firstName(),
            request.lastName(),
            request.language(),
            request.gender(),
            request.birthday(),
            request.countryOfResidence(),
                false
        );
    }
}


