package com.cloudbeds.demo.exception.handler;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.Errors;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidationError {

    private String field;
    private String message;

    public static ValidationError validationError(String field, String message) {
        return new ValidationError(field, message);
    }

    public static List<ValidationError> fromException(MethodArgumentNotValidException exception) {
        return Optional.of(exception.getBindingResult())
                .map(Errors::getFieldErrors)
                .filter(fieldErrors -> !fieldErrors.isEmpty())
                .map(fieldErrorList ->
                        fieldErrorList.stream()
                                .map(fieldError -> {
                                    String field = fieldError.getField();
                                    String errorMessage = fieldError.getDefaultMessage();

                                    return validationError(field, errorMessage);
                                }).collect(Collectors.toList())
                ).orElse(null);
    }

}
