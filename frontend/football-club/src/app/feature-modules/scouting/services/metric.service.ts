import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/env/environment';
import { GameMetric, Metric } from '../models/metric.model';

@Injectable({
  providedIn: 'root'
})
export class MetricService {
  private baseMetricsUrl = `${environment.apiHost}metrics`;
  private baseGameMetricsUrl = `${environment.apiHost}game-metrics`;

  constructor(private http: HttpClient) {}

  getAllMetrics(): Observable<Metric[]> {
    return this.http.get<Metric[]>(`${this.baseMetricsUrl}`);
  }

  getLastFiveGamesMetrics(playerId: number): Observable<GameMetric[]> {
    return this.http.get<GameMetric[]>(`${this.baseGameMetricsUrl}/player/${playerId}/recent`);
  }
}