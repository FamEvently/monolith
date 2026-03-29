package com.famevently.monolith.userrating;

public class UserAlreadyRatedException extends Exception {

    public static final String CUSTOMER_ALREADY_RATED = "CustomerAlreadyRated";

    public UserAlreadyRatedException() {
        super(CUSTOMER_ALREADY_RATED);
    }
}
