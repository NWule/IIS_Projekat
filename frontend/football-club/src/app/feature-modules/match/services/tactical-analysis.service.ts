import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/env/environment';

@Injectable({
  providedIn: 'root'
})
export class TacticalAnalysisService {
  private readonly apiUrl = `${environment.apiHost}tactical-analysis`;

  constructor(private http: HttpClient) { }

  getAnalysisByGame(gameId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/game/${gameId}`);
  }
}