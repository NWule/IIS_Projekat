import { Metric } from "./metric.model";
import { Report } from "./report.model";

export interface WeightedMetric {
  metricdId: number;
  weight: number;
}

export interface RecommendationRequest {
  position: string;
  metricWeights: WeightedMetric[];
}

export interface PlayerRecommendation {
  playerId: number;
  name: string;
  surname: string;
  score: number;
  source: string;
  position?: string;
  dateOfBirth?: string;
}

export interface ShownRecommendation extends PlayerRecommendation {
  latestReport?: Report | null;
}

export interface SelectedMetric {
  metric: Metric;
  weight: number;
}