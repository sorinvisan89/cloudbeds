package com.cloudbeds.demo.model;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class CreateUserRequestDTO {

    @NotNull(message = "{user.firstname.mandatory}")
    private String firstName;

    @NotNull(message = "{user.lastname.mandatory}")
    private String lastName;

    @NotNull(message = "{user.email.mandatory}")
    @Email(message = "{user.email.invalid}")
    private String email;

    @NotNull(message = "{user.password.mandatory}")
    @Size(min = 6, message = "{user.password.min}")
    private String password;
}
