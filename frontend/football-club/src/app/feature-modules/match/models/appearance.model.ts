export interface Appearance {
  id?: number;
  playsForId: number;       
  playerName?: string;     
  playerSurname?: string;   
  gameId: number;   
  clubId?: number;      
  minutesPlayed: number;
  goals: number;
  assists: number;
  fouls: number;
  yellowCards: number;      
  redCard: boolean;         
  rating: number;           
  passingAccuracy: number;  
}