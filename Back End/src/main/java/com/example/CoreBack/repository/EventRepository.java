package com.example.CoreBack.repository;

import com.example.CoreBack.entity.StoredEvent;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<StoredEvent, Long>, JpaSpecificationExecutor<StoredEvent> {
    
    List<StoredEvent> findTop100ByStatusInAndNextAttemptAtBeforeOrderByNextAttemptAtAsc(
        Collection<String> statuses, LocalDateTime before
    );
    
}
