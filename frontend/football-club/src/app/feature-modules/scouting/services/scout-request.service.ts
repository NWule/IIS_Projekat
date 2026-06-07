import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/env/environment';
import { ScoutRequest, ScoutRequestSave } from '../models/scout-request.model';

@Injectable({ providedIn: 'root' })
export class ScoutRequestService {
  private baseUrl = `${environment.apiHost}scout-requests`;

  constructor(private http: HttpClient) {}

  saveScoutRequest(dto: ScoutRequestSave): Observable<ScoutRequest> {
    return this.http.post<ScoutRequest>(this.baseUrl, dto);
  }

  updateRequest(id: number, dto: ScoutRequestSave): Observable<ScoutRequest> {
    return this.http.put<ScoutRequest>(`${this.baseUrl}/${id}`, dto);
  }

  deleteRequest(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  getUnclaimedRequests(): Observable<ScoutRequest[]> {
    return this.http.get<ScoutRequest[]>(`${this.baseUrl}/unclaimed`);
  }

  getRequestsByScout(): Observable<ScoutRequest[]> {
    return this.http.get<ScoutRequest[]>(`${this.baseUrl}/scout`);
  }

  getRequestsByDirector(): Observable<ScoutRequest[]> {
    return this.http.get<ScoutRequest[]>(`${this.baseUrl}/director`);
  }

  claimRequest(id: number): Observable<ScoutRequest> {
    return this.http.post<ScoutRequest>(`${this.baseUrl}/${id}/claim`, {});
  }

  cancelRequest(id: number): Observable<ScoutRequest> {
    return this.http.post<ScoutRequest>(`${this.baseUrl}/${id}/cancel`, {});
  }

  directorCancelRequest(id: number): Observable<ScoutRequest> {
    return this.http.post<ScoutRequest>(`${this.baseUrl}/${id}/cancel-by-director`, {});
  }
}