import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/env/environment';
import { SeatAdminDTO } from '../models/admin.model';

@Injectable({ providedIn: 'root' })
export class SeatAdminService {
  private baseUrl = `${environment.apiHost}seats`;

  constructor(private http: HttpClient) {}

  getByZone(zoneId: number): Observable<SeatAdminDTO[]> {
    return this.http.get<SeatAdminDTO[]>(`${this.baseUrl}/zone/${zoneId}`);
  }

  generateForZone(zoneId: number): Observable<SeatAdminDTO[]> {
    return this.http.post<SeatAdminDTO[]>(`${this.baseUrl}/generate/${zoneId}`, {});
  }

  update(id: number, seat: Partial<SeatAdminDTO>): Observable<SeatAdminDTO> {
    return this.http.put<SeatAdminDTO>(`${this.baseUrl}/${id}`, seat);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
