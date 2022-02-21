package com.cloudbeds.demo.model.response;

import lombok.Data;

@Data
public class AddressResponseDTO {

    private Integer addressId;

    private String addressLine1;

    private String addressLine2;

    private String city;

    private String state;

    private String country;
}
