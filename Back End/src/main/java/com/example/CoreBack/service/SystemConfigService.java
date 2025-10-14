package com.example.CoreBack.service;

import com.example.CoreBack.entity.SystemConfig;
import com.example.CoreBack.repository.SystemConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SystemConfigService {

    @Autowired
    private SystemConfigRepository repository;

    public SystemConfig getConfig() {
        return repository.findById(1L).orElseGet(() -> {
            SystemConfig config = new SystemConfig();
            return repository.save(config);
        });
    }

    public SystemConfig update(SystemConfig config) {
        config.setId(1L);
        return repository.save(config);
    }
}