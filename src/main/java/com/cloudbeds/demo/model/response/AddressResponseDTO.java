package com.cloudbeds.demo.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "A response containing an address", name = "AddressResponseDTO")
public class AddressResponseDTO {

    @Schema(description = "The address Id")
    private Integer addressId;

    @Schema(description = "The address first line")
    private String addressLine1;

    @Schema(description = "The address second line")
    private String addressLine2;

    @Schema(description = "The address city")
    private String city;

    @Schema(description = "The address state")
    private String state;

    @Schema(description = "The address country")
    private String country;

    @Schema(description = "The address zip code")
    private String zip;
}
