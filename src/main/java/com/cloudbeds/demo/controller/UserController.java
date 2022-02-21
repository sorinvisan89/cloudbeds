package com.cloudbeds.demo.controller;

import com.cloudbeds.demo.model.CreateUserRequestDTO;
import com.cloudbeds.demo.model.UserDTO;
import com.cloudbeds.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(final UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user")
    @Tag(name = "User", description = "User Operations")
    @Operation(description = "Create an user", summary = "Create user",
            responses = {
                    @ApiResponse(
                            description = "User created successfully!",
                            responseCode = "201",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))
                    ),
                    @ApiResponse(description = "Invalid parameters provided!", responseCode = "400"),
                    @ApiResponse(description = "Internal error", responseCode = "500")
            })
    ResponseEntity<UserDTO> createUser(@RequestBody @Valid @NotNull final CreateUserRequestDTO createUserDTO) {
        final UserDTO result = userService.createUser(createUserDTO);
        return ResponseEntity.ok(result);
    }
}
