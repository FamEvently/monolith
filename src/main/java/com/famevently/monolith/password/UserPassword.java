package com.famevently.monolith.password;

public record UserPassword(
         long userId,
         String email,
         String passwordHash
) {
}
