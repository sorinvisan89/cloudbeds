package com.cloudbeds.demo.exception.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static java.util.UUID.randomUUID;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorResponse {

    private String id;
    private String message;
    private String path;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ValidationError> validationErrors;

    public static ErrorResponse errorResponse(String message, String path) {
        return new ErrorResponse(randomUUID().toString(), message, path, null);
    }

    public static ErrorResponse errorResponse(String message, List<ValidationError> validationErrors, String path) {
        return new ErrorResponse(randomUUID().toString(), message, path, validationErrors);
    }


}
