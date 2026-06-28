import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/env/environment';
import { GameAnalyticsDTO } from '../models/ticket-analytics.model';

@Injectable({ providedIn: 'root' })
export class TicketAnalyticsService {
  private baseUrl = `${environment.apiHost}ticket-analytics`;

  constructor(private http: HttpClient) {}

  getGameAnalytics(gameId: number): Observable<GameAnalyticsDTO> {
    return this.http.get<GameAnalyticsDTO>(`${this.baseUrl}/game/${gameId}`);
  }

  getAllGamesAnalytics(): Observable<GameAnalyticsDTO[]> {
    return this.http.get<GameAnalyticsDTO[]>(`${this.baseUrl}/all`);
  }

  downloadCsvReport(gameId: number): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/game/${gameId}/csv`, { responseType: 'blob' });
  }
}
