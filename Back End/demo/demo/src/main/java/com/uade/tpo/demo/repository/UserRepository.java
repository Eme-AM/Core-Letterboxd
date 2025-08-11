package com.uade.tpo.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uade.tpo.demo.models.objects.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByEmail(String email);

  Optional<User> findByUsername(String username);

  Optional<User> findByEmailOrUsername(String email, String username);
  
  boolean existsByEmail(String email);
  
  boolean existsByUsername(String username);
}
