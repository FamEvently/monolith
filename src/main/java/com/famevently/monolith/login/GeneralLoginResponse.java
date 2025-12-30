package com.famevently.monolith.login;

import java.time.OffsetDateTime;

public record GeneralLoginResponse(
        long userId,
        String email,
        String language,
        OffsetDateTime createdAt,
        String sessionId,
        String loginToken,
        AuthenticationMethod authenticationMethod)
{
    public static class Builder {
        private final long userId;
        private final String email;
        private final String language;
        private final OffsetDateTime createdAt;
        private final String sessionId;
        private String loginToken;
        private AuthenticationMethod authenticationMethod;

        public Builder(final long userId, final String email, final String language, final OffsetDateTime createdAt, final String sessionId, final AuthenticationMethod authenticationMethod) {
            this.userId = userId;
            this.email = email;
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
            return new GeneralLoginResponse(userId, email, language, createdAt, sessionId, loginToken, authenticationMethod);
        }
    }
}
