package com.example.CoreBack.service;

import com.example.CoreBack.entity.RetryPolicy;
import com.example.CoreBack.entity.RetryPolicyDTO;
import com.example.CoreBack.entity.SystemConfigDTO;
import com.example.CoreBack.entity.ModulePolicyDTO;
import com.example.CoreBack.repository.RetryPolicyRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ConfigService {

    private final RetryPolicyRepository retryPolicyRepository;

    // Variables temporales para probar en memoria
    private SystemConfigDTO systemConfig = new SystemConfigDTO();
    private final Map<String, String> modulePolicies = new HashMap<>();

    public ConfigService(RetryPolicyRepository retryPolicyRepository) {
        this.retryPolicyRepository = retryPolicyRepository;
    }

    // ================================
    // Retry Policies
    // ================================

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

    public void deleteRetryPolicy(Long id) {
        retryPolicyRepository.deleteById(id);
    }

    // ================================
    // System Configuration
    // ================================

    public void saveSystemConfig(SystemConfigDTO dto) {
        this.systemConfig = dto;
    }

    public SystemConfigDTO getSystemConfig() {
        return this.systemConfig;
    }

    // ================================
    // Module Policies
    // ================================

    public void assignPolicyToModule(ModulePolicyDTO dto) {
        modulePolicies.put(dto.getModuleName(), dto.getPolicyName());
    }

    public List<Map<String, String>> getModulesAndPolicies() {
        List<Map<String, String>> result = new ArrayList<>();
        for (Map.Entry<String, String> entry : modulePolicies.entrySet()) {
            Map<String, String> map = new HashMap<>();
            map.put("module", entry.getKey());
            map.put("policy", entry.getValue());
            result.add(map);
        }
        return result;
    }
}