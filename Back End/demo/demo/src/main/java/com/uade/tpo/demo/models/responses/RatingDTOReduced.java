package com.uade.tpo.demo.models.responses;

import com.uade.tpo.demo.models.objects.Rating;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Reduced data transfer object for rating information")
public class RatingDTOReduced {
  @Schema(description = "Unique identifier of the rating", example = "1")
  private Long id;

  @Schema(description = "Numeric value of the rating (1-5)", example = "5")
  private Integer ratingValue;

  @Schema(description = "Additional comments for the rating", example = "Amazing movie!")
  private String comments;

  @Schema(description = "Date when the rating was created")
  private LocalDateTime createdAt;

  public RatingDTOReduced(Rating rating) {
    this.id = rating.getId();
    this.ratingValue = rating.getRatingValue();
    this.comments = rating.getComments();
    this.createdAt = rating.getCreatedAt();
  }
}
