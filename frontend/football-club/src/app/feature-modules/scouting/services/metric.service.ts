import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/env/environment';
import { Metric } from '../models/metric.model';

@Injectable({
  providedIn: 'root'
})
export class MetricService {
  private baseMetricsUrl = `${environment.apiHost}metrics`;

  constructor(private http: HttpClient) {}

  getAllMetrics(): Observable<Metric[]> {
    return this.http.get<Metric[]>(`${this.baseMetricsUrl}`);
  }
}