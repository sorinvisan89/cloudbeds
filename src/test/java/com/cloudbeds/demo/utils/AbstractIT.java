package com.cloudbeds.demo.utils;

import com.cloudbeds.demo.integration.UserIT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

import static com.cloudbeds.demo.utils.TestUtils.extractIdFromHeaders;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public abstract class AbstractIT {

    protected static final int PORT = 8080;
    protected static final String CONTENT_TYPE = "Content-Type";
    protected static final String APPLICATION_JSON = "application/json";

    protected HttpClient httpClient;

    protected static final ObjectMapper objectMapper = new ObjectMapper();


    protected HttpClient createHttpClient() {
        return HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofMillis(2000))
                .build();
    }

    protected void assertHttpResponseStatusCodeInSuccessRange(final HttpResponse<String> response) {
        assertThat(response.statusCode(), greaterThanOrEqualTo(200));
        assertThat(response.statusCode(), lessThanOrEqualTo(299));
    }

    protected URI buildCreateUserRequestUrl() {
        return UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(PORT)
                .path("user")
                .build()
                .toUri();
    }

    protected URI buildCreateAddressRequestUrl() {
        return UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(PORT)
                .pathSegment("address")
                .build()
                .toUri();
    }

    protected URI buildAddAddressToUserRequestUrl(final String userId) {
        return UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(PORT)
                .pathSegment("user", userId, "address")
                .build()
                .toUri();
    }

    protected URI buildSearchCountryRequestUrl(final String country) {
        return UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(PORT)
                .path("users")
                .queryParam("country", country)
                .build()
                .toUri();
    }


    protected void assertJsonPathsWithValues(final HttpResponse<String> response, final Map<String, String> expectedValues) {
        final String responseBody = response.body();

        expectedValues.forEach((jsonPath, expectedValue) -> {
            final String actualValue = JsonPath.parse(responseBody).read(jsonPath);
            assertThat(actualValue, equalTo(expectedValue));
        });
    }

    protected void assertErrorMessage(final String responseBody, final String jsonPath, final String expectedValue) {
        final String actualValue = JsonPath.parse(responseBody).read(jsonPath);
        assertThat(actualValue, equalTo(expectedValue));
    }

    protected String createUser(final String fixtureName) throws IOException, InterruptedException {
        final HttpRequest request = HttpRequest.newBuilder()
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .uri(buildCreateUserRequestUrl())
                .POST(
                        HttpRequest.BodyPublishers.ofInputStream(() ->
                                UserIT.class.getResourceAsStream("/" + fixtureName)))
                .build();

        final HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode(), equalTo(HttpStatus.CREATED.value()));
        return extractIdFromHeaders(response);
    }

    protected String createAddress(final String fixtureName) throws IOException, InterruptedException {
        final HttpRequest request = HttpRequest.newBuilder()
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .uri(buildCreateAddressRequestUrl())
                .POST(
                        HttpRequest.BodyPublishers.ofInputStream(() ->
                                UserIT.class.getResourceAsStream("/" + fixtureName)))
                .build();

        final HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode(), equalTo(HttpStatus.CREATED.value()));
        return extractIdFromHeaders(response);
    }

    protected String createRequestBodyForAddAddress(final String addressId) throws IOException {

        try (InputStream inputStream = UserIT.class.getResourceAsStream("/add_address_template.json")) {
            assertThat(inputStream, notNullValue());
            final String input = new String(inputStream.readAllBytes());
            return input.replaceFirst("ADDRESS_ID_PLACEHODER", addressId);
        }

    }

    protected void linkAddressWithUser(final String userId, final String addressId) throws IOException, InterruptedException {
        final HttpRequest addUserAddressRequest = HttpRequest.newBuilder()
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .uri(buildAddAddressToUserRequestUrl(userId))
                .POST(
                        HttpRequest.BodyPublishers.ofString(createRequestBodyForAddAddress(addressId))
                )
                .build();

        final HttpResponse<String> addAddressResponse = this.httpClient.send(addUserAddressRequest, HttpResponse.BodyHandlers.ofString());
        assertHttpResponseStatusCodeInSuccessRange(addAddressResponse);
    }

}
