package com.og.templateback.configuration.core.service;


import com.og.templateback.configuration.core.specification.request.SearchRequest;
import com.og.templateback.configuration.core.web.controller.advice.exception.DeleteException;
import com.og.templateback.configuration.core.web.controller.advice.exception.FindException;
import com.og.templateback.configuration.core.web.controller.advice.exception.SaveException;
import com.og.templateback.configuration.core.web.controller.advice.exception.UpdateException;
import com.og.templateback.configuration.core.web.dto.ApiPaginationResponse;
import com.og.templateback.configuration.core.entity.AbstractEntity;

import java.util.List;

/**
 * The AbstractService interface defines the contract for service classes that handle CRUD operations on entities.
 * It provides methods for saving, updating, deleting, and retrieving entities, as well as performing search and pagination.
 *
 * @param <E> The type of entity that the service operates on, must extend AbstractEntity.
 * @author ogbozoyan
 * @since 08.02.2023
 */
public interface AbstractService<E extends AbstractEntity> {
    E save(E entity) throws SaveException;

    E update(E entity) throws UpdateException, FindException;

    void delete(Long id) throws DeleteException;

    E findById(Long id) throws FindException;

    List<E> findAll() throws FindException;

    ApiPaginationResponse findAll(Integer page, Integer size) throws FindException;

    ApiPaginationResponse searchFilter(SearchRequest request) throws FindException;

    List<?> getAllUniqueValuesFromField(String fieldName) throws FindException;

}