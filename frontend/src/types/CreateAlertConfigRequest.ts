import type { AlertType } from "./AlertConfigDTO";

export interface CreateAlertConfigRequest {
  type: AlertType;
  destination: string;
  enabled: boolean;
  failureThreshold: number; // Added to fix the not-null constraint error
  recoveryThreshold: number; // Added to fix the not-null constraint error
}