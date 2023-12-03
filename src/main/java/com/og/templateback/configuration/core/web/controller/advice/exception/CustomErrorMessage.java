package com.og.templateback.configuration.core.web.controller.advice.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * @author ogbozoyan
 * @since 17.02.2023
 */
@Data
@AllArgsConstructor
@Builder
public class CustomErrorMessage {
    private Integer statusCode;
    private Date timestamp;
    private String message;
    private String description;
    private String exceptionName;
}
