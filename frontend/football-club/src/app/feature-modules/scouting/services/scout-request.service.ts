import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/env/environment';
import { ScoutRequestSave } from '../models/scout-request.model';

@Injectable({
  providedIn: 'root'
})
export class ScoutRequestService {
  private baseScoutRequestsUrl = `${environment.apiHost}scout-requests`;

  constructor(private http: HttpClient) {}

  saveScoutRequest(scoutRequest: ScoutRequestSave): Observable<ScoutRequestSave> {
    return this.http.post<ScoutRequestSave>(`${this.baseScoutRequestsUrl}`, scoutRequest);
  }
}