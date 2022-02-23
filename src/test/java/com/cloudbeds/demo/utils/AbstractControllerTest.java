package com.cloudbeds.demo.utils;

import com.cloudbeds.demo.model.request.AddUserAddressRequestDTO;
import com.cloudbeds.demo.model.request.CreateAddressRequestDTO;
import com.cloudbeds.demo.model.request.CreateUserRequestDTO;
import com.cloudbeds.demo.model.response.AddressResponseDTO;
import com.cloudbeds.demo.model.response.UserResponseDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;

import static com.cloudbeds.demo.utils.TestUtils.readFromFixture;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ActiveProfiles("test")
public abstract class AbstractControllerTest {

    @LocalServerPort
    protected int port;

    @Autowired
    protected TestRestTemplate restTemplate;

    protected String context() {
        return "http://localhost:" + port;
    }


    protected UserResponseDTO createUser(final String fixtureName) throws IOException {
        final HttpHeaders headers = new HttpHeaders();
        final CreateUserRequestDTO userRequestBody = readFromFixture(fixtureName, new TypeReference<CreateUserRequestDTO>() {
        });

        final HttpEntity<CreateUserRequestDTO> userRequest = new HttpEntity<>(userRequestBody, headers);
        final ResponseEntity<UserResponseDTO> userResult = this.restTemplate.postForEntity(context() + "/user", userRequest, UserResponseDTO.class);
        assertThat(userResult.getStatusCode(), equalTo(HttpStatus.CREATED));
        final UserResponseDTO user = userResult.getBody();
        assertThat(user, notNullValue());
        return user;
    }

    protected AddressResponseDTO createAddress(final String fixtureName) throws IOException {
        final HttpHeaders headers = new HttpHeaders();
        final CreateAddressRequestDTO addressRequestBody = readFromFixture(fixtureName, new TypeReference<CreateAddressRequestDTO>() {
        });
        final HttpEntity<CreateAddressRequestDTO> createAddressRequest = new HttpEntity<>(addressRequestBody, headers);
        final ResponseEntity<AddressResponseDTO> createAddressResult = this.restTemplate.postForEntity(context() + "/address", createAddressRequest, AddressResponseDTO.class);
        assertThat(createAddressResult.getStatusCode(), equalTo(HttpStatus.CREATED));
        assertThat(createAddressResult.getBody(), notNullValue());
        return createAddressResult.getBody();
    }

    protected UserResponseDTO addAddressToUser(final Integer userId, final Integer addressId){
        final HttpHeaders headers = new HttpHeaders();
        final AddUserAddressRequestDTO addUserAddressRequestDTO = AddUserAddressRequestDTO.builder()
                .addressId(addressId)
                .build();

        final HttpEntity<AddUserAddressRequestDTO> addAddressRequest = new HttpEntity<>(addUserAddressRequestDTO, headers);
        final ResponseEntity<UserResponseDTO> addAddressResult = this.restTemplate.postForEntity(context() + "/user/" + userId + "/address", addAddressRequest, UserResponseDTO.class);
        assertThat(addAddressResult.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(addAddressResult.getBody(), notNullValue());
        return addAddressResult.getBody();
    }
}
