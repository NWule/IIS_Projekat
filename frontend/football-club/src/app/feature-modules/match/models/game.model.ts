export interface Game {
  id?: number;
  homeClubId: number;
  awayClubId: number;
  dateTime: string; 
  status: 'UPCOMING' | 'LIVE' | 'FINISHED';
}