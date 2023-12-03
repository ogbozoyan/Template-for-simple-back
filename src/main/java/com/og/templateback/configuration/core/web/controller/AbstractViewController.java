package com.og.templateback.configuration.core.web.controller;

import com.og.templateback.configuration.core.specification.request.SearchRequest;
import com.og.templateback.configuration.core.web.controller.advice.exception.FilterException;
import com.og.templateback.configuration.core.web.dto.ApiPaginationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author ogbozoyan
 * @date 18.06.2023
 */
@SuppressWarnings("SpellCheckingInspection")
public interface AbstractViewController {

    @Operation(summary = "Поиск по фильтрам", security = @SecurityRequirement(name = "JWT"))
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("filter")
    ResponseEntity<ApiPaginationResponse> searchFilter(@RequestBody SearchRequest request) throws FilterException;


}
