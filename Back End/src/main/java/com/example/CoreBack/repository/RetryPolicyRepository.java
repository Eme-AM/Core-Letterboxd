package com.example.CoreBack.repository;

import com.example.CoreBack.entity.RetryPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RetryPolicyRepository extends JpaRepository<RetryPolicy, Long> {
}