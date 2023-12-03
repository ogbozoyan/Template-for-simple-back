package com.og.templateback.configuration.core.web.controller;

import com.og.templateback.configuration.core.web.controller.advice.exception.DeleteException;
import com.og.templateback.configuration.core.web.controller.advice.exception.UpdateException;
import com.og.templateback.configuration.core.entity.AbstractEntity;
import com.og.templateback.configuration.core.service.AbstractService;
import com.og.templateback.configuration.core.specification.request.SearchRequest;
import com.og.templateback.configuration.core.web.controller.advice.exception.FindException;
import com.og.templateback.configuration.core.web.controller.advice.exception.SaveException;
import com.og.templateback.configuration.core.web.dto.ApiPaginationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


/**
 * An abstract implementation of the AbstractController interface.
 *
 * @param <E> The entity type.
 * @param <S> The service type.
 * @author ogbozoyan
 * @apiNote This implementation provides basic CRUD operations and utilizes the  @ToLogger aspect as a pointcut to log the method invocations.
 * @since 08.02.2023
 */
@RequiredArgsConstructor
public abstract class AbstractControllerImpl<E extends AbstractEntity, S extends AbstractService<E>>
        implements AbstractController<E> {

    protected final S service;

    /**
     * Retrieves a page of entities.
     *
     * @param page The page number.
     * @param size The page size.
     * @return The ResponseEntity containing the page of entities.
     */
    @Override
    public ResponseEntity<ApiPaginationResponse> getPage(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) throws FindException {
        return ResponseEntity.ok(service.findAll(page, size));
    }

    /**
     * Retrieves a single entity by ID.
     *
     * @param id The ID of the entity.
     * @return The ResponseEntity containing the entity.
     */
    @Override
    public ResponseEntity<E> getOne(@PathVariable Long id) throws FindException {
        return ResponseEntity.ok(service.findById(id));
    }

    /**
     * Performs a search and filtering operation on entities.
     *
     * @param request The SearchRequest object containing the search criteria.
     * @return The ResponseEntity containing the search results.
     */
    @Override
    public ResponseEntity<ApiPaginationResponse> searchFilter(@RequestBody SearchRequest request) throws FindException {
        return ResponseEntity.ok(service.searchFilter(request));
    }

    /**
     * Updates an existing entity.
     *
     * @param update The updated entity object.
     * @return The ResponseEntity containing the updated entity.
     */
    @Override
    public ResponseEntity<E> update(@RequestBody E update) throws FindException, UpdateException {
        return ResponseEntity.ok(service.update(update));
    }

    /**
     * Creates a new entity.
     *
     * @param create The entity to be created.
     * @return The ResponseEntity containing the created entity.
     */
    @Override
    public ResponseEntity<E> create(@RequestBody E create) throws SaveException {
        return ResponseEntity.ok(service.save(create));
    }

    /**
     * Deletes an entity by ID.
     *
     * @param id The ID of the entity to be deleted.
     */
    @Override
    public void delete(@PathVariable Long id) throws DeleteException {
        service.delete(id);
    }

    @Override
    public ResponseEntity<List<E>> getAll() throws FindException {
        return ResponseEntity.ok(service.findAll());
    }

    @Override
    public ResponseEntity<List<?>> findAllUniqueByFieldName(@RequestParam String fieldName) throws FindException {
        return ResponseEntity.ok(service.getAllUniqueValuesFromField(fieldName));
    }
}