package com.cloudbeds.demo.controller;

import com.cloudbeds.demo.exception.handler.ErrorResponse;
import com.cloudbeds.demo.exception.handler.ValidationError;
import com.cloudbeds.demo.model.request.AddUserAddressRequestDTO;
import com.cloudbeds.demo.model.request.CreateAddressRequestDTO;
import com.cloudbeds.demo.model.request.CreateUserRequestDTO;
import com.cloudbeds.demo.model.response.AddressResponseDTO;
import com.cloudbeds.demo.model.response.UserResponseDTO;
import com.cloudbeds.demo.utils.AbstractControllerTest;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.cloudbeds.demo.utils.TestUtils.readFromFixture;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class UserControllerTest extends AbstractControllerTest {

    @Test
    public void createUser_whenValidInput_shouldReturnExpected() throws IOException {

        final HttpHeaders headers = new HttpHeaders();
        final CreateUserRequestDTO requestBody = readFromFixture("create_user_valid_1.json", new TypeReference<CreateUserRequestDTO>() {
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
    public void createUser_whenInvalidInput_shouldReturnError() throws IOException {

        final HttpHeaders headers = new HttpHeaders();
        final CreateUserRequestDTO requestBody = readFromFixture("create_user_invalid.json", new TypeReference<CreateUserRequestDTO>() {
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
    public void createUser_whenEmailAlreadyRegistered_shouldReturnError() throws IOException {
        final HttpHeaders headers = new HttpHeaders();
        final CreateUserRequestDTO userRequestBody = readFromFixture("create_user_not_unique_email.json", new TypeReference<CreateUserRequestDTO>() {
        });

        final HttpEntity<CreateUserRequestDTO> request = new HttpEntity<>(userRequestBody, headers);

        final ResponseEntity<UserResponseDTO> result = this.restTemplate.postForEntity(context() + "/user", request, UserResponseDTO.class);

        // First time request adding the user
        assertThat(result.getStatusCode(), equalTo(HttpStatus.CREATED));

        // Now try again to add an user with the same email
        final ResponseEntity<ErrorResponse> errorResult = this.restTemplate.postForEntity(context() + "/user", request, ErrorResponse.class);
        assertThat(errorResult.getStatusCode(), equalTo(HttpStatus.CONFLICT));
        final ErrorResponse failedRequest = errorResult.getBody();

        assertThat(failedRequest, notNullValue());
        assertThat(failedRequest.getMessage(), equalTo("Email 'non_unique_email@yahoo.com' is already registered to another user!"));
    }

    @Test
    public void addUserAddress_whenValidInput_shouldReturnExpected() throws IOException {

        // First create the user
        final HttpHeaders headers = new HttpHeaders();
        final CreateUserRequestDTO userRequestBody = readFromFixture("create_user_valid_2.json", new TypeReference<CreateUserRequestDTO>() {
        });

        final HttpEntity<CreateUserRequestDTO> userRequest = new HttpEntity<>(userRequestBody, headers);
        final ResponseEntity<UserResponseDTO> userResult = this.restTemplate.postForEntity(context() + "/user", userRequest, UserResponseDTO.class);
        assertThat(userResult.getStatusCode(), equalTo(HttpStatus.CREATED));
        final UserResponseDTO user = userResult.getBody();
        assertThat(user, notNullValue());

        // Then create an address

        final CreateAddressRequestDTO createAddressRequestDTO = readFromFixture("create_address_valid_1.json", new TypeReference<CreateAddressRequestDTO>() {
        });
        final HttpEntity<CreateAddressRequestDTO> createAddressRequest = new HttpEntity<>(createAddressRequestDTO, headers);
        final ResponseEntity<AddressResponseDTO> addressResult = this.restTemplate.postForEntity(context() + "/address", createAddressRequest, AddressResponseDTO.class);
        assertThat(addressResult.getStatusCode(), equalTo(HttpStatus.CREATED));
        assertThat(addressResult.getBody(), notNullValue());

        final Integer addressId = addressResult.getBody().getAddressId();

        // Now add an address for that user
        final AddUserAddressRequestDTO addUserAddressRequestDTO = AddUserAddressRequestDTO.builder()
                .addressId(addressId)
                .build();

        final HttpEntity<AddUserAddressRequestDTO> addAddressRequest = new HttpEntity<>(addUserAddressRequestDTO, headers);
        final ResponseEntity<UserResponseDTO> addAddressResult = this.restTemplate.postForEntity(context() + "/user/" + user.getUserId() + "/address", addAddressRequest, UserResponseDTO.class);
        assertThat(addAddressResult.getStatusCode(), equalTo(HttpStatus.OK));

        final UserResponseDTO actual = addAddressResult.getBody();

        assertThat(actual, notNullValue());
        assertThat(actual.getAddresses(), notNullValue());
        assertThat(actual.getAddresses(), hasSize(1));
        assertThat(actual.getAddresses().get(0), notNullValue());

        assertThat(actual.getAddresses().get(0).getAddressId(), notNullValue());
        assertThat(actual.getAddresses().get(0).getAddressLine1(), equalTo(createAddressRequestDTO.getAddressLine1()));
        assertThat(actual.getAddresses().get(0).getAddressLine2(), equalTo(createAddressRequestDTO.getAddressLine2()));
        assertThat(actual.getAddresses().get(0).getCity(), equalTo(createAddressRequestDTO.getCity()));
        assertThat(actual.getAddresses().get(0).getCountry(), equalTo(createAddressRequestDTO.getCountry()));
        assertThat(actual.getAddresses().get(0).getState(), equalTo(createAddressRequestDTO.getState()));
        assertThat(actual.getAddresses().get(0).getZip(), equalTo(createAddressRequestDTO.getZip()));
    }

    @Test
    public void addUserAddress_whenAddressDoesNotExist_shouldReturnError() throws IOException {
        // First create the user
        final HttpHeaders headers = new HttpHeaders();
        final CreateUserRequestDTO userRequestBody = readFromFixture("create_user_valid_3.json", new TypeReference<CreateUserRequestDTO>() {
        });

        final HttpEntity<CreateUserRequestDTO> userRequest = new HttpEntity<>(userRequestBody, headers);
        final ResponseEntity<UserResponseDTO> userResult = this.restTemplate.postForEntity(context() + "/user", userRequest, UserResponseDTO.class);
        assertThat(userResult.getStatusCode(), equalTo(HttpStatus.CREATED));
        final UserResponseDTO user = userResult.getBody();
        assertThat(user, notNullValue());

        // Now try to add an address that does not exist

        final Integer addressIdNotPresent = 999999999;

        final AddUserAddressRequestDTO addUserAddressRequestDTO = AddUserAddressRequestDTO.builder()
                .addressId(addressIdNotPresent)
                .build();

        final HttpEntity<AddUserAddressRequestDTO> addAddressRequest = new HttpEntity<>(addUserAddressRequestDTO, headers);
        final ResponseEntity<UserResponseDTO> addAddressResult = this.restTemplate.postForEntity(context() + "/user/" + user.getUserId() + "/address", addAddressRequest, UserResponseDTO.class);
        assertThat(addAddressResult.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
    }

    @Test
    public void addAddresses_whenMultipleAddressesToSameUser_shouldReturnExpected() throws IOException {
        final UserResponseDTO userCreated = createUser("create_user_valid_6.json");
        final AddressResponseDTO address1 = createAddress("create_address_valid_3.json");
        final AddressResponseDTO address2 = createAddress("create_address_valid_4.json");
        addAddressToUser(userCreated.getUserId(), address1.getAddressId());
        final UserResponseDTO userWithTwoAddresses = addAddressToUser(userCreated.getUserId(), address2.getAddressId());

        assertThat(userWithTwoAddresses.getAddresses(), hasSize(2));
        assertThat(userWithTwoAddresses.getAddresses(), containsInAnyOrder(address1, address2));
    }

    @Test
    public void addAddresses_whenSameAddressToDifferentUsers_shouldReturnExpected() throws IOException {
        final UserResponseDTO userCreated1 = createUser("create_user_valid_4.json");
        final UserResponseDTO userCreated2 = createUser("create_user_valid_5.json");
        final AddressResponseDTO address = createAddress("create_address_valid_5.json");

        final UserResponseDTO firstUserWithAddress = addAddressToUser(userCreated1.getUserId(), address.getAddressId());
        final UserResponseDTO secondUserWithAddress = addAddressToUser(userCreated2.getUserId(), address.getAddressId());

        assertThat(firstUserWithAddress.getAddresses(), hasSize(1));
        assertThat(firstUserWithAddress.getAddresses().get(0).getAddressId(), equalTo(address.getAddressId()));
        assertThat(firstUserWithAddress.getAddresses().get(0).getAddressLine1(), equalTo(address.getAddressLine1()));
        assertThat(firstUserWithAddress.getAddresses().get(0).getZip(), equalTo(address.getZip()));
        assertThat(firstUserWithAddress.getAddresses().get(0).getCountry(), equalTo(address.getCountry()));
        assertThat(firstUserWithAddress.getAddresses().get(0).getState(), equalTo(address.getState()));
        assertThat(firstUserWithAddress.getAddresses().get(0).getCity(), equalTo(address.getCity()));

        assertThat(secondUserWithAddress.getAddresses(), hasSize(1));
        assertThat(secondUserWithAddress.getAddresses().get(0).getAddressId(), equalTo(address.getAddressId()));
        assertThat(secondUserWithAddress.getAddresses().get(0).getAddressLine1(), equalTo(address.getAddressLine1()));
        assertThat(secondUserWithAddress.getAddresses().get(0).getZip(), equalTo(address.getZip()));
        assertThat(secondUserWithAddress.getAddresses().get(0).getCountry(), equalTo(address.getCountry()));
        assertThat(secondUserWithAddress.getAddresses().get(0).getState(), equalTo(address.getState()));
        assertThat(secondUserWithAddress.getAddresses().get(0).getCity(), equalTo(address.getCity()));
    }

    @Test
    public void getUsers_whenSearchByCountry_shouldReturnExpected() throws IOException {
        // First create 3 users

        // Lives in France
        final UserResponseDTO user1 = createUserAndAddAddress("create_user_for_country_search_1.json", "create_address_for_country_search_1.json");

        // Lives in France and Romania
        // Add the Romanian address
        final UserResponseDTO user2 = createUserAndAddAddress("create_user_for_country_search_2.json", "create_address_for_country_search_3.json");

        // Also add French address
        final CreateAddressRequestDTO createAddressRequestDTO = readFromFixture("create_address_for_country_search_2.json", new TypeReference<CreateAddressRequestDTO>() {
        });
        final HttpHeaders headers = new HttpHeaders();
        final HttpEntity<CreateAddressRequestDTO> createAddressRequest = new HttpEntity<>(createAddressRequestDTO, headers);
        final ResponseEntity<AddressResponseDTO> addressResult = this.restTemplate.postForEntity(context() + "/address", createAddressRequest, AddressResponseDTO.class);
        assertThat(addressResult.getStatusCode(), equalTo(HttpStatus.CREATED));
        assertThat(addressResult.getBody(), notNullValue());

        // Link French address to user
        final AddUserAddressRequestDTO addUserAddressRequestDTO = AddUserAddressRequestDTO.builder()
                .addressId(addressResult.getBody().getAddressId())
                .build();

        final HttpEntity<AddUserAddressRequestDTO> addAddressRequest = new HttpEntity<>(addUserAddressRequestDTO, headers);
        final ResponseEntity<UserResponseDTO> addedUserAddressResult = this.restTemplate.postForEntity(context() + "/user/" + user2.getUserId() + "/address", addAddressRequest, UserResponseDTO.class);
        assertThat(addedUserAddressResult.getStatusCode(), equalTo(HttpStatus.OK));

        // Lives in Germany
        final UserResponseDTO user3 = createUserAndAddAddress("create_user_for_country_search_3.json", "create_address_for_country_search_4.json");
        assertThat(user3, notNullValue());
        // Only two users should be retrieved.
        // User1 and also user2 since user2 two addresses, one of which is in the requested country.

        final String urlTemplate = UriComponentsBuilder.fromHttpUrl(context() + "/users")
                .queryParam("country", "{country}")
                .queryParam("page", "{page}")
                .queryParam("limit", "{limit}")
                .encode()
                .toUriString();

        final Map<String, Object> params = new HashMap<>();
        params.put("country", "France");
        params.put("page", 0);
        params.put("limit", 10);


        final ResponseEntity<UserResponseDTO[]> retrieveResponse = this.restTemplate.getForEntity(
                urlTemplate, UserResponseDTO[].class, params);

        assertThat(retrieveResponse.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(retrieveResponse.getBody(), notNullValue());

        final List<UserResponseDTO> users = Arrays.asList(retrieveResponse.getBody());
        assertThat(users.size(), equalTo(2));

        final List<Integer> userIds = users.stream().map(UserResponseDTO::getUserId).collect(Collectors.toList());
        assertThat(userIds, containsInAnyOrder(user1.getUserId(), user2.getUserId()));
        final List<String> userNames = users.stream().map(UserResponseDTO::getLastName).collect(Collectors.toList());
        assertThat(userNames, containsInAnyOrder("TheFirstLivingInFrance", "TheSecondAlsoLivingInFrance"));
    }


    private UserResponseDTO createUserAndAddAddress(final String userFixture, final String addressFixture) throws IOException {
        final HttpHeaders headers = new HttpHeaders();
        final CreateUserRequestDTO createUserRequestDTO = readFromFixture(userFixture, new TypeReference<CreateUserRequestDTO>() {
        });

        final HttpEntity<CreateUserRequestDTO> userRequest = new HttpEntity<>(createUserRequestDTO, headers);
        final ResponseEntity<UserResponseDTO> userResult = this.restTemplate.postForEntity(context() + "/user", userRequest, UserResponseDTO.class);
        assertThat(userResult.getStatusCode(), equalTo(HttpStatus.CREATED));
        final UserResponseDTO user = userResult.getBody();
        assertThat(user, notNullValue());
        assertThat(user.getUserId(), notNullValue());

        final CreateAddressRequestDTO addressRequestBody = readFromFixture(addressFixture, new TypeReference<CreateAddressRequestDTO>() {
        });
        final HttpEntity<CreateAddressRequestDTO> createAddressRequest = new HttpEntity<>(addressRequestBody, headers);
        final ResponseEntity<AddressResponseDTO> createAddressResult = this.restTemplate.postForEntity(context() + "/address", createAddressRequest, AddressResponseDTO.class);
        assertThat(createAddressResult.getStatusCode(), equalTo(HttpStatus.CREATED));
        assertThat(createAddressResult.getBody(), notNullValue());

        final Integer addressId = createAddressResult.getBody().getAddressId();
        assertThat(addressId, notNullValue());

        final AddUserAddressRequestDTO addUserAddressRequestDTO = AddUserAddressRequestDTO.builder()
                .addressId(addressId)
                .build();

        final HttpEntity<AddUserAddressRequestDTO> addAddressRequest = new HttpEntity<>(addUserAddressRequestDTO, headers);
        final ResponseEntity<UserResponseDTO> addAddressResult = this.restTemplate.postForEntity(context() + "/user/" + user.getUserId() + "/address", addAddressRequest, UserResponseDTO.class);
        assertThat(addAddressResult.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(addAddressResult.getBody(), notNullValue());

        return addAddressResult.getBody();
    }

}
