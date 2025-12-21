package com.famevently.monolith.login;

public class InvalidGoogleCredentials extends RuntimeException {

    public static final String INVALID_GOOGLE_CREDENTIALS = "InvalidGoogleCredentials";

    public InvalidGoogleCredentials() {
        super(INVALID_GOOGLE_CREDENTIALS);
    }
}
