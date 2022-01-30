package com.madvena.shorturl.controller;

import com.madvena.shorturl.exception.LinkNotFoundException;
import com.madvena.shorturl.exception.WrongLinkException;
import com.madvena.shorturl.model.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> processValidationError(HttpServletRequest request, Exception exception) {
        return createErrorResponse(request, BAD_REQUEST, exception);
    }

    @ExceptionHandler(LinkNotFoundException.class)
    public ResponseEntity<ErrorResponse> onLinkNotFoundException(HttpServletRequest request, Exception exception) {
        return createErrorResponse(request, HttpStatus.NOT_FOUND, exception);
    }

    @ExceptionHandler(WrongLinkException.class)
    public ResponseEntity<ErrorResponse> onWrongLinkException(HttpServletRequest request, Exception exception) {
        return createErrorResponse(request, HttpStatus.BAD_REQUEST, exception);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> defaultExceptionHandler(HttpServletRequest request, Exception exception) {
        return createErrorResponse(request, HttpStatus.INTERNAL_SERVER_ERROR, exception);
    }

    private ResponseEntity<ErrorResponse> createErrorResponse(
            HttpServletRequest request,
            HttpStatus status,
            Exception exception
    ) {
        if (status.is5xxServerError()) {
            logger.error("unhandled exception caught", exception);
        } else {
            logger.warn(exception.getClass().getName() + ": " + exception.getMessage());
        }
        return ResponseEntity.status(status).body(new ErrorResponse(
                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                request.getRequestURL().toString(),
                exception.getLocalizedMessage(),
                status.value()
        ));
    }
}
