package com.cloudbeds.demo.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpResponse;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

public class TestUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private TestUtils() {

    }

    public static <T> T readFromFixture(final String fixtureName, final TypeReference<T> typeReference) throws IOException {
        try (InputStream reader = TestUtils.class.getResourceAsStream("/" + fixtureName)) {
            return objectMapper.readValue(reader, typeReference);
        }
    }

    public static String extractIdFromHeaders(final HttpResponse<String> response) {
        final String createdPath = response.headers().allValues("location")
                .stream()
                .findFirst()
                .orElse(null);

        assertThat(createdPath, notNullValue());
        return createdPath.substring(createdPath.lastIndexOf("/") + 1);
    }
}
