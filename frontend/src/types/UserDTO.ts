export interface UserDTO {
    id: number;
    email: string;
    createdAt: string; // Corrected: Will be an ISO 8601 string from the backend
    updatedAt: string; // Corrected: Will be an ISO 8601 string from the backend
}