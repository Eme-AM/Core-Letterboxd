package com.uade.tpo.demo.repository;

import com.uade.tpo.demo.models.objects.EventMessage;
import com.uade.tpo.demo.models.objects.EventMessage.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventMessageRepository extends JpaRepository<EventMessage, Long> {

    List<EventMessage> findByStatus(EventStatus status);
    
    List<EventMessage> findByStatusAndRetryCountLessThan(EventStatus status, Integer maxRetries);
    
    Page<EventMessage> findByEventType(String eventType, Pageable pageable);
    
    Page<EventMessage> findBySourceModule(String sourceModule, Pageable pageable);
    
    Page<EventMessage> findByTargetModule(String targetModule, Pageable pageable);
    
    Page<EventMessage> findByCorrelationId(String correlationId, Pageable pageable);
    
    @Query("SELECT e FROM EventMessage e WHERE " +
           "(:eventType IS NULL OR e.eventType = :eventType) AND " +
           "(:sourceModule IS NULL OR e.sourceModule = :sourceModule) AND " +
           "(:targetModule IS NULL OR e.targetModule = :targetModule) AND " +
           "(:status IS NULL OR e.status = :status) AND " +
           "(:correlationId IS NULL OR e.correlationId = :correlationId) AND " +
           "(:fromDate IS NULL OR e.createdAt >= :fromDate) AND " +
           "(:toDate IS NULL OR e.createdAt <= :toDate)")
    Page<EventMessage> findByFilters(
        @Param("eventType") String eventType,
        @Param("sourceModule") String sourceModule,
        @Param("targetModule") String targetModule,
        @Param("status") EventStatus status,
        @Param("correlationId") String correlationId,
        @Param("fromDate") LocalDateTime fromDate,
        @Param("toDate") LocalDateTime toDate,
        Pageable pageable
    );
    
    // Stats queries
    Long countByStatus(EventStatus status);
    
    @Query("SELECT COUNT(e) FROM EventMessage e WHERE e.createdAt >= :since")
    Long countEventsSince(@Param("since") LocalDateTime since);
    
    @Query("SELECT COUNT(e) FROM EventMessage e WHERE e.sourceModule = :module")
    Long countBySourceModule(@Param("module") String module);
    
    @Query("SELECT AVG(FUNCTION('TIMESTAMPDIFF', SECOND, e.createdAt, e.processedAt)) " +
           "FROM EventMessage e WHERE e.status = 'DELIVERED' AND e.processedAt IS NOT NULL")
    Double getAverageProcessingTimeInSeconds();
}
