import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { PricingRuleService } from '../../services/pricing-rule.service';
import { GameSearchService } from '../../services/game-search.service';
import { PricingRuleDTO } from '../../models/admin.model';
import { GameSearchResult } from '../../models/game-search.model';

@Component({
  selector: 'app-pricing-rule-list',
  templateUrl: './pricing-rule-list.component.html',
  styleUrls: ['./pricing-rule-list.component.css']
})
export class PricingRuleListComponent implements OnInit {
  rules: PricingRuleDTO[] = [];
  allGames: GameSearchResult[] = [];
  loading = true;
  errorMessage = '';
  expandedRuleId: number | null = null;

  constructor(
    private pricingRuleService: PricingRuleService,
    private gameSearchService: GameSearchService,
    public router: Router
  ) {}

  ngOnInit(): void {
    this.loadRules();
    this.gameSearchService.searchGames({}).subscribe({
      next: (games) => this.allGames = games
    });
  }

  loadRules(): void {
    this.loading = true;
    this.pricingRuleService.getAll().subscribe({
      next: (data) => { this.rules = data; this.loading = false; },
      error: () => { this.errorMessage = 'Greška pri učitavanju pravila.'; this.loading = false; }
    });
  }

  toggleActive(rule: PricingRuleDTO): void {
    this.pricingRuleService.toggleActive(rule.id!).subscribe({
      next: (updated) => {
        const idx = this.rules.findIndex(r => r.id === updated.id);
        if (idx !== -1) this.rules[idx] = updated;
      },
      error: () => { this.errorMessage = 'Greška pri promeni statusa.'; }
    });
  }

  delete(rule: PricingRuleDTO): void {
    if (!confirm(`Obrisati pravilo "${rule.name}"?`)) return;
    this.pricingRuleService.delete(rule.id!).subscribe({
      next: () => this.loadRules(),
      error: () => { this.errorMessage = 'Greška pri brisanju.'; }
    });
  }

  toggleExpand(ruleId: number): void {
    this.expandedRuleId = this.expandedRuleId === ruleId ? null : ruleId;
  }

  isGameLinked(rule: PricingRuleDTO, gameId: number): boolean {
    return rule.gameIds.includes(gameId);
  }

  toggleGame(rule: PricingRuleDTO, gameId: number): void {
    if (this.isGameLinked(rule, gameId)) {
      this.pricingRuleService.unlinkGame(rule.id!, gameId).subscribe({
        next: () => { rule.gameIds = rule.gameIds.filter(id => id !== gameId); },
        error: () => { this.errorMessage = 'Greška pri uklanjanju utakmice.'; }
      });
    } else {
      this.pricingRuleService.linkGame(rule.id!, gameId).subscribe({
        next: () => { rule.gameIds = [...rule.gameIds, gameId]; },
        error: () => { this.errorMessage = 'Greška pri dodavanju utakmice.'; }
      });
    }
  }

  getGameLabel(game: GameSearchResult): string {
    const date = new Date(game.matchDate).toLocaleDateString('sr-RS');
    return `${game.homeClubName} – ${game.awayClubName} (${date})`;
  }
}
