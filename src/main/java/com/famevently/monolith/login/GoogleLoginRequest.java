package com.famevently.monolith.login;

public record GoogleLoginRequest(
        String token,
        boolean rememberMe) {
}

