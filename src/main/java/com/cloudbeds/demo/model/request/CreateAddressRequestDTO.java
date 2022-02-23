package com.cloudbeds.demo.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "A request to create an address", name = "CreateAddressRequestDTO")
public class CreateAddressRequestDTO {

    @Schema(description = "The first address line", required = true)
    @NotNull(message = "{address.line1.mandatory}")
    private String addressLine1;

    @Schema(description = "The second address line. Can be omitted")
    private String addressLine2;

    @Schema(description = "The country of the address", required = true)
    @NotNull(message = "{address.country.mandatory}")
    private String country;

    @Schema(description = "The city of the address", required = true)
    @NotNull(message = "{address.city.mandatory}")
    private String city;

    @Schema(description = "The state of the address", required = true)
    @NotNull(message = "{address.state.mandatory}")
    private String state;

    @Schema(description = "The zip code of the address", required = true)
    @NotNull(message = "{address.zip.mandatory}")
    private String zip;
}
