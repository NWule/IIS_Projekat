export interface TeamStatistic {
  id?: number;
  gameId: number;
  
  homeGoals: number;
  awayGoals: number;
  
  homeShots: number;
  awayShots: number;
  
  homeShotsOnTarget: number;
  awayShotsOnTarget: number;
  
  homePossession: number;
  awayPossession: number;
  
  homeCorners: number;
  awayCorners: number;
  
  homeFouls: number;
  awayFouls: number;
  
  homeOffsides: number;
  awayOffsides: number;
  
  homePassSuccessRate: number;
  awayPassSuccessRate: number;
}