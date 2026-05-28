import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/env/environment';
import { GameSearchResult, GameSearchParams } from '../models/game-search.model';

@Injectable({ providedIn: 'root' })
export class GameSearchService {
  private baseUrl = `${environment.apiHost}games`;

  constructor(private http: HttpClient) {}

  searchGames(params: GameSearchParams): Observable<GameSearchResult[]> {
    let httpParams = new HttpParams();
    if (params.opponent) httpParams = httpParams.set('opponent', params.opponent);
    if (params.startDate) httpParams = httpParams.set('startDate', params.startDate);
    if (params.endDate) httpParams = httpParams.set('endDate', params.endDate);
    if (params.minPrice !== undefined && params.minPrice !== null)
      httpParams = httpParams.set('minPrice', params.minPrice.toString());
    if (params.maxPrice !== undefined && params.maxPrice !== null)
      httpParams = httpParams.set('maxPrice', params.maxPrice.toString());
    return this.http.get<GameSearchResult[]>(`${this.baseUrl}/search`, { params: httpParams });
  }
}
