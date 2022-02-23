package com.cloudbeds.demo.integration;

import com.cloudbeds.demo.utils.AbstractIT;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import static com.cloudbeds.demo.utils.TestUtils.extractIdFromHeaders;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class UserIT extends AbstractIT {

    @Before
    public void setup() {
        this.httpClient = createHttpClient();
    }

    @Test
    public void addUser_whenValidInput_shouldReturnExpected() throws IOException, InterruptedException {

        final HttpRequest request = HttpRequest.newBuilder()
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .uri(buildCreateUserRequestUrl())
                .POST(
                        HttpRequest.BodyPublishers.ofInputStream(() ->
                                UserIT.class.getResourceAsStream("/create_user_valid_1.json")))
                .build();

        final HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertHttpResponseStatusCodeInSuccessRange(response);

        final Map<String, String> expected = Map.of(
                "$.firstName", "sorin",
                "$.lastName", "visan",
                "$.email", "sorin2@yahoo.com"
        );

        assertJsonPathsWithValues(response, expected);
    }

    @Test
    public void addUser_whenInvalidInput_shouldReturnError() throws IOException, InterruptedException {

        final HttpRequest request = HttpRequest.newBuilder()
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .uri(buildCreateUserRequestUrl())
                .POST(
                        HttpRequest.BodyPublishers.ofInputStream(() ->
                                UserIT.class.getResourceAsStream("/create_user_invalid.json")))
                .build();

        final HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode(), equalTo(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    public void addUser_whenEmailAlreadyRegistered_shouldReturnError() throws IOException, InterruptedException {

        final HttpRequest request = HttpRequest.newBuilder()
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .uri(buildCreateUserRequestUrl())
                .POST(
                        HttpRequest.BodyPublishers.ofInputStream(() ->
                                UserIT.class.getResourceAsStream("/create_user_not_unique_email.json")))
                .build();

        // First request should pass
        final HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode(), equalTo(HttpStatus.CREATED.value()));

        // This should fail since we already have the email in the DB
        final HttpResponse<String> secondResponse = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(secondResponse.statusCode(), equalTo(HttpStatus.CONFLICT.value()));
        assertErrorMessage(secondResponse.body(), "$.message", "Email 'non_unique_email@yahoo.com' is already registered to another user!");
    }

    @Test
    public void addAddressToUser_whenValidInput_shouldReturnExpected() throws IOException, InterruptedException {

        // First create user
        final HttpRequest userRequest = HttpRequest.newBuilder()
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .uri(buildCreateUserRequestUrl())
                .POST(
                        HttpRequest.BodyPublishers.ofInputStream(() ->
                                UserIT.class.getResourceAsStream("/create_user_valid_2.json")))
                .build();

        final HttpResponse<String> userResponse = this.httpClient.send(userRequest, HttpResponse.BodyHandlers.ofString());
        assertHttpResponseStatusCodeInSuccessRange(userResponse);

        final String userId = extractIdFromHeaders(userResponse);

        // Now create first address
        final HttpRequest addressRequest = HttpRequest.newBuilder()
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .uri(buildCreateAddressRequestUrl())
                .POST(
                        HttpRequest.BodyPublishers.ofInputStream(() ->
                                UserIT.class.getResourceAsStream("/create_address_valid_1.json")))
                .build();

        final HttpResponse<String> addressResponse = this.httpClient.send(addressRequest, HttpResponse.BodyHandlers.ofString());
        assertHttpResponseStatusCodeInSuccessRange(addressResponse);

        final String addressId = extractIdFromHeaders(addressResponse);

        // Now add the address to the user
        final HttpRequest addUserAddressRequest = HttpRequest.newBuilder()
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .uri(buildAddAddressToUserRequestUrl(userId))
                .POST(
                        HttpRequest.BodyPublishers.ofString(createRequestBodyForAddAddress(addressId))
                )
                .build();

        final HttpResponse<String> addAddressResponse = this.httpClient.send(addUserAddressRequest, HttpResponse.BodyHandlers.ofString());
        assertHttpResponseStatusCodeInSuccessRange(addAddressResponse);

        final Map<String, String> expected = Map.of(
                "$.firstName", "another",
                "$.lastName", "user",
                "$.email", "valid_mail@yahoo.com",
                "$.addresses[0].addressLine1", "Washington Street no 54",
                "$.addresses[0].addressLine2", "Building 39, Ap. 14",
                "$.addresses[0].country", "USA",
                "$.addresses[0].city", "Washington",
                "$.addresses[0].state", "DC",
                "$.addresses[0].zip", "400-320"
        );

        assertJsonPathsWithValues(addAddressResponse, expected);
    }

    @Test
    public void getUsers_whenCalledWithCountry_shouldReturnExpected() throws IOException, InterruptedException {

        // Create first user
        final HttpRequest userRequest1 = HttpRequest.newBuilder()
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .uri(buildCreateUserRequestUrl())
                .POST(
                        HttpRequest.BodyPublishers.ofInputStream(() ->
                                UserIT.class.getResourceAsStream("/create_user_for_country_search_1.json")))
                .build();

        final HttpResponse<String> userResponse1 = this.httpClient.send(userRequest1, HttpResponse.BodyHandlers.ofString());
        assertHttpResponseStatusCodeInSuccessRange(userResponse1);

        final String userId1 = extractIdFromHeaders(userResponse1);

        // Create first address
        final HttpRequest addressRequest1 = HttpRequest.newBuilder()
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .uri(buildCreateAddressRequestUrl())
                .POST(
                        HttpRequest.BodyPublishers.ofInputStream(() ->
                                UserIT.class.getResourceAsStream("/create_address_for_country_search_1.json")))
                .build();

        final HttpResponse<String> addressResponse1 = this.httpClient.send(addressRequest1, HttpResponse.BodyHandlers.ofString());
        assertHttpResponseStatusCodeInSuccessRange(addressResponse1);

        final String addressId1 = extractIdFromHeaders(addressResponse1);

        // Now link the address with the user
        linkAddressWithUser(userId1, addressId1);

        // Create second user
        final HttpRequest userRequest2 = HttpRequest.newBuilder()
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .uri(buildCreateUserRequestUrl())
                .POST(
                        HttpRequest.BodyPublishers.ofInputStream(() ->
                                UserIT.class.getResourceAsStream("/create_user_for_country_search_3.json")))
                .build();

        final HttpResponse<String> userResponse2 = this.httpClient.send(userRequest2, HttpResponse.BodyHandlers.ofString());
        assertHttpResponseStatusCodeInSuccessRange(userResponse2);

        final String userId2 = extractIdFromHeaders(userResponse2);

        // Create second address
        final HttpRequest addressRequest2 = HttpRequest.newBuilder()
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .uri(buildCreateAddressRequestUrl())
                .POST(
                        HttpRequest.BodyPublishers.ofInputStream(() ->
                                UserIT.class.getResourceAsStream("/create_address_for_country_search_3.json")))
                .build();

        final HttpResponse<String> addressResponse2 = this.httpClient.send(addressRequest2, HttpResponse.BodyHandlers.ofString());
        assertHttpResponseStatusCodeInSuccessRange(addressResponse2);

        // Now link the address with the user
        final String addressId2 = extractIdFromHeaders(addressResponse2);
        linkAddressWithUser(userId2, addressId2);

        // Now search the users by country
        final HttpRequest searchUsersRequest = HttpRequest.newBuilder()
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .uri(buildSearchCountryRequestUrl("Romania"))
                .GET()
                .build();

        final HttpResponse<String> searchResponse = this.httpClient.send(searchUsersRequest, HttpResponse.BodyHandlers.ofString());
        assertHttpResponseStatusCodeInSuccessRange(searchResponse);

        final Map<String, String> expected = Map.of(
                "$[0].firstName", "user3",
                "$[0].lastName", "TheThird",
                "$[0].email", "user3@yahoo.com",
                "$[0].addresses[0].addressLine1", "A street in Romania",
                "$[0].addresses[0].country", "Romania",
                "$[0].addresses[0].city", "Bucharest",
                "$[0].addresses[0].state", "Bucharest",
                "$[0].addresses[0].zip", "0340C100"
        );

        assertJsonPathsWithValues(searchResponse, expected);
    }

    @Test
    public void addAddress_whenMultipleUsersShareTheSameAddress_shouldReturnExpected() throws IOException, InterruptedException {
        final String userId1 = createUser("create_user_valid_7.json");
        final String userId2 = createUser("create_user_valid_8.json");
        final String addressId = createAddress("create_address_valid_10.json");

        linkAddressWithUser(userId1, addressId);
        linkAddressWithUser(userId2, addressId);
    }

    @Test
    public void addAddress_whenMultipleAddressesToSameUser_shouldReturnExpected() throws IOException, InterruptedException {
        final String userId = createUser("create_user_valid_9.json");
        final String addressId1 = createAddress("create_address_valid_11.json");
        final String addressId2 = createAddress("create_address_valid_12.json");

        linkAddressWithUser(userId, addressId1);
        linkAddressWithUser(userId, addressId2);
    }

}
