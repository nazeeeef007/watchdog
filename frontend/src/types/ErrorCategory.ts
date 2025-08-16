// Since enums from the backend are often represented as strings in JSON,
// we define a corresponding TypeScript type.
export type ErrorCategory = 
  | 'HTTP_CLIENT_ERROR'
  | 'HTTP_SERVER_ERROR'
  | 'NETWORK_ERROR'
  | 'SSL_ERROR'
  | 'TIMEOUT_ERROR'
  | 'CONTENT_MISMATCH'
  | 'UNKNOWN_ERROR'
  | 'NONE';