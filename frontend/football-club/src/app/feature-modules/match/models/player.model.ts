export interface Player {
  id?: number;
  name: string;
  surname: string;
  dateOfBirth: string; 
}

export interface PlaysFor {
  id?: number;
  playerId?: number;
  playerName?: string;
  playerSurname?: string;
  clubId: number;
  clubName?: string;
  jerseyNumber: number;
  contractStart: string;
  contractEnd: string;
}