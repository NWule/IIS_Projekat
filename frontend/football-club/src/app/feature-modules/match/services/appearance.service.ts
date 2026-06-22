import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/env/environment';
import { Appearance } from '../models/appearance.model';

@Injectable({
  providedIn: 'root'
})
export class AppearanceService {
  private readonly apiUrl = `${environment.apiHost}appearances`;

  constructor(private http: HttpClient) { }

  createAppearance(appearance: Appearance): Observable<Appearance> {
    return this.http.post<Appearance>(this.apiUrl, appearance);
  }

  getAppearancesByGame(gameId: number): Observable<Appearance[]> {
    return this.http.get<Appearance[]>(`${this.apiUrl}/game/${gameId}`);
  }
  saveLineup(gameId: number, clubId: number, lineup: Appearance[]): Observable<any> {
    return this.http.post(`${this.apiUrl}/game/${gameId}/lineup?clubId=${clubId}`, lineup);
  }
}