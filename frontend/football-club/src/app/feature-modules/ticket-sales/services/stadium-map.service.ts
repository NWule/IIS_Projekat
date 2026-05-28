import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/env/environment';
import { StadiumMapDTO, PricePreviewDTO, TicketTypeDTO } from '../models/stadium-map.model';

@Injectable({ providedIn: 'root' })
export class StadiumMapService {
  private mapUrl = `${environment.apiHost}api/stadium`;
  private ticketTypeUrl = `${environment.apiHost}api/ticket-types`;

  constructor(private http: HttpClient) {}

  getStadiumMap(gameId: number): Observable<StadiumMapDTO> {
    return this.http.get<StadiumMapDTO>(`${this.mapUrl}/map/${gameId}`);
  }

  getPricePreview(zoneId: number, ticketTypeId: number): Observable<PricePreviewDTO> {
    const params = new HttpParams()
      .set('zoneId', zoneId.toString())
      .set('ticketTypeId', ticketTypeId.toString());
    return this.http.get<PricePreviewDTO>(`${this.mapUrl}/price-preview`, { params });
  }

  getAllTicketTypes(): Observable<TicketTypeDTO[]> {
    return this.http.get<TicketTypeDTO[]>(this.ticketTypeUrl);
  }
}
