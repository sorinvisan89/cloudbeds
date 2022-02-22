package com.cloudbeds.demo.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class TestUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private TestUtils() {

    }

    public static <T> T readFromFixture(final String fixtureName, final TypeReference<T> typeReference) throws IOException {
        final URL url = Resources.getResource(fixtureName);
        final String file = url.getFile();
        try (InputStream reader = new FileInputStream(file)) {
            return objectMapper.readValue(reader, typeReference);
        }
    }
}
