import { Injectable } from '@angular/core';
import { SelectedMetric, PlayerRecommendation } from '../models/recommendation.model';

@Injectable({
  providedIn: 'root'
})
export class RecommendationStateService {
  selectedPosition: string = 'STRIKER';
  selectedMetrics: SelectedMetric[] = [];
  recommendations: PlayerRecommendation[] = [];
}