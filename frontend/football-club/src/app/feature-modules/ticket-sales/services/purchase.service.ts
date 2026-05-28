import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/env/environment';
import { PurchaseRequestDTO, PurchaseResponseDTO } from '../models/purchase.model';

@Injectable({ providedIn: 'root' })
export class PurchaseService {
  private baseUrl = `${environment.apiHost}api/purchases`;

  constructor(private http: HttpClient) {}

  createPurchase(request: PurchaseRequestDTO): Observable<PurchaseResponseDTO> {
    return this.http.post<PurchaseResponseDTO>(this.baseUrl, request);
  }

  getMyPurchases(): Observable<PurchaseResponseDTO[]> {
    return this.http.get<PurchaseResponseDTO[]>(`${this.baseUrl}/my`);
  }

  getPurchaseById(id: number): Observable<PurchaseResponseDTO> {
    return this.http.get<PurchaseResponseDTO>(`${this.baseUrl}/${id}`);
  }
}
