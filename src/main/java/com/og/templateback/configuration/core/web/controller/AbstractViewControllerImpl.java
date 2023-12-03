package com.og.templateback.configuration.core.web.controller;

import com.og.templateback.configuration.core.entity.AbstractViewEntity;
import com.og.templateback.configuration.core.service.AbstractViewService;
import com.og.templateback.configuration.core.specification.request.SearchRequest;
import com.og.templateback.configuration.core.web.controller.advice.exception.FilterException;
import com.og.templateback.configuration.core.web.dto.ApiPaginationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author ogbozoyan
 * @date 18.06.2023
 */
@SuppressWarnings("unused")
@RequiredArgsConstructor
public class AbstractViewControllerImpl<E extends AbstractViewEntity, S extends AbstractViewService> implements AbstractViewController {

    protected final S service;


    /**
     * Performs a search and filtering operation on entities.
     *
     * @param request The SearchRequest object containing the search criteria.
     * @return The ResponseEntity containing the search results.
     */
    @Override
    public ResponseEntity<ApiPaginationResponse> searchFilter(@RequestBody SearchRequest request) throws FilterException {
        return ResponseEntity.ok(service.searchFilter(request));
    }

}
