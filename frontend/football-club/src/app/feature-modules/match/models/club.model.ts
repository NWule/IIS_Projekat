export interface Club {
  id: number;
  name: string;
  location: string;
  wins: number;
  losses: number;
  draws: number;
  goalsScored: number;
  goalsConceded: number;
  imagePath?: string;
}