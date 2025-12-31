package com.famevently.monolith.password;

public class InvalidCredentials extends RuntimeException {

    private final String email;
    public static final String INVALID_CREDENTIALS = "InvalidCredentials";

    public InvalidCredentials(final String email) {
        super(INVALID_CREDENTIALS);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
