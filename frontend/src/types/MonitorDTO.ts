// src/types/monitor.ts (or wherever you keep your DTO interfaces)

// Assuming your Java enums Monitor.MonitorType and Monitor.MonitorStatus
// are serialized as strings by your Spring Boot backend.
// These types should precisely match the enum values in your Java Monitor class.

export type MonitorType = 'HTTP_HTTPS' | 'PING' | 'PORT'; // Updated based on Java Monitor.MonitorType enum
export type MonitorStatus = 'UP' | 'DOWN' | 'PAUSED' | 'UNKNOWN'; // Updated based on Java Monitor.MonitorStatus enum

export interface MonitorDTO {
  id: number; // Corresponds to Java Long
  userId: number; // Corresponds to Java Long (from the 'user' relationship)
  url: string; // Corresponds to Java String
  checkIntervalSeconds: number; // Corresponds to Java Integer
  type: MonitorType; // Corresponds to Java Monitor.MonitorType enum
  status: MonitorStatus; // Corresponds to Java Monitor.MonitorStatus enum
  lastCheckedAt: string; // Corresponds to Java LocalDateTime, serialized as ISO 8601 string
  lastStatusChangeAt: string; // Corresponds to Java LocalDateTime, serialized as ISO 8601 string
  contentMatchString?: string; // New: Corresponds to Java String, optional as it's not nullable
  httpMethod?: string; // New: Corresponds to Java String, optional as it's not nullable
  httpHeaders?: string; // New: Corresponds to Java String (even if DB is jsonb, Java DTO is String), optional as it's not nullable
  createdAt: string; // Corresponds to Java LocalDateTime, serialized as ISO 8601 string
  updatedAt?: string; // New: Corresponds to Java LocalDateTime, serialized as ISO 8601 string, optional as it's not nullable
}
