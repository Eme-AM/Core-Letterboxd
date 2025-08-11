package com.uade.tpo.demo.models.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request to register a new user")
public class RegisterRequest {

  @NotBlank(message = "Email is required")
  @Email(message = "Email should be valid")
  @Schema(
      description = "User's email address",
      example = "user@example.com",
      required = true
  )
  private String email;

  @NotBlank(message = "Username is required")
  @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
  @Schema(
      description = "Username for the account",
      example = "moviefan123",
      required = true
  )
  private String username;

  @NotBlank(message = "First name is required")
  @Schema(
      description = "User's first name",
      example = "John",
      required = true
  )
  private String firstName;

  @NotBlank(message = "Last name is required")
  @Schema(
      description = "User's last name",
      example = "Doe",
      required = true
  )
  private String lastName;

  @NotBlank(message = "Password is required")
  @Size(min = 6, message = "Password must be at least 6 characters")
  @Schema(
      description = "User's password",
      example = "securePassword123",
      required = true
  )
  private String password;
}