package com.cloudbeds.demo.integration;

import com.cloudbeds.demo.utils.AbstractIT;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class AddressIT extends AbstractIT {

    @Before
    public void setup() {
        this.httpClient = createHttpClient();
    }

    @Test
    public void addAddress_whenValidInput_shouldReturnExpected() throws IOException, InterruptedException {
        final HttpRequest request = HttpRequest.newBuilder()
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .uri(buildCreateAddressRequestUrl())
                .POST(
                        HttpRequest.BodyPublishers.ofInputStream(() ->
                                UserIT.class.getResourceAsStream("/create_address_valid_8.json")))
                .build();

        final HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertHttpResponseStatusCodeInSuccessRange(response);

        final Map<String, String> expected = Map.of(
                "$.addressLine1", "Hacienta 100",
                "$.city", "Cancun",
                "$.state", "Yucatan",
                "$.country", "Mexico",
                "$.zip", "MX-600"
        );

        assertJsonPathsWithValues(response, expected);
    }

    @Test
    public void addAddress_whenZipCodeAlreadyExists_shouldReturnError() throws IOException, InterruptedException {
        // First add the address
        final HttpRequest request = HttpRequest.newBuilder()
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .uri(buildCreateAddressRequestUrl())
                .POST(
                        HttpRequest.BodyPublishers.ofInputStream(() ->
                                UserIT.class.getResourceAsStream("/create_address_valid_9.json")))
                .build();

        final HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertHttpResponseStatusCodeInSuccessRange(response);

        // Try to add again the address
        final HttpResponse<String> failedResponse = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(failedResponse.statusCode(), equalTo(HttpStatus.CONFLICT.value()));
        assertErrorMessage(failedResponse.body(), "$.message", "An address already exists for this zip code: 'IND-300'!");
    }
}
