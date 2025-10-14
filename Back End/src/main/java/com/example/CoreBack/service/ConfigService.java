package com.example.CoreBack.service;

import com.example.CoreBack.entity.RetryPolicy;
import com.example.CoreBack.entity.RetryPolicyDTO;
import com.example.CoreBack.repository.RetryPolicyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConfigService {

    private final RetryPolicyRepository retryPolicyRepository;

    public ConfigService(RetryPolicyRepository retryPolicyRepository) {
        this.retryPolicyRepository = retryPolicyRepository;
    }

    // Crear
    public RetryPolicyDTO createRetryPolicy(RetryPolicyDTO dto) {
        RetryPolicy policy = new RetryPolicy();
        policy.setName(dto.getName());
        policy.setMinDelay(dto.getMinDelay());
        policy.setMaxDelay(dto.getMaxDelay());
        policy.setMaxTries(dto.getMaxTries());
        policy.setBackoffMultiplier(dto.getBackoffMultiplier());
        policy.setEnabled(dto.isEnabled());

        RetryPolicy saved = retryPolicyRepository.save(policy);
        dto.setId(saved.getId());
        return dto;
    }

    // Listar
    public List<RetryPolicyDTO> listRetryPolicies() {
        return retryPolicyRepository.findAll().stream()
                .map(p -> {
                    RetryPolicyDTO dto = new RetryPolicyDTO();
                    dto.setId(p.getId());
                    dto.setName(p.getName());
                    dto.setMinDelay(p.getMinDelay());
                    dto.setMaxDelay(p.getMaxDelay());
                    dto.setMaxTries(p.getMaxTries());
                    dto.setBackoffMultiplier(p.getBackoffMultiplier());
                    dto.setEnabled(p.isEnabled());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // Obtener por ID
    public RetryPolicyDTO getRetryPolicy(Long id) {
        RetryPolicy policy = retryPolicyRepository.findById(id).orElse(null);
        if (policy == null) return null;

        RetryPolicyDTO dto = new RetryPolicyDTO();
        dto.setId(policy.getId());
        dto.setName(policy.getName());
        dto.setMinDelay(policy.getMinDelay());
        dto.setMaxDelay(policy.getMaxDelay());
        dto.setMaxTries(policy.getMaxTries());
        dto.setBackoffMultiplier(policy.getBackoffMultiplier());
        dto.setEnabled(policy.isEnabled());
        return dto;
    }

    // Actualizar
    public RetryPolicyDTO updateRetryPolicy(Long id, RetryPolicyDTO dto) {
        RetryPolicy policy = retryPolicyRepository.findById(id).orElseThrow();

        policy.setName(dto.getName());
        policy.setMinDelay(dto.getMinDelay());
        policy.setMaxDelay(dto.getMaxDelay());
        policy.setMaxTries(dto.getMaxTries());
        policy.setBackoffMultiplier(dto.getBackoffMultiplier());
        policy.setEnabled(dto.isEnabled());

        retryPolicyRepository.save(policy);
        dto.setId(id);
        return dto;
    }

    // Eliminar
    public void deleteRetryPolicy(Long id) {
        retryPolicyRepository.deleteById(id);
    }
}