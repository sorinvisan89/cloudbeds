package com.cloudbeds.demo.model.response;

import lombok.Data;

import java.util.List;

@Data
public class UserResponseDTO {

    private Integer userId;

    private String lastName;

    private String firstName;

    private String email;

    private List<AddressResponseDTO> addresses;
}
