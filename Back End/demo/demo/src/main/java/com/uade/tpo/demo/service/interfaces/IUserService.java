package com.uade.tpo.demo.service.interfaces;

import com.uade.tpo.demo.models.objects.User;
import com.uade.tpo.demo.models.requests.UpdateUserRequest;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

public interface IUserService {
    List<User> getUsers();
    Optional<User> getUserById(Long userId);
    Optional<User> getUserByEmail(String email);
    Optional<User> getUserByUsername(String username);
    void deleteUser(Long userId);
    void saveUser(User user);
    
    User updateUser(Principal principal, UpdateUserRequest updateUserRequest);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    
    List<String> getUsernames();
}