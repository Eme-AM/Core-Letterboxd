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
    
    // Búsqueda con filtros para el dashboard
    @Query("SELECT e FROM EventMessage e WHERE " +
           "(:eventType IS NULL OR e.eventType = :eventType) AND " +
           "(:sourceModule IS NULL OR e.sourceModule = :sourceModule) AND " +
           "(:status IS NULL OR e.status = :status) AND " +
           "(:from IS NULL OR e.createdAt >= :from) AND " +
           "(:to IS NULL OR e.createdAt <= :to)")
    Page<EventMessage> findWithFilters(
            @Param("eventType") String eventType,
            @Param("sourceModule") String sourceModule,
            @Param("status") EventStatus status,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable
    );
    
    // Búsqueda con más filtros para el EventHubService existente
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
    
    // Eventos fallidos para reintento
    List<EventMessage> findByStatusAndRetryCountLessThan(EventStatus status, int maxRetries);
    
    // Eventos por estado
    List<EventMessage> findByStatus(EventStatus status);
    
    // Estadísticas
    long countByStatus(EventStatus status);
    
    @Query("SELECT COUNT(e) FROM EventMessage e WHERE e.sourceModule = :sourceModule")
    long countBySourceModule(@Param("sourceModule") String sourceModule);
    
    @Query("SELECT AVG(CAST(e.processedAt AS double) - CAST(e.createdAt AS double)) FROM EventMessage e WHERE e.status = :status AND e.processedAt IS NOT NULL")
    Double getAverageProcessingTimeByStatus(@Param("status") EventStatus status);
    
    // Eventos por procesar
    List<EventMessage> findByStatusOrderByCreatedAtAsc(EventStatus status);
    
    // Búsqueda por tipo de evento
    Page<EventMessage> findByEventType(String eventType, Pageable pageable);
    
    // Búsqueda por módulo origen
    Page<EventMessage> findBySourceModule(String sourceModule, Pageable pageable);
    
    // Estadísticas adicionales
    @Query("SELECT COUNT(e) FROM EventMessage e WHERE e.createdAt >= :since")
    Long countEventsSince(@Param("since") LocalDateTime since);
    
    @Query("SELECT AVG(CAST(e.processedAt AS double) - CAST(e.createdAt AS double)) " +
           "FROM EventMessage e WHERE e.status = 'DELIVERED' AND e.processedAt IS NOT NULL")
    Double getAverageProcessingTimeInSeconds();
}
