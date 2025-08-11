package com.uade.tpo.demo.models.objects;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ratings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rating {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  // Generic reference to any entity that can be rated (movies, reviews, etc.)
  @Column(name = "entity_id")
  private Long entityId;

  @Column(name = "entity_type")
  private String entityType; // "MOVIE", "REVIEW", etc.

  @Column(name = "rating_value")
  private Integer ratingValue; // 1-5 stars

  @Column(name = "comments", length = 1000)
  private String comments;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }
}