package com.cloudbeds.demo.exception.handler;

import com.cloudbeds.demo.exception.custom.AddressNotFoundException;
import com.cloudbeds.demo.exception.custom.DuplicateAddressException;
import com.cloudbeds.demo.exception.custom.EmailAlreadyRegisteredException;
import com.cloudbeds.demo.exception.custom.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

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

    @ExceptionHandler({EmailAlreadyRegisteredException.class, DuplicateAddressException.class})
    @ResponseStatus(CONFLICT)
    @ResponseBody
    public ErrorResponse handleDuplicates(final Exception exception, final WebRequest webRequest) {
        logError(exception, webRequest);
        return ErrorResponse.errorResponse(exception.getMessage(), fullUrl(webRequest));
    }

    @ExceptionHandler({UserNotFoundException.class, AddressNotFoundException.class})
    @ResponseStatus(NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleNotFound(final Exception exception, final WebRequest webRequest) {
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
