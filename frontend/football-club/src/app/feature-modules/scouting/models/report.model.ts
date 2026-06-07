import { Player } from "../../match/models/player.model";

export interface ValuedMetric {
  id: number;
  reportId: number;
  metricId: number;
  metricName: string;
  value: number;
}

export interface Report {
  id: number;
  playerId: number;
  playerName: string;
  playerSurname: string;
  scoutId: number;
  scoutUsername: string;
  createdAt: string;
  overallCommentary: string;
  clubAtTimeId: number;
  clubAtTimeName: string;
  leagueMultiplierAtTime: number;
  valuedMetrics: ValuedMetric[];
}

export interface ReportSave {
  playerId: number;
  overallCommentary: string;
  clubAtTimeId: number;
  leagueMultiplierAtTime: number; 
}

export interface PlayerWithReport extends Player {
  latestReport?: Report | null;
}

export interface ValuedMetricSave {
  reportId: number;
  metricId: number;
  value: number;
}