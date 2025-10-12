package com.example.CoreBack.service;

import com.example.CoreBack.entity.ModulePolicy;
import com.example.CoreBack.repository.ModulePolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModulePolicyService {

    @Autowired
    private ModulePolicyRepository repository;

    public List<ModulePolicy> findAll() {
        return repository.findAll();
    }

    public ModulePolicy save(ModulePolicy modulePolicy) {
        return repository.save(modulePolicy);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}