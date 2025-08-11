package com.uade.tpo.demo.models.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request to create or update a rating")
public class RatingRequest {

    @NotNull(message = "Rating value is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    @Schema(description = "Rating value (1-5 stars)", example = "5", required = true)
    private Integer ratingValue;

    @Schema(description = "Additional comments about the rating", example = "Amazing movie with great cinematography")
    private String comments;

    @NotNull(message = "Entity ID is required")
    @Schema(description = "ID of the entity being rated", example = "123", required = true)
    private Long entityId;

    @NotNull(message = "Entity type is required")
    @Schema(description = "Type of entity being rated", example = "MOVIE", required = true)
    private String entityType;
}
