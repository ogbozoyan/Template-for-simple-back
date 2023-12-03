package com.og.templateback.configuration.core.repository;

import com.og.templateback.configuration.core.entity.AbstractEntityStr;
import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

/**
 * An abstract repository interface that extends JpaRepository and JpaSpecificationExecutor.
 *
 * @param <T> The entity type.
 * @author ogbozoyan
 * @since 18.04.2023
 */
@NoRepositoryBean
public interface AbstractRepositoryStr<T extends AbstractEntityStr> extends JpaRepository<T, String>,
        JpaSpecificationExecutor<T> {
    default List<?> getAllDistinctFields(String fieldName, String entityClassName, EntityManager entityManager) {
        return entityManager.createQuery(
                        "SELECT DISTINCT e." + fieldName + " FROM " + entityClassName + " e"
                )
                .getResultList();
    }
}
