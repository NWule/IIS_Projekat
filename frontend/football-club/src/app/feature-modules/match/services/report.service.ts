import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/env/environment';

@Injectable({
  providedIn: 'root'
})
export class ReportService {
  private readonly apiUrl = `${environment.apiHost}reports`;

  constructor(private http: HttpClient) { }

  downloadGameReportPdf(gameId: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/game/${gameId}/pdf`, {
      responseType: 'blob'
    });
  }
}