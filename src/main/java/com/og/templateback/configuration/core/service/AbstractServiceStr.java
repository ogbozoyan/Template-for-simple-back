package com.og.templateback.configuration.core.service;


import com.og.templateback.configuration.core.specification.request.SearchRequest;
import com.og.templateback.configuration.core.web.controller.advice.exception.*;
import com.og.templateback.configuration.core.web.dto.ApiPaginationResponse;
import com.og.templateback.configuration.core.entity.AbstractEntityStr;

import java.util.List;

/**
 * The AbstractService interface defines the contract for service classes that handle CRUD operations on entities.
 * It provides methods for saving, updating, deleting, and retrieving entities, as well as performing search and pagination.
 *
 * @param <T> The type of entity that the service operates on, must extend AbstractEntity.
 * @author ogbozoyan
 * @since 18.04.2023
 */
public interface AbstractServiceStr<T extends AbstractEntityStr> {
    T save(T entity) throws SaveException;

    T update(T entity) throws UpdateException;


    void delete(String id) throws DeleteException;

    T findById(String id);

    List<T> findAll() throws FindException;

    ApiPaginationResponse findAll(Integer page, Integer size);

    ApiPaginationResponse searchFilter(SearchRequest request) throws FilterException;

    List<?> getAllUniqueValuesFromField(String fieldName) throws FindException;
}
