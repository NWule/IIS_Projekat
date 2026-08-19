export interface Game {
  id?: number;
  homeClubId: number;
  awayClubId: number;
  matchDate: string; 
  status: 'UPCOMING' | 'LIVE' | 'FINISHED';
}