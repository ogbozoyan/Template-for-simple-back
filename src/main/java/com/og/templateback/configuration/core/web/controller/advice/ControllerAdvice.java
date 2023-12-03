package com.og.templateback.configuration.core.web.controller.advice;

import com.og.templateback.configuration.core.web.controller.advice.exception.AbstractInternalException;
import com.og.templateback.configuration.core.web.controller.advice.exception.AbstractNotFoundException;
import com.og.templateback.configuration.core.web.controller.advice.exception.CustomErrorMessage;
import com.og.templateback.configuration.core.web.controller.advice.exception.FilterException;
import feign.FeignException;
import feign.RetryableException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.SemanticException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

/**
 * To dynamic handling 500 need to extend  AbstractInternalException, for 404 AbstractNotFoundException
 *
 * @author ogbozoyan
 * @since 20.10.2023
 */
@SuppressWarnings("FieldCanBeLocal")
@RestControllerAdvice
@Slf4j
public class ControllerAdvice {
    private final Integer INTERNAL_SERVER_ERROR = 500;
    private final Integer NOT_FOUND = 404;
    private final Integer BAD_REQUEST = 400;
    private final Integer UNAUTHORIZED = 401;
    private final HttpHeaders headers = new HttpHeaders();

    {
        headers.add("Content-Type", "application/json");
    }

    /**
     * 400 Exception handler
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<CustomErrorMessage> handleMethodArgumentTypeMismatch(Exception ex, WebRequest req) {
        CustomErrorMessage errorMessage = CustomErrorMessage.builder()
                .statusCode(BAD_REQUEST)
                .timestamp(new Date())
                .message(ex.getMessage())
                .description(req.getDescription(false))
                .exceptionName(ex.getClass().getName())
                .build();
        log.debug("ControllerAdvice: {} {}", req.getRemoteUser(), errorMessage);
        return new ResponseEntity<>(errorMessage, headers, HttpStatus.BAD_REQUEST);
    }

    /**
     * 500 Exception handler
     */
    @ExceptionHandler({
            IOException.class,
            AbstractInternalException.class,
            FilterException.class,
            DataIntegrityViolationException.class,
            ConstraintViolationException.class,
            InvocationTargetException.class,
            SemanticException.class,
            InvalidDataAccessApiUsageException.class
    })
    public ResponseEntity<CustomErrorMessage> handleInternalException(Exception ex, WebRequest req) {

        CustomErrorMessage errorMessage = CustomErrorMessage.builder()
                .statusCode(INTERNAL_SERVER_ERROR)
                .timestamp(new Date())
                .message(ex.getMessage())
                .description(req.getDescription(false))
                .exceptionName(ex.getClass().getName())
                .build();
        log.info("ControllerAdvice: {} {}", req.getRemoteUser(), errorMessage);
        return new ResponseEntity<>(errorMessage, headers, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 404 Exception handler
     */
    @ExceptionHandler(AbstractNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<CustomErrorMessage> notFoundExceptionHandler(Exception ex, WebRequest req) {
        CustomErrorMessage errorMessage = CustomErrorMessage.builder()
                .statusCode(NOT_FOUND)
                .timestamp(new Date())
                .message(ex.getMessage())
                .description(req.getDescription(false))
                .exceptionName(ex.getClass().getName())
                .build();
        return new ResponseEntity<>(errorMessage, headers, HttpStatus.NOT_FOUND);
    }

    /**
     * Feign client exception handler
     */
    @ExceptionHandler({FeignException.FeignClientException.class, FeignException.class, RetryableException.class})
    public ResponseEntity<CustomErrorMessage> handleFeignException(Exception ex, WebRequest req) {
        CustomErrorMessage errorMessage;
        if (ex instanceof FeignException.NotFound) {
            errorMessage = CustomErrorMessage.builder()
                    .statusCode(NOT_FOUND)
                    .timestamp(new Date())
                    .message(ex.getMessage())
                    .description(req.getDescription(false))
                    .exceptionName(ex.getClass().getName())
                    .build();
            log.debug("ControllerAdvice: {} {}", req.getRemoteUser(), errorMessage);
            return new ResponseEntity<>(errorMessage, headers, HttpStatus.NOT_FOUND);

        } else if (ex instanceof FeignException.BadRequest) {
            errorMessage = CustomErrorMessage.builder()
                    .statusCode(BAD_REQUEST)
                    .timestamp(new Date())
                    .message(ex.getMessage())
                    .description(req.getDescription(false))
                    .exceptionName(ex.getClass().getName())
                    .build();
            log.debug("ControllerAdvice: {} {}", req.getRemoteUser(), errorMessage);
            return new ResponseEntity<>(errorMessage, headers, HttpStatus.BAD_REQUEST);
        } else if (ex instanceof FeignException.Unauthorized) {
            errorMessage = CustomErrorMessage.builder()
                    .statusCode(UNAUTHORIZED)
                    .timestamp(new Date())
                    .message("Wrong credentials or smth else happened. Error message: " + ex.getMessage())
                    .description(req.getDescription(false))
                    .exceptionName(ex.getClass().getName())
                    .build();
            log.debug("ControllerAdvice: {} {}", req.getRemoteUser(), errorMessage);
            return new ResponseEntity<>(errorMessage, headers, HttpStatus.UNAUTHORIZED);
        } else {
            errorMessage = CustomErrorMessage.builder()
                    .statusCode(INTERNAL_SERVER_ERROR)
                    .timestamp(new Date())
                    .message(ex.getMessage())
                    .description(req.getDescription(false))
                    .exceptionName(ex.getClass().getName())
                    .build();
        }
        log.debug("ControllerAdvice: {} {}", req.getRemoteUser(), errorMessage);
        return new ResponseEntity<>(errorMessage, headers, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
