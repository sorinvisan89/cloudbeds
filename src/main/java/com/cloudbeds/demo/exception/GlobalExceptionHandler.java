package com.cloudbeds.demo.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleMethodArgumentNotValid(final MethodArgumentNotValidException exception, final WebRequest webRequest) {
        logError(exception, webRequest);
        final List<ValidationError> validationErrors = ValidationError.fromException(exception);
        return ErrorResponse.errorResponse("Validation error", validationErrors, fullUrl(webRequest));
    }

    @ExceptionHandler(EmailAlreadyRegisteredException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleUserAlreadyRegistered(final EmailAlreadyRegisteredException exception, final WebRequest webRequest) {
        logError(exception, webRequest);
        return ErrorResponse.errorResponse(exception.getMessage(), fullUrl(webRequest));
    }

    private static String fullUrl(final WebRequest webRequest) {
        return webRequest.toString();
    }

    private void logError(final Exception exception, final WebRequest webRequest) {
        final String errorMessage = String.format("Rest error message: %s at path %s", exception.getMessage(), fullUrl(webRequest));
        log.error(errorMessage, exception);
    }
}
