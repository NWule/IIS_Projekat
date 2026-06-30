import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/env/environment';
import { TeamStatistic } from '../models/team-statistic.model';

@Injectable({
  providedIn: 'root'
})
export class TeamStatisticService {
  private readonly apiUrl = `${environment.apiHost}statistics`;

  constructor(private http: HttpClient) { }

  saveFinalStatistic(statistic: TeamStatistic): Observable<TeamStatistic> {
    return this.http.post<TeamStatistic>(`${this.apiUrl}/final`, statistic);
  }

  getStatisticByGameId(gameId: number): Observable<TeamStatistic> {
    return this.http.get<TeamStatistic>(`${this.apiUrl}/game/${gameId}`);
  }

  getClubChartData(clubId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/club/${clubId}/chart`);
  }
}