package com.og.templateback.configuration.core.service;

import com.og.templateback.configuration.core.entity.AbstractEntity;
import com.og.templateback.configuration.core.repository.AbstractRepository;
import com.og.templateback.configuration.core.specification.SearchSpecification;
import com.og.templateback.configuration.core.specification.request.SearchRequest;
import com.og.templateback.configuration.core.utils.Reflection;
import com.og.templateback.configuration.core.web.controller.advice.exception.DeleteException;
import com.og.templateback.configuration.core.web.controller.advice.exception.FindException;
import com.og.templateback.configuration.core.web.controller.advice.exception.SaveException;
import com.og.templateback.configuration.core.web.controller.advice.exception.UpdateException;
import com.og.templateback.configuration.core.web.dto.ApiPaginationResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * An abstract implementation of the AbstractService interface.
 *
 * @param <E> The entity type.
 * @param <R> The repository type.
 * @author ogbozoyan
 * @since 08.02.2023
 */
@Data
@RequiredArgsConstructor
@Slf4j
public abstract class AbstractServiceImpl<E extends AbstractEntity, R extends AbstractRepository<E>> implements AbstractService<E> {

    @Autowired
    protected ModelMapper defaultMapper;

    protected final R repository;

    @PersistenceContext
    protected EntityManager entityManager;


    /**
     * Saves the given entity to the database.
     *
     * @param entity The entity to save.
     * @return The saved entity.
     * @throws SaveException if an error occurs during the save operation.
     */
    @Override
    @Transactional
    public E save(E entity) throws SaveException {
        try {
            return repository.save(entity);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SaveException(e.getClass().getSimpleName() + " Can't save entity: " + e.getMessage());
        }
    }

    /**
     * Updates the given entity in the database.
     *
     * @param entity The entity to update.
     * @return The updated entity.
     * @throws UpdateException if an error occurs during the update operation.
     */
    @Override
    @Transactional
    public E update(E entity) throws UpdateException, FindException {

        E entityFromBd = repository.findById(entity.getId()).orElseThrow(() -> new FindException("Entity not found " + entity));
        defaultMapper.map(entity, entityFromBd);
        try {
            return repository.saveAndFlush(entityFromBd);
        } catch (Exception e) {
            e.printStackTrace();
            throw new UpdateException(e.getClass().getSimpleName() + " Can't update entity: " + e.getMessage());
        }
    }

    /**
     * Deletes the entity with the given ID from the database.
     *
     * @param id The ID of the entity to delete.
     * @throws DeleteException if an error occurs during the delete operation.
     */
    @Override
    @Transactional
    public void delete(Long id) throws DeleteException {
        try {
            E entity = repository.findById(id).orElseThrow(() -> new FindException("Entity not found with id: " + id));
            repository.delete(entity);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DeleteException(e.getClass().getSimpleName() + " Can't delete with id: " + id + " " + e.getMessage());
        }
    }

    /**
     * Retrieves the entity with the given ID from the database.
     *
     * @param id The ID of the entity to retrieve.
     * @return The retrieved entity.
     * @throws FindException if the entity is not found.
     */
    @Override
    @Transactional(readOnly = true)
    public E findById(Long id) throws FindException {
        return repository.findById(id).orElseThrow(() -> new FindException(" Entity not found with id " + id));
    }

    /**
     * Retrieves all entities from the database with pagination support.
     *
     * @param reqPage The reqPage number (0-based index).
     * @param reqSize The number of items per reqPage.
     * @return An ApiPaginationResponse object containing the list of entities and pagination information.
     * @throws FindException if an error occurs during the pagination operation.
     */
    @Override
    @Transactional(readOnly = true)
    public ApiPaginationResponse findAll(Integer reqPage, Integer reqSize) throws FindException {
        try {
            int page;
            int size;

            size = reqSize == null ? 10 : Math.abs(reqSize);
            page = reqPage == null ? 1 : Math.abs(reqPage);

            PageRequest request = PageRequest.of(page, size);
            Page<E> pageResponse = repository.findAll(request);
            return new ApiPaginationResponse(pageResponse.getContent(), pageResponse.getTotalElements(), pageResponse.getTotalPages());
        } catch (Exception e) {
            e.printStackTrace();
            throw new FindException(e.getClass().getSimpleName() + " Find Exception: " + e.getMessage());
        }
    }

    /**
     * Performs a search query with filters based on the given SearchRequest.
     *
     * @param request The SearchRequest containing the search criteria.
     * @return An ApiPaginationResponse object containing the search results and pagination information.
     * @throws FindException if an error occurs during the search query execution.
     */
    @Override
    @Transactional(readOnly = true)
    public ApiPaginationResponse searchFilter(SearchRequest request) throws FindException {
        try {
            SearchSpecification<E> specification = new SearchSpecification<>(request);
            Pageable pageable = SearchSpecification.getPageable(request.getPage(), request.getSize());
            var page = repository.findAll(specification, pageable);
            return new ApiPaginationResponse(page.getContent(), page.getTotalElements(), page.getTotalPages());
        } catch (Exception e) {
            e.printStackTrace();
            throw new FindException(e.getClass().getSimpleName() + " Filter find exception: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<E> findAll() throws FindException {
        try {
            return repository.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            throw new FindException(e.getClass().getSimpleName() + " Find Exception: " + e.getMessage());
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public List<?> getAllUniqueValuesFromField(String fieldName) throws FindException {
        try {
            Class<?> originalEntity = Class.forName(
                    Reflection.getClassGeneric(this).getName()
            );

            if (!fieldName.equalsIgnoreCase("id")) {
                try {
                    originalEntity.getDeclaredField(fieldName);
                } catch (Exception e) {
                    throw new FindException("Invalid field name: " + fieldName);
                }
            }

            return repository.getAllDistinctFields(fieldName, originalEntity.getSimpleName(), entityManager);
        } catch (Exception e) {
            e.printStackTrace();
            throw new FindException(e);
        }
    }
}
