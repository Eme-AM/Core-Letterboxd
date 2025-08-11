package com.uade.tpo.demo.models.objects;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.uade.tpo.demo.models.enums.Role;

@Entity
@Builder
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "email", unique = true, length = 150) 
  private String email;

  @Column(name = "username", nullable = false, length = 100) 
  private String username;

  @Column(name = "password")
  private String password;

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "last_name")
  private String lastName;

  @Column(name = "name")
  private String name;

  @Column(name = "bio", length = 500)
  private String bio;

  @Column(name = "avatar")
  private String avatar;

  @Column(name = "country")
  private String country;

  @Column(name = "website")
  private String website;

  @Enumerated(EnumType.STRING)
  @Column(name = "role")
  private Role role;

  @Column(name = "enabled")
  @Builder.Default
  private Boolean enabled = true;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Rating> ratings;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
    if (enabled == null) {
      enabled = true;
    }
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return enabled != null && enabled;
  }
}