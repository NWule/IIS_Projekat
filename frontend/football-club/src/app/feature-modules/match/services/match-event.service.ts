import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { MatchEventRequest } from '../models/match-event.model';
import { environment } from 'src/env/environment';

@Injectable({
  providedIn: 'root'
})
export class MatchEventService {
  private baseUrl = `${environment.apiHost}live`;

  constructor(private http: HttpClient) {}

  recordLiveEvent(gameId: number, dto: MatchEventRequest): Observable<string> {
    return this.http.post(
      `${this.baseUrl}/game/${gameId}/event`, 
      dto, 
      { responseType: 'text' } 
    );
  }

  endLiveMatch(gameId: number): Observable<string> {
    return this.http.put(
      `${this.baseUrl}/game/${gameId}/end`, {}, { responseType: 'text' } 
    );
  }
}