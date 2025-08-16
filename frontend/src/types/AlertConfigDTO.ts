// src/types/alert-config.ts (or wherever you keep your DTO interfaces)

// These types should precisely match the enum values in your Java AlertConfiguration.AlertType enum.
export type AlertType = 'EMAIL' | 'DISCORD_WEBHOOK' | 'TELEGRAM' | 'GENERIC_WEBHOOK';

export interface AlertConfigDTO {
  id: number; // Corresponds to Java Long
  monitorId: number; // Corresponds to Java Long
  type: AlertType; // Corresponds to Java AlertConfiguration.AlertType enum
  destination: string; // Corresponds to Java String (e.g., email address, webhook URL)
  enabled: boolean; // Corresponds to Java Boolean
  failureThreshold: number; // Corresponds to Java Integer, optional as it's not nullable in DTO but not required in entity for initial state
  recoveryThreshold: number; // Corresponds to Java Integer, optional as it's not nullable in DTO but not required in entity for initial state
  createdAt: string; // Corresponds to Java LocalDateTime, serialized as ISO 8601 string
  updatedAt: string; // Corresponds to Java LocalDateTime, serialized as ISO 8601 string, optional as it's not nullable
}
