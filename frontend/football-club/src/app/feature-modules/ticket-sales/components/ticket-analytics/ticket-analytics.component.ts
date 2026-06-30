import { Component, OnInit } from '@angular/core';
import { TicketAnalyticsService } from '../../services/ticket-analytics.service';
import { GameSearchService } from '../../services/game-search.service';
import { GameAnalyticsDTO } from '../../models/ticket-analytics.model';
import { GameSearchResult } from '../../models/game-search.model';

@Component({
  selector: 'app-ticket-analytics',
  templateUrl: './ticket-analytics.component.html',
  styleUrls: ['./ticket-analytics.component.css']
})
export class TicketAnalyticsComponent implements OnInit {
  games: GameSearchResult[] = [];
  selectedGameId: number | null = null;
  analytics: GameAnalyticsDTO | null = null;
  loading = false;
  errorMessage = '';

  constructor(
    private analyticsService: TicketAnalyticsService,
    private gameSearchService: GameSearchService
  ) {}

  ngOnInit(): void {
    this.gameSearchService.searchGames({}).subscribe({
      next: (games) => this.games = games
    });
  }

  onGameChange(): void {
    if (!this.selectedGameId) return;
    this.loading = true;
    this.errorMessage = '';
    this.analyticsService.getGameAnalytics(this.selectedGameId).subscribe({
      next: (data) => { this.analytics = data; this.loading = false; },
      error: () => { this.errorMessage = 'Greška pri učitavanju analitike.'; this.loading = false; }
    });
  }

  downloadCsv(): void {
    if (!this.selectedGameId) return;
    this.analyticsService.downloadCsvReport(this.selectedGameId).subscribe({
      next: (blob) => {
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `izvestaj-utakmica-${this.selectedGameId}.csv`;
        a.click();
        URL.revokeObjectURL(url);
      },
      error: () => { this.errorMessage = 'Greška pri preuzimanju izveštaja.'; }
    });
  }

  getGameLabel(game: GameSearchResult): string {
    const date = new Date(game.matchDate).toLocaleDateString('sr-RS');
    return `${game.homeClubName} – ${game.awayClubName} (${date})`;
  }
}
