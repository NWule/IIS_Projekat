import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/env/environment';
import { TicketDTO } from '../models/purchase.model';

@Injectable({ providedIn: 'root' })
export class TicketService {
  private baseUrl = `${environment.apiHost}api/tickets`;

  constructor(private http: HttpClient) {}

  getMyTickets(): Observable<TicketDTO[]> {
    return this.http.get<TicketDTO[]>(`${this.baseUrl}/my`);
  }

  getTicketByCode(code: string): Observable<TicketDTO> {
    return this.http.get<TicketDTO>(`${this.baseUrl}/code/${code}`);
  }
}
