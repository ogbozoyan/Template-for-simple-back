package com.og.templateback.configuration.core.service;

import com.og.templateback.configuration.core.entity.AbstractViewEntity;
import com.og.templateback.configuration.core.repository.AbstractReadOnlyRepository;
import com.og.templateback.configuration.core.specification.SearchSpecification;
import com.og.templateback.configuration.core.specification.request.SearchRequest;
import com.og.templateback.configuration.core.web.controller.advice.exception.FilterException;
import com.og.templateback.configuration.core.web.dto.ApiPaginationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author ogbozoyan
 * @since 18.06.2023
 */
@RequiredArgsConstructor
public class AbstractViewServiceImpl<E extends AbstractViewEntity, R extends AbstractReadOnlyRepository<E>> implements AbstractViewService {

    protected final R repository;


    /**
     * Performs a search query with filters based on the given SearchRequest.
     *
     * @param request The SearchRequest containing the search criteria.
     * @return An ApiPaginationResponse object containing the search results and pagination information.
     * @throws FilterException if an error occurs during the search query execution.
     */
    @Override
    @Transactional(readOnly = true)
    public ApiPaginationResponse searchFilter(SearchRequest request) throws FilterException {
        try {
            SearchSpecification<E> specification = new SearchSpecification<>(request);
            Pageable pageable = SearchSpecification.getPageable(request.getPage(), request.getSize());
            var page = repository.findAll(specification, pageable);
            return new ApiPaginationResponse(page.getContent(), page.getTotalElements(), page.getTotalPages());
        } catch (Exception e) {
            e.printStackTrace();
            throw new FilterException(e.getClass().getSimpleName() + " Filter exception: " + e.getMessage());
        }
    }

}
