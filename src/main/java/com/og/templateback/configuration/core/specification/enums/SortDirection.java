package com.og.templateback.configuration.core.specification.enums;

import com.og.templateback.configuration.core.specification.request.SortRequest;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;


/**
 * This is used when need to sort result query. It can be ascending or descending direction.
 *
 * @author ogbozoyan
 * @date 01.03.2023
 */
@Slf4j
public enum SortDirection {
    /**
     * ASC	SELECT * FROM table ORDER BY field ASC
     */
    ASC {
        public Order build(Root<?> root, CriteriaBuilder cb, SortRequest request, Path<?> path) {
            return cb.asc(path);
        }
    },
    /**
     * DESC	SELECT * FROM table ORDER BY field DESC
     */
    DESC {
        public Order build(Root<?> root, CriteriaBuilder cb, SortRequest request, Path<?> path) {
            return cb.desc(path);
        }
    };

    public abstract Order build(Root<?> root, CriteriaBuilder cb, SortRequest request, Path<?> path);

}
