import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PdfReportService {
  private baseUrl = 'http://localhost:8080/api/pdf-report';

  constructor(private http: HttpClient) {}

  downloadPlayerPdf(playerId: number): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/player/${playerId}/download`, {
      responseType: 'blob'
    });
  }
}