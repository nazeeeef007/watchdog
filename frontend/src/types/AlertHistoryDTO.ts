export interface AlertHistoryDTO {
  id: number;
  monitorId: number;
  alertConfigurationId: number;
  timestamp: string;
  status: 'SENT' | 'FAILED' | 'THROTTLED';
  message: string;
}
