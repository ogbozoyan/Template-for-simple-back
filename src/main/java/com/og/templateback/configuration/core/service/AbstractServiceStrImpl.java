package com.og.templateback.configuration.core.service;

import com.og.templateback.configuration.core.entity.AbstractEntityStr;
import com.og.templateback.configuration.core.repository.AbstractRepositoryStr;
import com.og.templateback.configuration.core.specification.SearchSpecification;
import com.og.templateback.configuration.core.specification.request.SearchRequest;
import com.og.templateback.configuration.core.utils.Reflection;
import com.og.templateback.configuration.core.web.controller.advice.exception.*;
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
 * @since 18.04.2023
 */
@Data
@RequiredArgsConstructor
@Slf4j
public class AbstractServiceStrImpl<E extends AbstractEntityStr, R extends AbstractRepositoryStr<E>> implements AbstractServiceStr<E> {

    @Autowired
    protected ModelMapper defaultMapper;

    protected final R repository;
    @PersistenceContext
    protected EntityManager entityManager;

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

    @Override
    @Transactional
    public E update(E entity) throws UpdateException {

        E entityFromBd = repository.findById(entity.getId()).orElseThrow(() -> new FindException("Entity not found " + entity));
        defaultMapper.map(entity, entityFromBd);
        try {
            return repository.saveAndFlush(entityFromBd);
        } catch (Exception e) {
            e.printStackTrace();
            throw new UpdateException(e.getClass().getSimpleName() + " Can't update entity: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void delete(String id) throws DeleteException {
        try {
            E entity = repository.findById(id).orElseThrow(() -> new FindException("Entity not found with id: " + id));
            repository.delete(entity);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DeleteException(e.getClass().getSimpleName() + " Can't delete with id: " + id + " " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public E findById(String id) {
        return repository.findById(id).orElseThrow(() -> new FindException(" Entity not found with id " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiPaginationResponse findAll(Integer reqPage, Integer reqSize) {
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

    @Override
    @Transactional(readOnly = true)
    public ApiPaginationResponse searchFilter(SearchRequest request) throws FilterException {
        try {
            SearchSpecification<E> specification = new SearchSpecification<>(request);
            Pageable pageable = SearchSpecification.getPageable(request.getPage(), request.getSize());
            Page<E> page = repository.findAll(specification, pageable);
            return new ApiPaginationResponse(page.getContent(), page.getTotalElements(), page.getTotalPages());
        } catch (Exception e) {
            e.printStackTrace();
            throw new FilterException(e.getClass().getSimpleName() + " Filter exception: " + e.getMessage());
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

            try {
                originalEntity.getDeclaredField(fieldName);
            } catch (Exception e) {
                throw new FindException("Invalid field name: " + fieldName);
            }

            return repository.getAllDistinctFields(fieldName, originalEntity.getSimpleName(), entityManager);
        } catch (Exception e) {
            e.printStackTrace();
            throw new FindException(e);
        }
    }
}
