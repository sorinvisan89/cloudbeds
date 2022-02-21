package com.cloudbeds.demo.mapper;

import com.cloudbeds.demo.entity.AddressEntity;
import com.cloudbeds.demo.entity.UserEntity;
import com.cloudbeds.demo.model.request.AddUserAddressRequestDTO;
import com.cloudbeds.demo.model.request.CreateUserRequestDTO;
import com.cloudbeds.demo.model.response.AddressResponseDTO;
import com.cloudbeds.demo.model.response.UserResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class CustomMapperTest {

    private CustomMapper mapper;

    @BeforeEach
    public void setup() {
        mapper = new CustomMapperImpl();
    }

    @Test
    public void mapRequest_whenCreateUserRequestDTO_shouldReturnExpected() {

        final CreateUserRequestDTO createUserRequestDTO = CreateUserRequestDTO.builder()
                .email("email1")
                .firstName("aName")
                .lastName("anotherName")
                .password("myPassowrd")
                .build();

        final UserEntity expected = UserEntity.builder()
                .id(null)
                .addresses(null)
                .lastName(createUserRequestDTO.getLastName())
                .firstName(createUserRequestDTO.getFirstName())
                .email(createUserRequestDTO.getEmail())
                .password(createUserRequestDTO.getPassword())
                .build();

        final UserEntity actual = mapper.mapRequest(createUserRequestDTO);
        assertThat(actual, equalTo(expected));
    }

    @Test
    public void mapRequest_whenAddUserAddressRequestDTO_shouldReturnExpected() {

        final AddUserAddressRequestDTO addUserAddressRequestDTO = AddUserAddressRequestDTO.builder()
                .addressLine1("line1")
                .addressLine2("line2")
                .country("Romania")
                .city("Bucharest")
                .state("Bucharest")
                .zip("100-XC-200")
                .build();

        final AddressEntity expected = AddressEntity.builder()
                .addressId(null)
                .address1(addUserAddressRequestDTO.getAddressLine1())
                .address2(addUserAddressRequestDTO.getAddressLine2())
                .country(addUserAddressRequestDTO.getCountry())
                .city(addUserAddressRequestDTO.getCity())
                .state(addUserAddressRequestDTO.getState())
                .zip(addUserAddressRequestDTO.getZip())
                .build();

        final AddressEntity actual = mapper.mapRequest(addUserAddressRequestDTO);
        assertThat(actual, equalTo(expected));
    }

    @Test
    public void mapResponse_whenAddressEntity_shouldReturnExpected() {

        final AddressEntity toMap = AddressEntity.builder()
                .addressId(123)
                .address1("line 1")
                .address2(null)
                .city("A City")
                .state("A state")
                .country("A country")
                .zip("200-XC-200")
                .build();

        final AddressResponseDTO expected = AddressResponseDTO.builder()
                .addressId(toMap.getAddressId())
                .addressLine1(toMap.getAddress1())
                .addressLine2(toMap.getAddress2())
                .city(toMap.getCity())
                .state(toMap.getState())
                .country(toMap.getCountry())
                .zip(toMap.getZip())
                .build();

        final AddressResponseDTO actual = mapper.mapResponse(toMap);
        assertThat(actual, equalTo(expected));
    }

    @Test
    public void mapResponse_whenUserEntity_shouldReturnExpected() {

        final AddressEntity address = AddressEntity.builder()
                .addressId(3345)
                .address1("line 1")
                .address2("line 2")
                .city("A City")
                .state("A state")
                .country("A country")
                .zip("200-XC-200")
                .build();

        final UserEntity toMap = UserEntity.builder()
                .email("my_email@yahoo.com")
                .password("A-secure-password")
                .firstName("Johnny")
                .lastName("Johnson")
                .id(123)
                .addresses(Collections.singletonList(address))
                .build();

        final UserResponseDTO expected = UserResponseDTO.builder()
                .userId(toMap.getId())
                .email(toMap.getEmail())
                .firstName(toMap.getFirstName())
                .lastName(toMap.getLastName())
                .addresses(Collections.singletonList(mapper.mapResponse(address)))
                .build();

        final UserResponseDTO actual = mapper.mapResponse(toMap);
        assertThat(actual, equalTo(expected));
    }


}
