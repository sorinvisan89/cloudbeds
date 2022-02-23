package com.cloudbeds.demo.exception.custom;

public class AddressNotFoundException extends RuntimeException {

    public AddressNotFoundException(final Integer addressId) {
        super(String.format("Address Id '%d' not found!", addressId));
    }
}
