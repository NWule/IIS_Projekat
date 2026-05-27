import { Component, OnInit } from '@angular/core';
import { GameService } from '../../services/game.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-match-list',
  templateUrl: './match-list.component.html',
  styleUrls: ['./match-list.component.css']
})
export class MatchListComponent implements OnInit {
  matches: any[] = []; 

  constructor(private gameService: GameService, private router: Router) {}

  ngOnInit(): void {
    this.gameService.getAllGames().subscribe({
      next: (data) => {
        this.matches = data;
      },
      error: (err) => console.error('Greška pri učitavanju utakmica:', err)
    });
  }

  viewMatchDetails(matchId: number | undefined): void {
    if (matchId) {
      this.router.navigate(['/utakmica-detalji', matchId]);
    }
  }
}