// src/types/MonitorCheckDTO.ts

import type { ErrorCategory } from "./ErrorCategory";

export interface MonitorCheckDTO {
  id: number;
  monitorId: number;
  timestamp: string;
  httpStatusCode?: number;
  responseTimeMs?: number;
  isUp?: boolean;
  errorMessage?: string;
  
  // --- New Fields to Match Backend DTO ---
  responseBodySize: number;
  errorCategory: ErrorCategory;
  dnsTimeMs: number;
  connectTimeMs: number;
  ttfbMs: number;
}
