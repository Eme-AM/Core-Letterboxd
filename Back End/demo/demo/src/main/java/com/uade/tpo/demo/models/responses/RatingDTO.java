package com.uade.tpo.demo.models.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Data transfer object for rating information")
public class RatingDTO {
  @Schema(description = "Unique identifier of the rating", example = "1")
  private Long id;

  @Schema(description = "User information who created the rating")
  private UserDTOReduced user;

  @Schema(description = "Numeric value of the rating (1-5)", example = "5")
  private Integer ratingValue;

  @Schema(description = "Additional comments for the rating", example = "Amazing movie!")
  private String comments;

  @Schema(description = "ID of the entity being rated", example = "123")
  private Long entityId;

  @Schema(description = "Type of entity being rated", example = "MOVIE")
  private String entityType;

  @Schema(description = "Date when the rating was created")
  private LocalDateTime createdAt;

  @Schema(description = "Date when the rating was last updated")
  private LocalDateTime updatedAt;
}
