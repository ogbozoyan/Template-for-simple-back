package com.og.templateback.configuration.core.repository;

import com.og.templateback.configuration.core.entity.AbstractEntity;
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
 * @since 08.02.2023
 */
@NoRepositoryBean
public interface AbstractRepository<T extends AbstractEntity> extends JpaRepository<T, Long>, JpaSpecificationExecutor<T> {

    @SuppressWarnings("SqlSourceToSinkFlow")
    default List<?> getAllDistinctFields(String fieldName, String entityClassName, EntityManager entityManager) {
        return entityManager.createQuery(
                        //unsafe
                        "SELECT DISTINCT e." + fieldName + " FROM " + entityClassName + " e"
                )
                .getResultList();
    }
}
