package com.og.templateback.configuration.core.service;


import com.og.templateback.configuration.core.specification.request.SearchRequest;
import com.og.templateback.configuration.core.web.controller.advice.exception.FilterException;
import com.og.templateback.configuration.core.web.dto.ApiPaginationResponse;

/**
 * @author ogbozoyan
 * @since 18.06.2023
 */
public interface AbstractViewService {
    ApiPaginationResponse searchFilter(SearchRequest request) throws FilterException;

}
