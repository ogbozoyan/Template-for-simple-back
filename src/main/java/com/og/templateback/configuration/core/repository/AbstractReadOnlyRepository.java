package com.og.templateback.configuration.core.repository;

import com.og.templateback.configuration.core.entity.AbstractViewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author ogbozoyan
 * @since 18.06.2023
 */
@NoRepositoryBean
@Transactional(readOnly = true)
public interface AbstractReadOnlyRepository<E extends AbstractViewEntity> extends JpaRepository<E, Long>, JpaSpecificationExecutor<E> {
}
