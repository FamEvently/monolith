package com.famevently.monolith.customer;

public class CustomerNotFoundException extends RuntimeException {

    public static final String CUSTOMER_NOT_FOUND = "CustomerNotFound";

    public CustomerNotFoundException() {
        super(CUSTOMER_NOT_FOUND);
    }
}
