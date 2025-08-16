// src/types/AuthenticationResponse.ts

/**
 * Represents the data structure for the authentication response received from the backend.
 * This directly mirrors the com.watchdog.dto.AuthenticationResponse Java DTO.
 */
export interface AuthenticationResponse {
  /**
   * The JWT (JSON Web Token) returned by the backend upon successful login or registration.
   */
  token: string;
}
