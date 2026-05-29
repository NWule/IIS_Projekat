import { Component, OnInit } from '@angular/core';
import { GameService } from '../../services/game.service';
import { Router } from '@angular/router';
import { AuthService } from '../../../../infrastructure/auth/auth.service';

// ptre, tre, stat

@Component({
  selector: 'app-match-list',
  templateUrl: './match-list.component.html',
  styleUrls: ['./match-list.component.css']
})
export class MatchListComponent implements OnInit {
  matches: any[] = []; 
  canViewDetails: boolean = false;
  isStatistician: boolean = false;

  constructor(private gameService: GameService, private router: Router, private authService: AuthService) {}

  ngOnInit(): void {
    this.gameService.getAllGames().subscribe({
      next: (data) => {
        this.matches = data;
      },
      error: (err) => console.error('Greška pri učitavanju utakmica:', err)
    });
    this.checkUserRole();
  }

  private checkUserRole(): void {
    this.authService.user$.subscribe(user => {
      if (user) {
        this.canViewDetails = 
          user.role === 'ROLE_HEAD_COACH' || 
          user.role === 'ROLE_ASSISTANT_COACH' || 
          user.role === 'ROLE_STATISTICIAN';

        this.isStatistician = user.role === 'ROLE_STATISTICIAN';
      } else {
        this.canViewDetails = false;
        this.isStatistician = false;
      }
    });
  }

  viewMatchDetails(matchId: number | undefined): void {
    if (this.canViewDetails && matchId) {
      this.router.navigate(['/match-details', matchId]);
    }
  }

  goToMatchEntry(): void {
    if (this.isStatistician) {
      this.router.navigate(['/match-entry']);
    }
  }
  
}