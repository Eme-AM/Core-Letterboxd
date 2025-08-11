package com.uade.tpo.demo.models.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Authentication response with JWT token")
public class AuthenticationResponse {

  @JsonProperty("token")
  @Schema(description = "JWT access token", example = "eyJhbGciOiJIUzI1...")
  private String token;
}
