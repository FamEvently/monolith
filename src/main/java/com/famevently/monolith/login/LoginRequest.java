package com.famevently.monolith.login;

public record LoginRequest(
        String email,
        String password,
        boolean rememberMe)
{
}
