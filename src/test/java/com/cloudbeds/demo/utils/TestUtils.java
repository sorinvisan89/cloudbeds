package com.cloudbeds.demo.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

public class TestUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private TestUtils() {

    }

    public static <T> T readFromFixture(final String fixtureName, final TypeReference<T> typeReference) throws IOException {
        try (InputStream reader = TestUtils.class.getResourceAsStream("/" + fixtureName)) {
            return objectMapper.readValue(reader, typeReference);
        }
    }
}
