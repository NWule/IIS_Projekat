import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/env/environment';
import { Club } from '../models/club.model'; 

@Injectable({
  providedIn: 'root'
})
export class ClubService {
  private readonly apiUrl = `${environment.apiHost}clubs`;

  constructor(private http: HttpClient) { }

  createClub(club: Omit<Club, 'id'>): Observable<Club> {
    return this.http.post<Club>(this.apiUrl, club);
  }

  getClubById(id: number): Observable<Club> {
    return this.http.get<Club>(`${this.apiUrl}/${id}`);
  }

  getAllClubs(): Observable<Club[]> {
    return this.http.get<Club[]>(this.apiUrl);
  }

  updateClub(id: number, club: Club): Observable<Club> {
    return this.http.put<Club>(`${this.apiUrl}/${id}`, club);
  }

  deleteClub(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getLeagueTable(): Observable<Club[]> {
    return this.http.get<Club[]>(`${this.apiUrl}/league-table`);
  }

  searchClubsByName(name: string): Observable<Club[]> {
    const params = new HttpParams().set('name', name);
    return this.http.get<Club[]>(`${this.apiUrl}/search`, { params });
  }

  getMyClub(): Observable<Club> {
    return this.http.get<Club>(`${this.apiUrl}/my`);
  }
  
  uploadClubImage(clubId: number, formData: FormData): Observable<Club> {
    return this.http.post<Club>(`${this.apiUrl}/${clubId}/image`, formData);
  }
}