package com.famevently.monolith.registration;

public class EmailAlreadyUsedException extends RuntimeException {

    public static final String EMAIL_ALREADY_USED = "EmailAlreadyUsed";

    public EmailAlreadyUsedException() {
        super(EMAIL_ALREADY_USED);
    }
}
