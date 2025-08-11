package com.uade.tpo.demo.service;

import com.uade.tpo.demo.models.objects.User;
import com.uade.tpo.demo.models.responses.UserDTOReduced;
import com.uade.tpo.demo.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

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
}