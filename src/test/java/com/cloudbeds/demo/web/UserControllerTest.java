package com.cloudbeds.demo.web;

import com.cloudbeds.demo.exception.handler.ErrorResponse;
import com.cloudbeds.demo.exception.handler.ValidationError;
import com.cloudbeds.demo.model.request.AddUserAddressRequestDTO;
import com.cloudbeds.demo.model.request.CreateUserRequestDTO;
import com.cloudbeds.demo.model.response.UserResponseDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private String context() {
        return "http://localhost:" + port;
    }

    @Test
    public void addUser_whenValidInput_shouldReturnExpected() throws IOException {

        final HttpHeaders headers = new HttpHeaders();
        final CreateUserRequestDTO requestBody = readFromFixture("add_user_valid.json", new TypeReference<CreateUserRequestDTO>() {
        });

        final HttpEntity<CreateUserRequestDTO> request = new HttpEntity<>(requestBody, headers);

        final ResponseEntity<UserResponseDTO> result = this.restTemplate.postForEntity(context() + "/user", request, UserResponseDTO.class);
        assertThat(result.getStatusCode(), equalTo(HttpStatus.CREATED));
        final UserResponseDTO actual = result.getBody();

        assertThat(actual, notNullValue());
        assertThat(actual.getUserId(), notNullValue());
        assertThat(actual.getEmail(), equalTo(requestBody.getEmail()));
        assertThat(actual.getLastName(), equalTo(requestBody.getLastName()));
        assertThat(actual.getFirstName(), equalTo(requestBody.getFirstName()));
        assertThat(actual.getAddresses(), nullValue());
    }

    @Test
    public void addUser_whenInvalidInput_shouldReturnError() throws IOException {

        final HttpHeaders headers = new HttpHeaders();
        final CreateUserRequestDTO requestBody = readFromFixture("add_user_invalid.json", new TypeReference<CreateUserRequestDTO>() {
        });

        final HttpEntity<CreateUserRequestDTO> request = new HttpEntity<>(requestBody, headers);

        final ResponseEntity<ErrorResponse> result = this.restTemplate.postForEntity(context() + "/user", request, ErrorResponse.class);
        assertThat(result.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
        final ErrorResponse failedRequest = result.getBody();

        assertThat(failedRequest, notNullValue());
        assertThat(failedRequest.getMessage(), equalTo("Validation error"));
        assertThat(failedRequest.getValidationErrors(), hasSize(2));
        assertThat(failedRequest.getValidationErrors(), containsInAnyOrder(Arrays.asList(
                ValidationError.validationError("email", "The user email must be a well-formed email address"),
                ValidationError.validationError("password", "The user password length must be at least 6 characters")
        ).toArray()));
    }

    @Test
    public void addAddress_whenValidInput_shouldReturnExpected() throws IOException {
        final HttpHeaders headers = new HttpHeaders();
        final CreateUserRequestDTO userRequestBody = readFromFixture("add_user_valid_2.json", new TypeReference<CreateUserRequestDTO>() {
        });

        final HttpEntity<CreateUserRequestDTO> userRequest = new HttpEntity<>(userRequestBody, headers);
        final ResponseEntity<UserResponseDTO> userResult = this.restTemplate.postForEntity(context() + "/user", userRequest, UserResponseDTO.class);
        assertThat(userResult.getStatusCode(), equalTo(HttpStatus.CREATED));
        final UserResponseDTO user = userResult.getBody();
        assertThat(user, notNullValue());

        // Now add an address for that user

        final AddUserAddressRequestDTO addressRequestBody = readFromFixture("add_address_valid.json", new TypeReference<AddUserAddressRequestDTO>() {
        });
        final HttpEntity<AddUserAddressRequestDTO> addressRequest = new HttpEntity<>(addressRequestBody, headers);
        final ResponseEntity<UserResponseDTO> addressResult = this.restTemplate.postForEntity(context() + "/user/" + user.getUserId() + "/address", addressRequest, UserResponseDTO.class);
        assertThat(addressResult.getStatusCode(), equalTo(HttpStatus.OK));

        final UserResponseDTO actual = addressResult.getBody();

        assertThat(actual, notNullValue());
        assertThat(actual.getAddresses(), notNullValue());
        assertThat(actual.getAddresses(), hasSize(1));
        assertThat(actual.getAddresses().get(0), notNullValue());

        assertThat(actual.getAddresses().get(0).getAddressId(), notNullValue());
        assertThat(actual.getAddresses().get(0).getAddressLine1(), equalTo(addressRequestBody.getAddressLine1()));
        assertThat(actual.getAddresses().get(0).getAddressLine2(), equalTo(addressRequestBody.getAddressLine2()));
        assertThat(actual.getAddresses().get(0).getCity(), equalTo(addressRequestBody.getCity()));
        assertThat(actual.getAddresses().get(0).getCountry(), equalTo(addressRequestBody.getCountry()));
        assertThat(actual.getAddresses().get(0).getState(), equalTo(addressRequestBody.getState()));
        assertThat(actual.getAddresses().get(0).getZip(), equalTo(addressRequestBody.getZip()));

    }

    private <T> T readFromFixture(final String fixtureName, final TypeReference<T> typeReference) throws IOException {
        final URL url = Resources.getResource(fixtureName);
        final String file = url.getFile();
        try (InputStream reader = new FileInputStream(file)) {
            return objectMapper.readValue(reader, typeReference);
        }
    }
}
