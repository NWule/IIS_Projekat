import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/env/environment';
import { TicketTypeDTO } from '../models/stadium-map.model';

@Injectable({ providedIn: 'root' })
export class TicketTypeCrudService {
  private baseUrl = `${environment.apiHost}api/ticket-types`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<TicketTypeDTO[]> {
    return this.http.get<TicketTypeDTO[]>(this.baseUrl);
  }

  getById(id: number): Observable<TicketTypeDTO> {
    return this.http.get<TicketTypeDTO>(`${this.baseUrl}/${id}`);
  }

  create(dto: TicketTypeDTO): Observable<TicketTypeDTO> {
    return this.http.post<TicketTypeDTO>(this.baseUrl, dto);
  }

  update(id: number, dto: TicketTypeDTO): Observable<TicketTypeDTO> {
    return this.http.put<TicketTypeDTO>(`${this.baseUrl}/${id}`, dto);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
