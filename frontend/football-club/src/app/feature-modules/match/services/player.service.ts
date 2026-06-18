import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/env/environment';
import { Player } from '../models/player.model'; 

@Injectable({
  providedIn: 'root'
})
export class PlayerService {
  private readonly apiUrl = `${environment.apiHost}players`;

  constructor(private http: HttpClient) { }

  createPlayer(player: Omit<Player, 'id'>): Observable<Player> {
    return this.http.post<Player>(this.apiUrl, player);
  }

  getPlayerById(id: number): Observable<Player> {
    return this.http.get<Player>(`${this.apiUrl}/${id}`);
  }

  getPlayers(ids: number[]): Observable<Player[]> {
    const params = new HttpParams().set('ids', ids.join(','));
    return this.http.get<Player[]>(`${this.apiUrl}/find`, { params });
  }

  getAllPlayers(): Observable<Player[]> {
    return this.http.get<Player[]>(this.apiUrl);
  }

  updatePlayer(id: number, player: Player): Observable<Player> {
    return this.http.put<Player>(`${this.apiUrl}/${id}`, player);
  }

  deletePlayer(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  searchPlayers(keyword: string): Observable<Player[]> {
    const params = new HttpParams().set('keyword', keyword);
    return this.http.get<Player[]>(`${this.apiUrl}/search`, { params });
  }
}