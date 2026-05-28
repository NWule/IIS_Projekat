import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/env/environment';
import { ZoneDTO } from '../models/admin.model';

@Injectable({ providedIn: 'root' })
export class ZoneService {
  private baseUrl = `${environment.apiHost}api/zones`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<ZoneDTO[]> {
    return this.http.get<ZoneDTO[]>(this.baseUrl);
  }

  getById(id: number): Observable<ZoneDTO> {
    return this.http.get<ZoneDTO>(`${this.baseUrl}/${id}`);
  }

  create(zone: ZoneDTO): Observable<ZoneDTO> {
    return this.http.post<ZoneDTO>(this.baseUrl, zone);
  }

  update(id: number, zone: ZoneDTO): Observable<ZoneDTO> {
    return this.http.put<ZoneDTO>(`${this.baseUrl}/${id}`, zone);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
