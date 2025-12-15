package com.famevently.monolith.customer;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record UserCoreInfo(
        long userId,
        String email,
        String firstName,
        String lastName,
        String language,
        String gender,
        LocalDate birthday,
        boolean isWhitelisted,
        OffsetDateTime createdAt
) {
}
