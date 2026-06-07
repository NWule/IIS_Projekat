export interface Metric {
  id: number;
  name: string;
  category: string;
}

export interface GameMetric {
  id: number;
  gameId: number;
  homeClubName: string;
  awayClubName: string;
  matchDate: string;
  playerId: number;
  metricId: number;
  metricName: string;
  recordedValue: number;
}