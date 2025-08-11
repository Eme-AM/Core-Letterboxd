package com.uade.tpo.demo.models.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request to update user profile information")
public class UpdateUserRequest {
  @Schema(
      description = "Username of the user",
      example = "moviefan123"
  )
  private String username;

  @Schema(
      description = "First name of the user",
      example = "John"
  )
  private String firstName;

  @Schema(
      description = "Last name of the user", 
      example = "Doe"
  )
  private String lastName;

  @Schema(
      description = "User biography",
      example = "Movie enthusiast and aspiring director"
  )
  private String bio;

  @Schema(
      description = "Country of the user",
      example = "United States"
  )
  private String country;

  @Schema(
      description = "Personal website or social media URL",
      example = "https://letterboxd.com/moviefan123"
  )
  private String website;

  @Schema(
      description = "Avatar image URL",
      example = "https://example.com/avatar.jpg"
  )
  private String avatar;
}
