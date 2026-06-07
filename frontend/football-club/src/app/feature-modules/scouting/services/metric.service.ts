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

  createMetric(metric: { name: string; category: string }): Observable<Metric> {
    return this.http.post<Metric>(`${this.baseMetricsUrl}`, metric);
  }

  updateMetric(id: number, metric: { name: string; category: string }): Observable<Metric> {
    return this.http.put<Metric>(`${this.baseMetricsUrl}/${id}`, metric);
  }

  getLastFiveGamesMetrics(playerId: number): Observable<GameMetric[]> {
    return this.http.get<GameMetric[]>(`${this.baseGameMetricsUrl}/player/${playerId}/recent`);
  }
}