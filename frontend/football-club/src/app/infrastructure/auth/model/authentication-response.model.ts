import { RoleEnum } from './user.model';
export interface AuthenticationResponse {
  id: number;
  accessToken: string;
  role: RoleEnum;    
  clubId?: number;   
}