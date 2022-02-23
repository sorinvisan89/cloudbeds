package com.cloudbeds.demo.controller;

import com.cloudbeds.demo.exception.handler.ErrorResponse;
import com.cloudbeds.demo.model.request.CreateAddressRequestDTO;
import com.cloudbeds.demo.model.response.AddressResponseDTO;
import com.cloudbeds.demo.utils.AbstractControllerTest;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

import static com.cloudbeds.demo.utils.TestUtils.readFromFixture;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class AddressControllerTest extends AbstractControllerTest {

    @Test
    public void createAddress_whenValidInput_shouldReturnExpected() throws IOException {
        final AddressResponseDTO result = createAddress("create_address_valid_6.json");
        assertThat(result.getAddressId(), notNullValue());
        assertThat(result.getAddressLine1(), equalTo("Street 678"));
        assertThat(result.getCountry(), equalTo("Romania"));
        assertThat(result.getState(), equalTo("Constanta"));
        assertThat(result.getCity(), equalTo("Constanta"));
        assertThat(result.getZip(), equalTo("300-TC"));
    }

    @Test
    public void createAddress_whenAddressAlreadyExists_shouldReturnError() throws IOException {
        // Create first the address
        final AddressResponseDTO result = createAddress("create_address_valid_7.json");

        //Now try to create again with the same Zip code

        final HttpHeaders headers = new HttpHeaders();
        final CreateAddressRequestDTO addressRequestBody = readFromFixture("create_address_valid_7.json", new TypeReference<CreateAddressRequestDTO>() {
        });
        final HttpEntity<CreateAddressRequestDTO> createAddressRequest = new HttpEntity<>(addressRequestBody, headers);
        final ResponseEntity<ErrorResponse> createAddressResult = this.restTemplate.postForEntity(context() + "/address", createAddressRequest, ErrorResponse.class);
        assertThat(createAddressResult.getStatusCode(), equalTo(HttpStatus.CONFLICT));

        final ErrorResponse failedRequest = createAddressResult.getBody();

        assertThat(failedRequest, notNullValue());
        assertThat(failedRequest.getMessage(), equalTo("An address already exists for this zip code: 'GH-300'!"));
    }


}
