import { Report } from "src/app/feature-modules/scouting/models/report.model";

export interface Player {
  id?: number;
  name: string;
  surname: string;
  dateOfBirth: string;
  playerPosition?: string;
  imagePath?: string;
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
  playerPosition?: string;
  imagePath?: string;
}

export enum SearchType {
  EQUAL = 'EQUAL',
  GREATER_THAN = 'GREATER_THAN',
  LESS_THAN = 'LESS_THAN',
}

export interface SearchMetric {
  metricId: number;
  value: number;
  searchType: SearchType;
}

export interface SearchParameters {
  searchTerm: string;
  metrics: SearchMetric[];
}

export interface PlayerWithReport {
  player: Player;
  latestReport: Report | null;
}