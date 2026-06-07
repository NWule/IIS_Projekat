import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/env/environment';
import { Report } from '../models/report.model';

@Injectable({
  providedIn: 'root'
})
export class ReportService {
  private baseReportsUrl = `${environment.apiHost}/reports`;

  constructor(private http: HttpClient) {}

  getLatestReportForPlayer(playerId: number): Observable<Report> {
    return this.http.get<Report>(`${this.baseReportsUrl}/player/${playerId}/latest`);
  }
}