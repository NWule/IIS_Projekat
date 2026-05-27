import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/env/environment';
import { PlaysFor } from '../models/player.model'; 

@Injectable({
  providedIn: 'root'
})
export class ContractService {
  private readonly apiUrl = `${environment.apiHost}contracts`;

  constructor(private http: HttpClient) { }

  createContract(contract: Omit<PlaysFor, 'id'>): Observable<PlaysFor> {
    return this.http.post<PlaysFor>(this.apiUrl, contract);
  }

  getContractById(id: number): Observable<PlaysFor> {
    return this.http.get<PlaysFor>(`${this.apiUrl}/${id}`);
  }

  updateContract(id: number, contract: PlaysFor): Observable<PlaysFor> {
    return this.http.put<PlaysFor>(`${this.apiUrl}/${id}`, contract);
  }

  deleteContract(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getPlayerHistory(playerId: number): Observable<PlaysFor[]> {
    return this.http.get<PlaysFor[]>(`${this.apiUrl}/player/${playerId}/history`);
  }

  getClubHistory(clubId: number): Observable<PlaysFor[]> {
    return this.http.get<PlaysFor[]>(`${this.apiUrl}/club/${clubId}/history`);
  }

  getCurrentContract(playerId: number): Observable<PlaysFor> {
    return this.http.get<PlaysFor>(`${this.apiUrl}/player/${playerId}/current`);
  }

  getCurrentRoster(clubId: number): Observable<PlaysFor[]> {
    return this.http.get<PlaysFor[]>(`${this.apiUrl}/club/${clubId}/roster`);
  }
}