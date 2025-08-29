package com.uade.tpo.demo.service;

import com.uade.tpo.demo.models.objects.User;
import com.uade.tpo.demo.models.requests.UpdateUserRequest;
import com.uade.tpo.demo.models.responses.UserDTOReduced;
import com.uade.tpo.demo.repository.UserRepository;
import com.uade.tpo.demo.service.interfaces.IUserService;

import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService implements IUserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDTOReduced> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToReducedDTO)
                .collect(Collectors.toList());
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    private UserDTOReduced convertToReducedDTO(User user) {
        UserDTOReduced dto = new UserDTOReduced();
        dto.setId(getUserId(user));
        dto.setUsername(getUserUsername(user));
        dto.setEmail(getUserEmail(user));
        dto.setFirstName(getUserFirstName(user));
        dto.setLastName(getUserLastName(user));
        return dto;
    }

    // Helper methods to access User fields (workaround for Lombok issues)
    private Long getUserId(User user) {
        try {
            return (Long) user.getClass().getMethod("getId").invoke(user);
        } catch (Exception e) {
            return null;
        }
    }

    private String getUserEmail(User user) {
        try {
            return (String) user.getClass().getMethod("getEmail").invoke(user);
        } catch (Exception e) {
            return null;
        }
    }

    private String getUserUsername(User user) {
        try {
            return (String) user.getClass().getMethod("getUsername").invoke(user);
        } catch (Exception e) {
            return null;
        }
    }

    private String getUserFirstName(User user) {
        try {
            return (String) user.getClass().getMethod("getFirstName").invoke(user);
        } catch (Exception e) {
            return null;
        }
    }

    private String getUserLastName(User user) {
        try {
            return (String) user.getClass().getMethod("getLastName").invoke(user);
        } catch (Exception e) {
            return null;
        }
    }

    // Implementation of IUserService methods
    
    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }
    
    @Override
    public Optional<User> getUserByEmail(String email) {
        return findByEmail(email);
    }
    
    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
    
    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }
    
    @Override
    public User updateUser(Principal principal, UpdateUserRequest updateUserRequest) {
        // Basic implementation - you can enhance this as needed
        Optional<User> userOpt = getUserByEmail(principal.getName());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Update user fields from updateUserRequest
            return userRepository.save(user);
        }
        throw new RuntimeException("User not found");
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    @Override
    public List<String> getUsernames() {
        return userRepository.findAll().stream()
                .map(this::getUserUsername)
                .collect(Collectors.toList());
    }
}