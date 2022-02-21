package com.cloudbeds.demo.controller;

import com.cloudbeds.demo.model.request.AddUserAddressRequestDTO;
import com.cloudbeds.demo.model.request.CreateUserRequestDTO;
import com.cloudbeds.demo.model.response.UserResponseDTO;
import com.cloudbeds.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;

@RestController
@Tag(name = "User", description = "User Operations")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(final UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user")
    @Operation(description = "Create an user", summary = "Create user",
            responses = {
                    @ApiResponse(
                            description = "User created successfully!",
                            responseCode = "201",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))
                    ),
                    @ApiResponse(description = "Invalid parameters provided!", responseCode = "400"),
                    @ApiResponse(description = "Internal error", responseCode = "500")
            },
            requestBody =
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "The request body for creating a new user",
                    required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreateUserRequestDTO.class))
            )
    )
    ResponseEntity<UserResponseDTO> createUser(@RequestBody @Valid @NotNull final CreateUserRequestDTO createUserDTO) {
        final UserResponseDTO result = userService.createUser(createUserDTO);

        final URI newUserLocation = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(result.getUserId()).toUri();

        return ResponseEntity.created(newUserLocation).build();
    }

    @PostMapping("/user/{userId}/address")
    @Operation(description = "Add an address to an existing user", summary = "Add address",
            responses = {
                    @ApiResponse(
                            description = "Address added successfully!",
                            responseCode = "200",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))
                    ),
                    @ApiResponse(description = "Invalid parameters provided!", responseCode = "400"),
                    @ApiResponse(description = "Internal error", responseCode = "500")
            },
            requestBody =
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "The request body for adding a new address",
                    required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AddUserAddressRequestDTO.class))
            )
    )
    ResponseEntity<UserResponseDTO> addAddress(
            @PathVariable(name = "userId") final Integer userId,
            @RequestBody @Valid @NotNull final AddUserAddressRequestDTO addAddressDTO) {
        final UserResponseDTO result = userService.addUserAddress(userId, addAddressDTO);
        return ResponseEntity.ok(result);
    }


    @GetMapping("/users")
    @Operation(description = "Search users by country", summary = "Search users",
            responses = {
                    @ApiResponse(
                            description = "Search completed successfully!",
                            responseCode = "200",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))
                    ),
                    @ApiResponse(description = "Invalid parameters provided!", responseCode = "400"),
                    @ApiResponse(description = "Internal error", responseCode = "500")
            })
    ResponseEntity<List<UserResponseDTO>> getUsersByCountry(
            @RequestParam(value = "country") final String country,
            @RequestParam(value = "page", required = false, defaultValue = "0") final int page,
            @RequestParam(value = "limit", required = false, defaultValue = "10") final int limit) {
        final List<UserResponseDTO> result = userService.getUsersByCountry(country, page, limit);
        return ResponseEntity.ok(result);

    }
}
