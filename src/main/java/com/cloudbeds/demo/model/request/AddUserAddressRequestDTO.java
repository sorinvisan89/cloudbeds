package com.cloudbeds.demo.model.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AddUserAddressRequestDTO {

    @NotNull(message = "{address.line1.mandatory}")
    private String addressLine1;

    private String addressLine2;

    @NotNull(message = "{address.country.mandatory}")
    private String country;

    @NotNull(message = "{address.city.mandatory}")
    private String city;

    @NotNull(message = "{address.state.mandatory}")
    private String state;
}
