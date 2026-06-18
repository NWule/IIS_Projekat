import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { GameZonePriceService } from '../../services/game-zone-price.service';
import { GameSearchService } from '../../services/game-search.service';
import { GameZonePriceDTO } from '../../models/admin.model';
import { GameSearchResult } from '../../models/game-search.model';

@Component({
  selector: 'app-game-zone-price',
  templateUrl: './game-zone-price.component.html',
  styleUrls: ['./game-zone-price.component.css']
})
export class GameZonePriceComponent implements OnInit {
  games: GameSearchResult[] = [];
  selectedGameId: number | null = null;
  zonePrices: GameZonePriceDTO[] = [];
  editValues: { [zoneId: number]: string } = {};
  loading = false;
  errorMessage = '';
  successMessage = '';

  constructor(
    private gameZonePriceService: GameZonePriceService,
    private gameSearchService: GameSearchService,
    public router: Router
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
    this.successMessage = '';
    this.gameZonePriceService.getZonePrices(this.selectedGameId).subscribe({
      next: (prices) => {
        this.zonePrices = prices;
        this.editValues = {};
        prices.forEach(p => {
          this.editValues[p.zoneId] = p.customPrice != null ? String(p.customPrice) : '';
        });
        this.loading = false;
      },
      error: () => { this.errorMessage = 'Greška pri učitavanju cena.'; this.loading = false; }
    });
  }

  save(zonePrice: GameZonePriceDTO): void {
    const val = this.editValues[zonePrice.zoneId];
    if (val === '' || val == null) {
      this.gameZonePriceService.removePrice(this.selectedGameId!, zonePrice.zoneId).subscribe({
        next: () => {
          zonePrice.customPrice = undefined;
          zonePrice.effectivePrice = zonePrice.defaultPrice;
          this.successMessage = 'Cena resetovana na zonu default.';
        },
        error: () => { this.errorMessage = 'Greška pri brisanju cene.'; }
      });
    } else {
      const price = parseFloat(val);
      if (isNaN(price) || price <= 0) { this.errorMessage = 'Unesite ispravnu cenu.'; return; }
      this.gameZonePriceService.setPrice(this.selectedGameId!, zonePrice.zoneId, price).subscribe({
        next: (updated) => {
          zonePrice.customPrice = updated.customPrice;
          zonePrice.effectivePrice = updated.effectivePrice;
          this.successMessage = `Cena za zonu "${zonePrice.zoneName}" sačuvana.`;
        },
        error: () => { this.errorMessage = 'Greška pri čuvanju cene.'; }
      });
    }
  }

  getGameLabel(game: GameSearchResult): string {
    const date = new Date(game.matchDate).toLocaleDateString('sr-RS');
    return `${game.homeClubName} – ${game.awayClubName} (${date})`;
  }
}
