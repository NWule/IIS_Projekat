import { RoleEnum } from './user.model';
export interface AuthenticationResponse {
  id: number;
  accessToken: string;
  token: string;
  expiresIn: number;
  role: RoleEnum;
  clubId?: number;
}