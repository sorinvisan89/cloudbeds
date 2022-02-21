package com.cloudbeds.demo.model;

import lombok.Data;

@Data
public class UserDTO {

    private Integer userId;

    private String lastName;

    private String firstName;

    private String email;
}
