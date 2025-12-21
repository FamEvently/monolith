package com.famevently.monolith.password;

public record AuthenticatedPassword(long userId) {
    public static AuthenticatedPassword of(final long userId) {
        return new AuthenticatedPassword(userId);
    }
}
