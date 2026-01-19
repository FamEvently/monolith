package com.famevently.monolith.logintoken;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;

import com.famevently.monolith.usersession.InvalidSessionException;
import com.jetbrains.exported.JBRApi.Service;

@Service
public class LoginTokenService {

    private final LoginTokenRepository repository;

    private final Duration ttl;
    public LoginTokenService(@Value("${login-token.time-to-live}") final Duration ttl, LoginTokenRepository repository) {
        this.ttl = ttl;
        this.repository = repository;
    }

    public LoginToken createToken(final long userId) {
        return repository.upsert(userId);
    }

    public Optional<LoginToken> getLoginToken(String token) {
        return repository.getLoginToken(token);
    }

    public Optional<LoginToken> getLoginTokenByUserId(long userId) {
        return repository.getLoginTokenByUserId(userId);
    }

    public LoginToken getValidToken(String token) {
        if (token == null || token.isEmpty()) {
            throw new InvalidSessionException("Missing token");
        }

        Optional<LoginToken> optionalToken = repository.getLoginToken(token);

        LoginToken loginToken = optionalToken.orElseThrow(() ->
                new InvalidSessionException("Invalid token"));

        OffsetDateTime expiryTime = loginToken.updatedAt().plus(ttl);
        if (OffsetDateTime.now().isAfter(expiryTime)) {
            repository.deleteToken(token);
            throw new InvalidSessionException("Token expired");
        }

        return loginToken;
    }

    public void deleteTokenByUserId(long userId) {
        repository.deleteTokenByUserId(userId);
    }
}
