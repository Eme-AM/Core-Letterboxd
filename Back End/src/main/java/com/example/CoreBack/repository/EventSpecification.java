package com.example.CoreBack.repository;

import com.example.CoreBack.entity.StoredEvent;
import org.springframework.data.jpa.domain.Specification;

public class EventSpecification {

    public static Specification<StoredEvent> hasModule(String module) {
        return (root, query, cb) -> module == null ? null :
                cb.equal(root.get("source"), module);
    }

    public static Specification<StoredEvent> hasStatus(String status) {
        return (root, query, cb) -> status == null ? null :
                cb.equal(root.get("status"), status);
    }

    public static Specification<StoredEvent> hasSearch(String search) {
        return (root, query, cb) -> search == null ? null :
                cb.like(cb.lower(root.get("type")), "%" + search.toLowerCase() + "%");
    }
}