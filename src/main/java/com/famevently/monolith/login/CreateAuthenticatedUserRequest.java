package com.famevently.monolith.login;

import java.time.OffsetDateTime;

public record CreateAuthenticatedUserRequest(
        Long userId,
        String deviceUuid,
        String email,
        String language,
        OffsetDateTime createdAt,
        boolean rememberMe,
        AuthenticationMethod authenticationMethod,
        String sessionId
)
{
    public static CreateAuthenticatedUserRequest withoutSession(final Long userId,
                                                                final String deviceUuid,
                                                                final String email,
                                                                final String language,
                                                                final OffsetDateTime createdAt,
                                                                final boolean rememberMe,
                                                                final AuthenticationMethod authenticationMethod) {
        return new CreateAuthenticatedUserRequest(userId,
                deviceUuid,
                email,
                language,
                createdAt,
                rememberMe,
                authenticationMethod,
                null);
    }
}
