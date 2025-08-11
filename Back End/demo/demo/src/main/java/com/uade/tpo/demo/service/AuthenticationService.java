package com.uade.tpo.demo.service;

import com.uade.tpo.demo.exceptions.ExistingUserException;
import com.uade.tpo.demo.models.objects.User;
import com.uade.tpo.demo.models.enums.Role;
import com.uade.tpo.demo.models.requests.AuthenticationRequest;
import com.uade.tpo.demo.models.requests.RegisterRequest;
import com.uade.tpo.demo.models.responses.AuthenticationResponse;
import com.uade.tpo.demo.repository.UserRepository;
import com.uade.tpo.demo.controllers.config.JwtService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final MailService mailService;

    public AuthenticationResponse register(RegisterRequest request) {
        // Check if user already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ExistingUserException("User with email " + request.getEmail() + " already exists");
        }

        // Create user
        var user = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userRepository.save(user);
        
        var jwtToken = jwtService.generateToken(user);
        
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();
                
        var jwtToken = jwtService.generateToken(user);
        
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public void recoverPassword(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            // Generate verification code and send email
            String verificationCode = generateVerificationCode();
            // Store verification code (you might want to add this field to User entity)
            try {
                mailService.sendPasswordRecoveryEmail(email, verificationCode);
            } catch (Exception e) {
                // Log error but don't reveal if user exists or not
                System.err.println("Error sending password recovery email: " + e.getMessage());
            }
        }
        // Don't reveal if user exists or not
    }

    public void resetPassword(String email, String verificationCode, String newPassword) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Verify the code (implement verification logic)
            // For now, we'll just update the password
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
        }
    }

    private String generateVerificationCode() {
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }
}