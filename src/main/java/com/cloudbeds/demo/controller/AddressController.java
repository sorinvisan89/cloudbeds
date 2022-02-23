package com.cloudbeds.demo.controller;


import com.cloudbeds.demo.model.request.CreateAddressRequestDTO;
import com.cloudbeds.demo.model.response.AddressResponseDTO;
import com.cloudbeds.demo.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;

@RestController
@Tag(name = "Address", description = "Address Operations")
public class AddressController {

    private final AddressService addressService;

    @Autowired
    public AddressController(final AddressService addressService) {
        this.addressService = addressService;
    }

    @PostMapping("/address")
    @Operation(description = "Create an address", summary = "Create address",
            responses = {
                    @ApiResponse(
                            description = "Address created successfully!",
                            responseCode = "201",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AddressResponseDTO.class))
                    ),
                    @ApiResponse(description = "Invalid parameters provided!", responseCode = "400"),
                    @ApiResponse(description = "Internal error", responseCode = "500")
            },
            requestBody =
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "The request body for creating a new address",
                    required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreateAddressRequestDTO.class))
            )
    )
    ResponseEntity<AddressResponseDTO> createAddress(@RequestBody @Valid @NotNull final CreateAddressRequestDTO createAddressDTO) {
        final AddressResponseDTO result = addressService.createAddress(createAddressDTO);

        final URI newAddressLocation = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(result.getAddressId()).toUri();

        return ResponseEntity.created(newAddressLocation)
                .body(result);
    }

}
