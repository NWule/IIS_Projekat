import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/env/environment';
import { PriceChangeLogDTO } from '../models/price-change-log.model';

@Injectable({ providedIn: 'root' })
export class PriceChangeLogService {
  private baseUrl = `${environment.apiHost}price-change-logs`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<PriceChangeLogDTO[]> {
    return this.http.get<PriceChangeLogDTO[]>(this.baseUrl);
  }

  getByGame(gameId: number): Observable<PriceChangeLogDTO[]> {
    return this.http.get<PriceChangeLogDTO[]>(`${this.baseUrl}/game/${gameId}`);
  }

  getByRule(ruleId: number): Observable<PriceChangeLogDTO[]> {
    return this.http.get<PriceChangeLogDTO[]>(`${this.baseUrl}/rule/${ruleId}`);
  }
}
