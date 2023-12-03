package com.og.templateback.configuration.core.specification.enums;


import com.og.templateback.configuration.core.specification.request.FilterRequest;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This is a logical for predicate of Criteria API likes EQUAL, NOT_EQUAL, LIKE, IN, and BETWEEN.
 *
 * @author ogbozoyan
 * @since 01.03.2023
 */
@Slf4j
public enum Operator {
    /**
     * EQUAL  >	SELECT * FROM table WHERE field = ?
     */
    EQUAL { //String Long

        public Predicate build(Root<?> root, CriteriaBuilder cb, FilterRequest request, Predicate predicate, Path<?> path) {
            Object value = request.getFieldType().parse(request.getValue().toString());
            return cb.and(cb.equal(path.as(value.getClass()), value), predicate);
        }
    },
    /**
     * NOT_EQUAL >	SELECT * FROM table WHERE field != ?
     */
    NOT_EQUAL {
        public Predicate build(Root<?> root, CriteriaBuilder cb, FilterRequest request, Predicate predicate, Path<?> path) {
            Object value = request.getFieldType().parse(request.getValue().toString());
            return cb.and(cb.notEqual(path.as(value.getClass()), value), predicate);
        }
    },
    /**
     * LIKE  >	SELECT * FROM table WHERE field LIKE '%?%'
     */
    LIKE { //String

        public Predicate build(Root<?> root, CriteriaBuilder cb, FilterRequest request, Predicate predicate, Path<?> path) {
            return cb.and(cb.like(cb.lower(path.as(String.class)), "%" + request.getValue().toString().toLowerCase() + "%"), predicate);
        }
    },
    /**
     * LIKE_ANY >SELECT * FROM table WHERE field LIKE '%value1%' OR field LIKE '%value2%' OR field LIKE '%value3%' ...
     */
    LIKE_ANY {
        public Predicate build(Root<?> root, CriteriaBuilder cb, FilterRequest request, Predicate predicate, Path<?> path) {
            Expression<String> key = root.get(request.getKey());
            List<String> values = request.getValues().stream()
                    .map(Object::toString)
                    .toList();
            List<Predicate> likePredicates = new ArrayList<>();
            for (String value : values) {
                likePredicates.add(cb.like(cb.lower(key), "%" + value.toLowerCase() + "%"));
            }
            return cb.and(cb.or(likePredicates.toArray(new Predicate[0])), predicate);
        }
    },
    /**
     * IN	> SELECT * FROM table WHERE field IN (?)
     */
    IN { //String Long Data

        public Predicate build(Root<?> root, CriteriaBuilder cb, FilterRequest request, Predicate predicate, Path<?> path) {
            List<Object> values = request.getValues();
            CriteriaBuilder.In<Object> inClause = cb.in(path.as(values.get(0).getClass()));
            for (Object value : values) {
                if (request.getFieldType() != null) {
                    inClause.value(request.getFieldType().parse(value.toString()));
                } else inClause.value(value.toString());
            }
            return cb.and(inClause, predicate);
        }
    },
    /**
     * BETWEEN	> SELECT * FROM table WHERE field >= ? AND field <= ?
     */
    BETWEEN { //Data

        public Predicate build(Root<?> root, CriteriaBuilder cb, FilterRequest request, Predicate predicate, Path<?> path) {
            Object value = request.getFieldType().parse(request.getValue().toString());
            Object valueTo = request.getFieldType().parse(request.getValueTo().toString());
            if (request.getFieldType() == FieldType.TIMESTAMP) {
                Timestamp startDate = (Timestamp) value;
                Timestamp endDate = (Timestamp) valueTo;
                Expression<Timestamp> key = root.get(request.getKey());
                return cb.and(cb.and(cb.greaterThanOrEqualTo(key, startDate), cb.lessThanOrEqualTo(key, endDate)), predicate);
            }
            if (request.getFieldType() == FieldType.DATE) {
                Date startDate = (Date) value;
                Date endDate = (Date) valueTo;
                Expression<Date> key = root.get(request.getKey());
                return cb.and(cb.and(cb.greaterThanOrEqualTo(key, startDate), cb.lessThanOrEqualTo(key, endDate)), predicate);
            }
            if (request.getFieldType() == FieldType.STRING) {
                String startDate = (String) value;
                String endDate = (String) valueTo;
                Expression<String> key = root.get(request.getKey());
                return cb.and(cb.and(cb.greaterThanOrEqualTo(key, startDate), cb.lessThanOrEqualTo(key, endDate)), predicate);
            }

            if (request.getFieldType() != FieldType.BOOLEAN) {
                Number start = (Number) request.getValue();
                Number end = (Number) request.getValueTo();
                Expression<Number> key = root.get(request.getKey());
                return cb.and(cb.and(cb.ge(key, start), cb.le(key, end)), predicate);
            }

            log.info("Can not use between for {} field type.", request.getFieldType());
            return predicate;
        }
    },

    IS_NULL {
        @Override
        public Predicate build(Root<?> root, CriteriaBuilder cb, FilterRequest request, Predicate predicate, Path<?> path) {
            return cb.and(cb.isNull(path), predicate);
        }
    },
    IS_NOT_NULL {
        @Override
        public Predicate build(Root<?> root, CriteriaBuilder cb, FilterRequest request, Predicate predicate, Path<?> path) {
            return cb.and(cb.isNotNull(path), predicate);
        }
    };

    public abstract Predicate build(Root<?> root, CriteriaBuilder cb, FilterRequest request, Predicate predicate, Path<?> path);

}