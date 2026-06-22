import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/env/environment';
import { PricingRuleDTO } from '../models/admin.model';

@Injectable({ providedIn: 'root' })
export class PricingRuleService {
  private baseUrl = `${environment.apiHost}pricing-rules`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<PricingRuleDTO[]> {
    return this.http.get<PricingRuleDTO[]>(this.baseUrl);
  }

  getById(id: number): Observable<PricingRuleDTO> {
    return this.http.get<PricingRuleDTO>(`${this.baseUrl}/${id}`);
  }

  create(dto: PricingRuleDTO): Observable<PricingRuleDTO> {
    return this.http.post<PricingRuleDTO>(this.baseUrl, dto);
  }

  update(id: number, dto: PricingRuleDTO): Observable<PricingRuleDTO> {
    return this.http.put<PricingRuleDTO>(`${this.baseUrl}/${id}`, dto);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  toggleActive(id: number): Observable<PricingRuleDTO> {
    return this.http.patch<PricingRuleDTO>(`${this.baseUrl}/${id}/toggle`, {});
  }

  linkGame(ruleId: number, gameId: number): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/${ruleId}/games/${gameId}`, {});
  }

  unlinkGame(ruleId: number, gameId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${ruleId}/games/${gameId}`);
  }
}
