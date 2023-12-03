package com.og.templateback.configuration.core.specification;

import com.og.templateback.configuration.core.specification.request.FilterRequest;
import com.og.templateback.configuration.core.specification.request.SearchRequest;
import com.og.templateback.configuration.core.specification.request.SortRequest;
import com.og.templateback.configuration.core.web.controller.advice.exception.FilterException;
import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A generic class that implements the Specification interface and constructs the actual query based on the provided constraints.
 *
 * @param <T> The entity type for which the query is being constructed.
 * @author ogbozoyan
 * @since 01.03.2023
 */
@Slf4j
@AllArgsConstructor
public class SearchSpecification<T> implements Specification<T> {
    public static final int DEFAULT_ITEMS_SIZE = 10;
    @Serial
    private static final long serialVersionUID = -9153865343320750644L;

    /**
     * The search request containing filters and sorts.
     */
    private final transient SearchRequest request;

    /**
     * Constructs the predicate (criteria) for the query based on the provided filters and sorts.
     *
     * @param root  The root entity in the query.
     * @param query The criteria query being constructed.
     * @param cb    The criteria builder to build predicates and expressions.
     * @return The predicate representing the constructed criteria for the query.
     */
    @SneakyThrows
    @Override
    public Predicate toPredicate(@NonNull Root<T> root, @NonNull CriteriaQuery<?> query, @NonNull CriteriaBuilder cb) {
        try {
            Predicate predicate = cb.equal(cb.literal(Boolean.TRUE), Boolean.TRUE);
            List<Order> orders = new ArrayList<>();
            for (FilterRequest filter : this.request.getFilters()) {
                predicate = filter.getOperator().build(
                        root,
                        cb,
                        filter,
                        predicate,
                        buildAbsolutePath(root, filter.getKey())
                );
            }

            for (SortRequest sort : this.request.getSorts()) {
                String[] nestedFields = sort.getKey().split("\\.");
                Path<?> path = root.get(nestedFields[0]);
                for (int i = 1; i < nestedFields.length; i++) {
                    path = path.get(nestedFields[i]);
                }
                orders.add(sort.getDirection().build(root, cb, sort, path));
            }

            query.orderBy(orders);
            log.debug("Applied filter: {} and sort: {}", this.request.getFilters(), this.request.getSorts());
            return predicate;
        } catch (Exception e) {
            e.printStackTrace();
            throw new FilterException(e.getClass().getSimpleName() + " Can't filter : " + e.getMessage());
        }
    }

    /**
     * Creates and append dynamically joins based on every node of provided @param key and concatenate all joins together
     *
     * @param root Root entity
     * @param key  path to attribute
     * @return Absolute Path to nested path from @param key
     */
    private Path<?> buildAbsolutePath(Root<T> root, String key) {
        //Container which is contains <ALL SUB_NODES OF ABSOLUTE PATH, SUB_NODE JOINS OF CURRENT SUB_NODE>
        Map<String, Join<Object, Object>> mapOfJoins = new Hashtable<>();

        //if we don't go to nested path
        if (!key.contains(".")) {
            return root.get(key);
        } else {

            //array of all nodes
            String[] path = key.split("\\.");
            int pathSize = path.length;

            //first node
            String subPathNode = path[0];

            /*
            if join don't exist in map -> add new join
            if (mapOfJoins.get(subPathNode) == null) {
                mapOfJoins.put(subPathNode, root.join(subPathNode));
            }
            */

            // initialize our  map of joins
            mapOfJoins.put(subPathNode, root.join(subPathNode));

            //iterating through nodes and creating one mega Join which contains all nodes
            for (int i = 0; i < pathSize - 1; i++) {
                subPathNode = Stream.of(path)
                        .limit(i + 1)
                        .collect(
                                Collectors.joining(".")
                        );

                //if map don't contain new node Join -> concatenate previous Join with new node Join
                if (mapOfJoins.get(subPathNode) == null) {
                    String prevPath =
                            Stream.of(path)
                                    .limit(i)
                                    .collect(
                                            Collectors.joining(".")
                                    );

                    Join<Object, Object> prevJoin = mapOfJoins.get(prevPath);
                    mapOfJoins.put(
                            subPathNode,
                            prevJoin.join(path[i])
                    );
                }
            }

            Join<Object, Object> finalJoin = mapOfJoins.get(subPathNode);
            String lastAttribute = path[pathSize - 1];

            return finalJoin.get(lastAttribute);
        }
    }

    /**
     * Creates a Pageable object for pagination based on the provided page number and size.
     * Sets default values if the parameters are null or invalid.
     *
     * @param page The page number for pagination (1-based index).
     * @param size The number of items per page.
     * @return A Pageable object representing the pagination configuration.
     */
    public static Pageable getPageable(Integer page, Integer size) {
        size = size == null ? DEFAULT_ITEMS_SIZE : size;
        page = page == null ? 1 : page;
        page = page == 0 ? 1 : page;
        return PageRequest.of(page - 1, size);
    }

}
