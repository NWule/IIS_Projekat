import { Injectable } from '@angular/core';
import { SelectedMetric, ShownRecommendation } from '../models/recommendation.model';

@Injectable({
  providedIn: 'root'
})
export class RecommendationStateService {
  selectedPosition: string = 'STRIKER';
  selectedMetrics: SelectedMetric[] = [];
  recommendations: ShownRecommendation[] = [];
}