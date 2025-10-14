package com.example.CoreBack.service;

import com.example.CoreBack.entity.RetryPolicy;
import com.example.CoreBack.repository.RetryPolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RetryPolicyService {

    @Autowired
    private RetryPolicyRepository repository;

    public List<RetryPolicy> findAll() {
        return repository.findAll();
    }

    public RetryPolicy findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public RetryPolicy save(RetryPolicy policy) {
        return repository.save(policy);
    }

    public RetryPolicy update(Long id, RetryPolicy policy) {
        RetryPolicy existing = repository.findById(id).orElseThrow();
        existing.setName(policy.getName());
        existing.setMinDelay(policy.getMinDelay());
        existing.setMaxDelay(policy.getMaxDelay());
        existing.setMaxTries(policy.getMaxTries());
        existing.setBackoffMultiplier(policy.getBackoffMultiplier());
        existing.setEnabled(policy.isEnabled());
        return repository.save(existing);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}