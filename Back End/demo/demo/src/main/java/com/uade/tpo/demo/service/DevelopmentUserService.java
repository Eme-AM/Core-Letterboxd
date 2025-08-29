package com.uade.tpo.demo.service;

import com.uade.tpo.demo.service.interfaces.IUserService;
import com.uade.tpo.demo.models.objects.User;
import com.uade.tpo.demo.models.requests.UpdateUserRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@ConditionalOnMissingBean(IUserService.class)
public class DevelopmentUserService implements IUserService {
    
    @Override
    public List<User> getUsers() {
        return Collections.emptyList();
    }
    
    @Override
    public Optional<User> getUserById(Long userId) {
        return Optional.empty();
    }
    
    @Override
    public Optional<User> getUserByEmail(String email) {
        return Optional.empty();
    }
    
    @Override
    public Optional<User> getUserByUsername(String username) {
        return Optional.empty();
    }
    
    @Override
    public void deleteUser(Long userId) {
        // Stub implementation for development
    }
    
    @Override
    public void saveUser(User user) {
        // Stub implementation for development
    }
    
    @Override
    public User updateUser(Principal principal, UpdateUserRequest updateUserRequest) {
        return new User(); // Stub implementation
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return false;
    }
    
    @Override
    public boolean existsByUsername(String username) {
        return false;
    }
    
    @Override
    public List<String> getUsernames() {
        return Collections.emptyList();
    }
}
