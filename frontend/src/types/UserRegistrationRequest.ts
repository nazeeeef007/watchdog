// src/types/UserRegistrationRequest.ts

/**
 * Represents the data structure for a user registration request sent to the backend.
 * This directly mirrors the com.watchdog.dto.UserRegistrationRequest Java DTO.
 */
export interface UserRegistrationRequest {
  /**
   * The user's email address.
   * Corresponds to the 'email' field in the backend DTO.
   */
  email: string;

  /**
   * The user's chosen plain-text password.
   * Corresponds to the 'password' field in the backend DTO.
   * This will be hashed by the backend before storage.
   */
  password: string;
}
