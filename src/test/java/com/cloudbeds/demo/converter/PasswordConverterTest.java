package com.cloudbeds.demo.converter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class PasswordConverterTest {

    private PasswordConverter passwordConverter;

    @BeforeEach
    public void setup() {
        passwordConverter = new PasswordConverter("MySuperSecretKey");
    }


    private static Stream<Arguments> provideParametersForConvertToDatabaseColumnTest() {
        return Stream.of(
                Arguments.of("a-small-password", "tU+bETjKd1Ph9DPO+CSprado+mdGKSgOMcFcvJwfXas="),
                Arguments.of("", "p2j6Z0YpKA4xwVy8nB9dqw=="),
                Arguments.of("  ", "codBJWznf3qbgIWPqSx/oQ=="),
                Arguments.of("password", "ZulhwB27UKBqJPYk2f9UQg=="),
                Arguments.of("admin123", "4ysYhMkL63/8JJxPhHIz8Q==")
        );
    }

    private static Stream<Arguments> provideParametersForConvertToEntityAttributeTest() {
        return Stream.of(
                Arguments.of("tU+bETjKd1Ph9DPO+CSprado+mdGKSgOMcFcvJwfXas=", "a-small-password"),
                Arguments.of("p2j6Z0YpKA4xwVy8nB9dqw==", ""),
                Arguments.of("codBJWznf3qbgIWPqSx/oQ==", "  "),
                Arguments.of("ZulhwB27UKBqJPYk2f9UQg==", "password"),
                Arguments.of("4ysYhMkL63/8JJxPhHIz8Q==", "admin123")
        );
    }


    @ParameterizedTest
    @MethodSource("provideParametersForConvertToDatabaseColumnTest")
    public void convertToDatabaseColumn_shouldReturnExpectedValuesForInput(final String input, final String expected) {
        final String actual = passwordConverter.convertToDatabaseColumn(input);
        assertThat(expected, equalTo(actual));
    }

    @ParameterizedTest
    @MethodSource("provideParametersForConvertToEntityAttributeTest")
    public void convertToEntityAttribute_shouldReturnExpectedValuesForInput(final String input, final String expected) {
        final String actual = passwordConverter.convertToEntityAttribute(input);
        assertThat(expected, equalTo(actual));
    }
}
