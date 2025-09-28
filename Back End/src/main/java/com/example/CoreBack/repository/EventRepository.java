package com.example.CoreBack.repository;

import com.example.CoreBack.entity.StoredEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<StoredEvent, Long> {
}