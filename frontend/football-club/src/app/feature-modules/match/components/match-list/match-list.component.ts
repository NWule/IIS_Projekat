import { Component, OnInit } from '@angular/core';
import { GameService } from '../../services/game.service';
import { Router } from '@angular/router';
import { AuthService } from '../../../../infrastructure/auth/auth.service';
import { ReportService } from 'src/app/feature-modules/match/services/report.service';

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
  canGenerateReport: boolean = false;
  searchQuery: string = '';
  filteredMatches: any[] = [];

  constructor(private gameService: GameService, private router: Router, private authService: AuthService,
    private reportService: ReportService) {}

  ngOnInit(): void {
    this.gameService.getPlayedGames().subscribe({
      next: (data) => {
        const formattedData = data.map(match => ({
          ...match,
          matchDate: match.matchDate ? match.matchDate.replace(' ', 'T') : match.matchDate
        }));
        this.matches = formattedData;
        this.filteredMatches = [...this.matches];
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

        this.canGenerateReport = user.role === 'ROLE_HEAD_COACH' || user.role === 'ROLE_STATISTICIAN' || 
          user.role === 'ROLE_ADMIN';
      } else {
        this.canViewDetails = false;
        this.isStatistician = false;
        this.canGenerateReport = false;
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

  downloadReport(matchId: number): void {
    if (this.canGenerateReport) {
      this.reportService.downloadGameReportPdf(matchId).subscribe({
        next: (blob) => {
          const url = window.URL.createObjectURL(blob);
          const a = document.createElement('a');
          a.href = url;
          a.download = `report-${matchId}.pdf`;
          document.body.appendChild(a);
          a.click();
          document.body.removeChild(a);
          window.URL.revokeObjectURL(url);
        },
        error: (err) => console.error('Greška pri generisanju izveštaja:', err)
      });
    }
  }

  onSearchChange(query: string): void {
    const lowerQuery = query.toLowerCase().trim();
    
    if (!lowerQuery) {
      this.filteredMatches = [...this.matches];
      return;
    }

    this.filteredMatches = this.matches.filter(match => 
      (match.homeClubName || '').toLowerCase().includes(lowerQuery) || 
      (match.awayClubName || '').toLowerCase().includes(lowerQuery)
    );
  }

}