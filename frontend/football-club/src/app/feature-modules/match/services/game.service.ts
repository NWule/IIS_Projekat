import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/env/environment';
import { Game } from '../models/game.model';

@Injectable({
  providedIn: 'root'
})
export class GameService {
  private readonly apiUrl = `${environment.apiHost}games`;

  constructor(private http: HttpClient) { }

  createGame(game: Game): Observable<Game> {
    return this.http.post<Game>(this.apiUrl, game);
  }

  getGameById(id: number): Observable<Game> {
    return this.http.get<Game>(`${this.apiUrl}/${id}`);
  }

  getAllGames(): Observable<Game[]> {
    return this.http.get<Game[]>(this.apiUrl);
  }
}