import { Component, OnInit } from '@angular/core';
import { GameService } from '../../services/game.service';
import { Router } from '@angular/router';
import { AuthService } from '../../../../infrastructure/auth/auth.service';

@Component({
  selector: 'app-upcoming-matches',
  templateUrl: './upcoming-matches.component.html',
  styleUrls: ['./upcoming-matches.component.css']
})
export class UpcomingMatchesComponent implements OnInit {
  upcomingMatches: any[] = []; 
  canViewUpcoming: boolean = false;
  searchQuery: string = '';
  filteredUpcomingMatches: any[] = [];

  constructor(
    private gameService: GameService, 
    private router: Router, 
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.gameService.getUpcomingGames().subscribe({
      next: (data) => {
        this.upcomingMatches = data;
        this.filteredUpcomingMatches = [...this.upcomingMatches];
      },
      error: (err) => console.error('Greška pri učitavanju predstojećih mečeva:', err)
    });
    
    this.checkUserRole();
  }

  private checkUserRole(): void {
    this.authService.user$.subscribe(user => {
      if (user) {
        const allowedRoles = [
          'ROLE_HEAD_COACH', 
          'ROLE_ASSISTANT_COACH', 
          'ROLE_STATISTICIAN'
        ];
        
        this.canViewUpcoming = allowedRoles.includes(user.role);
      } else {
        this.canViewUpcoming = false;
      }
    });
  }

  viewMatchDetails(matchId: number): void {
    if (this.canViewUpcoming) {
      this.router.navigate(['/match-details', matchId]);
    }
  }

  onSearchChange(query: string): void {
    const lowerQuery = query.toLowerCase().trim();
    
    if (!lowerQuery) {
      this.filteredUpcomingMatches = [...this.upcomingMatches];
      return;
    }

    this.filteredUpcomingMatches = this.upcomingMatches.filter(match => 
      (match.homeClubName || '').toLowerCase().includes(lowerQuery) || 
      (match.awayClubName || '').toLowerCase().includes(lowerQuery)
    );
  }
}