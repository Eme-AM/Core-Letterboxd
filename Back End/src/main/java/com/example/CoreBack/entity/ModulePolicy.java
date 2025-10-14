package com.example.CoreBack.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ModulePolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String moduleName;

    @ManyToOne
    @JoinColumn(name = "policy_id")
    private RetryPolicy policy;
}