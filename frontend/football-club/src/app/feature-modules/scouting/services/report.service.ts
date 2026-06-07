import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/env/environment';
import { Report, ReportSave, ValuedMetric, ValuedMetricSave } from '../models/report.model';

@Injectable({
  providedIn: 'root'
})
export class ReportService {
  private baseReportsUrl = `${environment.apiHost}reports`;
  private baseValuedMetricsUrl = `${environment.apiHost}valued-metrics`;

  constructor(private http: HttpClient) {}

  getLatestReportForPlayer(playerId: number): Observable<Report> {
    return this.http.get<Report>(`${this.baseReportsUrl}/player/${playerId}/latest`);
  }

  createReport(dto: ReportSave): Observable<any> {
    return this.http.post<any>(`${this.baseReportsUrl}`, dto);
  }

  getReportById(id: number): Observable<Report> {
    return this.http.get<Report>(`${this.baseReportsUrl}/${id}`);
  }

  updateReport(id: number, dto: ReportSave): Observable<Report> {
    return this.http.put<Report>(`${this.baseReportsUrl}/${id}`, dto);
  }

  createValuedMetrics(dtos: ValuedMetricSave[]): Observable<any> {
    return this.http.post<any>(`${this.baseValuedMetricsUrl}/bulk`, dtos);
  }

  updateValuedMetrics(dtos: ValuedMetricSave[]): Observable<ValuedMetric[]> {
    return this.http.put<ValuedMetric[]>(`${this.baseValuedMetricsUrl}/bulk`, dtos); 
  }

  getMyReports(): Observable<Report[]> {
    return this.http.get<Report[]>(`${this.baseReportsUrl}/my`);
  }

  getReportsByPlayer(playerId: number): Observable<Report[]> {
    return this.http.get<Report[]>(`${this.baseReportsUrl}/player/${playerId}`);
  }
}