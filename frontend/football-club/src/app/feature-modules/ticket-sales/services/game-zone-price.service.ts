import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/env/environment';
import { GameZonePriceDTO } from '../models/admin.model';

@Injectable({ providedIn: 'root' })
export class GameZonePriceService {
  private baseUrl = `${environment.apiHost}game-zone-prices`;

  constructor(private http: HttpClient) {}

  getZonePrices(gameId: number): Observable<GameZonePriceDTO[]> {
    return this.http.get<GameZonePriceDTO[]>(`${this.baseUrl}/${gameId}`);
  }

  setPrice(gameId: number, zoneId: number, price: number): Observable<GameZonePriceDTO> {
    return this.http.put<GameZonePriceDTO>(`${this.baseUrl}/${gameId}/${zoneId}`, price);
  }

  removePrice(gameId: number, zoneId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${gameId}/${zoneId}`);
  }
}
