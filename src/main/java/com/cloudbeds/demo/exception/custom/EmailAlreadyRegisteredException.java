package com.cloudbeds.demo.exception.custom;

public class EmailAlreadyRegisteredException extends RuntimeException {

    public EmailAlreadyRegisteredException(final String email) {
        super(String.format("Email '%s' is already registered to another user!", email));
    }
}
