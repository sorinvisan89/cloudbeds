package com.cloudbeds.demo.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "A request to create an user", name = "CreateUserRequestDTO")
public class CreateUserRequestDTO {

    @Schema(description = "The user first name", required = true)
    @NotNull(message = "{user.firstname.mandatory}")
    private String firstName;

    @Schema(description = "The user last name", required = true)
    @NotNull(message = "{user.lastname.mandatory}")
    private String lastName;

    @Schema(description = "The email for the user", required = true)
    @NotNull(message = "{user.email.mandatory}")
    @Email(message = "{user.email.invalid}")
    private String email;

    @Schema(description = "The password for the user", required = true, minLength = 6)
    @NotNull(message = "{user.password.mandatory}")
    @Size(min = 6, message = "{user.password.min}")
    private String password;
}
