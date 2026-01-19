package com.famevently.monolith.login;

import java.time.OffsetDateTime;

public record GeneralLoginResponse(
        long userId,
        String email,
        String firstName,
        String lastName,
        String language,
        OffsetDateTime createdAt,
        String sessionId,
        String loginToken,
        AuthenticationMethod authenticationMethod)
{
    public static class Builder {
        private final long userId;
        private final String email;
        private final String firstName;
        private final String lastName;
        private final String language;
        private final OffsetDateTime createdAt;
        private final String sessionId;
        private String loginToken;
        private final AuthenticationMethod authenticationMethod;

        public Builder(final long userId, final String email, final String firstName, final String lastName, final String language, final OffsetDateTime createdAt, final String sessionId, final AuthenticationMethod authenticationMethod) {
            this.userId = userId;
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
            this.language = language;
            this.createdAt = createdAt;
            this.sessionId = sessionId;
            this.authenticationMethod = authenticationMethod;
        }

        public Builder loginToken(final String loginToken) {
            this.loginToken = loginToken;
            return this;
        }

        public GeneralLoginResponse build() {
            return new GeneralLoginResponse(userId, email, firstName, lastName, language, createdAt, sessionId, loginToken, authenticationMethod);
        }
    }
}
