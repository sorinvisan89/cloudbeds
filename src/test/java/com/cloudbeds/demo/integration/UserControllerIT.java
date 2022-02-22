package com.cloudbeds.demo.integration;

import com.google.common.collect.ImmutableMap;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class UserControllerIT {

    private static final int PORT = 8080;
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";

    private HttpClient httpClient;

    @BeforeEach
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
                                UserControllerIT.class.getResourceAsStream("/add_user_valid_1.json")))
                .build();

        final HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertHttpResponseStatusCodeInSuccessRange(response);

        final Map<String, String> expected = ImmutableMap.of(
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
                                UserControllerIT.class.getResourceAsStream("/add_user_invalid.json")))
                .build();

        final HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void addUser_whenEmailAlreadyRegistered_shouldReturnError() throws IOException, InterruptedException {

        final HttpRequest request = HttpRequest.newBuilder()
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .uri(buildCreateUserRequestUrl())
                .POST(
                        HttpRequest.BodyPublishers.ofInputStream(() ->
                                UserControllerIT.class.getResourceAsStream("/add_user_not_unique_email.json")))
                .build();

        // First request should pass
        final HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // This should fail since we already have the email in the DB
        final HttpResponse<String> secondResponse = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(secondResponse.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @Test
    public void addAddressToUser_whenValidInput_shouldReturnExpected() throws IOException, InterruptedException {

        final HttpRequest userRequest = HttpRequest.newBuilder()
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .uri(buildCreateUserRequestUrl())
                .POST(
                        HttpRequest.BodyPublishers.ofInputStream(() ->
                                UserControllerIT.class.getResourceAsStream("/add_user_valid_2.json")))
                .build();

        final HttpResponse<String> userResponse = this.httpClient.send(userRequest, HttpResponse.BodyHandlers.ofString());
        assertHttpResponseStatusCodeInSuccessRange(userResponse);

        final String createdPath = userResponse.headers().allValues("location")
                .stream()
                .findFirst()
                .orElse(null);

        assertThat(createdPath).isNotEqualTo(null);
        final String userId = createdPath.substring(createdPath.lastIndexOf("/") + 1);

        final HttpRequest addressRequest = HttpRequest.newBuilder()
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .uri(buildAddAddressToUserRequestUrl(userId))
                .POST(
                        HttpRequest.BodyPublishers.ofInputStream(() ->
                                UserControllerIT.class.getResourceAsStream("/add_address_valid.json")))
                .build();

        final HttpResponse<String> addressResponse = this.httpClient.send(addressRequest, HttpResponse.BodyHandlers.ofString());
        assertHttpResponseStatusCodeInSuccessRange(addressResponse);

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

        assertJsonPathsWithValues(addressResponse, expected);
    }

    private HttpClient createHttpClient() {
        return HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofMillis(2000))
                .build();
    }

    private void assertHttpResponseStatusCodeInSuccessRange(final HttpResponse<String> response) {
        assertThat(response.statusCode()).isBetween(200, 299);
    }

    private URI buildCreateUserRequestUrl() {
        return UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(PORT)
                .path("user")
                .build()
                .toUri();
    }

    private URI buildAddAddressToUserRequestUrl(final String userId) {
        return UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(PORT)
                .pathSegment("user", userId, "address")
                .build()
                .toUri();
    }

    private void assertJsonPathsWithValues(final HttpResponse<String> response, final Map<String, String> expectedValues) {
        final String responseBody = response.body();

        expectedValues.forEach((jsonPath, expectedValue) -> {
            final String actualValue = JsonPath.parse(responseBody).read(jsonPath);
            org.hamcrest.MatcherAssert.assertThat(actualValue, equalTo(expectedValue));
        });
    }
}
