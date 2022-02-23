package com.cloudbeds.demo.exception.custom;

public class DuplicateAddressException extends RuntimeException{

    public DuplicateAddressException(final String zip) {
        super(String.format("An address already exists for this zip code: '%s'!", zip));
    }
}
