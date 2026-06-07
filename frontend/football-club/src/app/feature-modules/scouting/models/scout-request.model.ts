export interface ScoutRequestSave {
  playerId: number;
  instructions: string;
  deadline: string;
}

export interface ScoutRequest {
  id: number;
  directorId: number;
  directorName: string;
  scoutId: number;
  scoutName: string;
  playerId: number;
  playerName: string;
  playerSurname: string;
  requestDate: string;
  instructions: string;
  deadline: string;
  status: string; // PENDING, IN_PROGRESS, COMPLETED, CANCELLED
}