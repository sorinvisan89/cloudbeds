package com.cloudbeds.demo.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@Schema(description = "A response containing an user", name = "UserResponseDTO")
public class UserResponseDTO {

    @Schema(description = "The user Id")
    private Integer userId;

    @Schema(description = "The user last name")
    private String lastName;

    @Schema(description = "The user first name")
    private String firstName;

    @Schema(description = "The user email")
    private String email;

    @Schema(description = "The list of user addresses")
    private List<AddressResponseDTO> addresses;
}
