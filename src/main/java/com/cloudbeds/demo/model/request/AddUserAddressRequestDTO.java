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
@Schema(description = "A request to add an address to an user", name = "AddUserAddressRequestDTO")
public class AddUserAddressRequestDTO {

    @Schema(description = "The address Id", required = true)
    @NotNull(message = "{address.id.mandatory}")
    private Integer addressId;
}
