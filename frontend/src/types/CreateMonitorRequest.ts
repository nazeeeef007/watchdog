import type { MonitorType } from "./MonitorDTO";

export interface CreateMonitorRequest {
  url: string;
  checkIntervalSeconds: number;
  type: MonitorType;
  alertConfig: {
    alertType: 'EMAIL' | 'WEBHOOK',
    destination: string
  }
}