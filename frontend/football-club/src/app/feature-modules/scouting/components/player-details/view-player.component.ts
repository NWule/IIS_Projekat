import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import { PlayerService } from '../../../match/services/player.service';
import { ContractService } from '../../../match/services/playsFor.service';
import { ReportService } from '../../services/report.service';
import { MetricService } from '../../services/metric.service';
import { WishlistService } from '../../services/wishlist.service';

import { Player, PlaysFor } from '../../../match/models/player.model';
import { Report } from '../../models/report.model';
import { Metric, GameMetric } from '../../models/metric.model';
import { Wishlist } from '../../models/wishlist.model';
import { AuthService } from 'src/app/infrastructure/auth/auth.service';

interface GroupedGame {
  gameId: number;
  matchInfo: string;
  matchDate: string;
  metrics: GameMetric[];
}

@Component({
  selector: 'app-view-player',
  templateUrl: './view-player.component.html',
  styleUrls: ['./view-player.component.css']
})
export class ViewPlayerComponent implements OnInit {
  isDirector: boolean = false;
  playerId!: number;
  player!: Player;
  currentClubName: string = 'Slobodan igrač (Bez kluba)';
  playerHistory: PlaysFor[] = [];
  
  activeTab: 'reports' | 'stats' = 'reports';
  showHistoryModal = false;
  showScoutRequestModal = false;
  showWishlistModal = false;

  reports: Report[] = [];
  selectedReportId: number | null = null;
  selectedReport: Report | null = null;
  
  metricsByCategory: { [category: string]: { name: string, value: number }[] } = {};
  categories: string[] = [];
  allMetrics: Metric[] = [];

  gameStats: GroupedGame[] = [];
  myWishlists: Wishlist[] = [];

  isLoading = true;
  isWishlistLoading = false;

  showCompareModal = false;
  allPlayers: Player[] = [];
  isCompareLoading = false;

  constructor(
    private route: ActivatedRoute,
    private playerService: PlayerService,
    private contractService: ContractService,
    private reportService: ReportService,
    private metricService: MetricService,
    private wishlistService: WishlistService,
    private authService: AuthService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.authService.checkIfUserExists();
    this.authService.user$.subscribe(user => {
      if (!user) {
        console.warn('Nije prijavljen nijedan korisnik. Pristup informacijama o igraču je ograničen.');
        this.isLoading = false;
        return;
      }
      if (user.role.toString() === 'ROLE_SPORTS_DIRECTOR' || user.role.toString() === 'ROLE_ADMIN') {
        this.isDirector = true;
      }
    });
    this.playerId = +this.route.snapshot.paramMap.get('id')!;
    this.loadPlayerData();
  }

  private loadPlayerData(): void {
    forkJoin({
      player: this.playerService.getPlayerById(this.playerId),
      history: this.contractService.getPlayerHistory(this.playerId),
      reports: this.reportService.getReportsByPlayer(this.playerId),
      recentGames: this.metricService.getLastFiveGamesMetrics(this.playerId),
      allMetrics: this.metricService.getAllMetrics()
    }).subscribe({
      next: (data) => {
        console.log('Podaci o igraču učitani:', data);
        this.player = data.player;
        this.playerHistory = data.history;
        this.allMetrics = data.allMetrics;
        
        this.determineCurrentClub();
        this.processReports(data.reports);
        this.processGameMetrics(data.recentGames);

        this.isLoading = false;
      },
      error: (err) => {
        console.error('Greška pri učitavanju podataka o igraču', err);
        this.isLoading = false;
      }
    });
  }

  private determineCurrentClub(): void {
    const today = new Date();
    const activeContract = this.playerHistory.find(contract => {
      if (!contract.contractEnd) return true;
      const endDate = new Date(contract.contractEnd);
      return endDate >= today;
    });

    if (activeContract && activeContract.clubName) {
      this.currentClubName = activeContract.clubName;
    }
  }

  private processReports(reports: Report[]): void {
    this.reports = reports.sort((a, b) => 
      new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
    );

    if (this.reports.length > 0) {
      this.selectedReportId = this.reports[0].id;
      this.onReportChange();
    }
  }

  onReportChange(): void {
    this.selectedReport = this.reports.find(r => r.id === +this.selectedReportId!) || null;
    if (this.selectedReport) {
      this.mapSelectedReportMetrics();
    }
  }

  private mapSelectedReportMetrics(): void {
    this.metricsByCategory = {};
    this.categories = [];
    
    if (!this.selectedReport || !this.selectedReport.valuedMetrics) return;

    const valuesMap = new Map<number, number>();
    this.selectedReport.valuedMetrics.forEach((vm: any) => valuesMap.set(vm.metricId, vm.value));

    this.allMetrics.forEach(metric => {
      if (valuesMap.has(metric.id)) {
        const cat = metric.category || 'UNCATEGORIZED';
        if (!this.metricsByCategory[cat]) {
          this.metricsByCategory[cat] = [];
          this.categories.push(cat);
        }
        this.metricsByCategory[cat].push({
          name: metric.name,
          value: valuesMap.get(metric.id)!
        });
      }
    });
  }

  formatCategoryName(category: string): string {
    return category.toLowerCase().split('_')
      .map(word => word.charAt(0).toUpperCase() + word.slice(1)).join(' ');
  }

  private processGameMetrics(metrics: GameMetric[]): void {
    const grouped = new Map<number, GroupedGame>();

    metrics.forEach(m => {
      if (!grouped.has(m.gameId)) {
        grouped.set(m.gameId, {
          gameId: m.gameId,
          matchInfo: `${m.homeClubName} vs ${m.awayClubName}`,
          matchDate: m.matchDate,
          metrics: []
        });
      }
      grouped.get(m.gameId)?.metrics.push(m);
    });

    this.gameStats = Array.from(grouped.values()).sort((a, b) => 
      new Date(b.matchDate).getTime() - new Date(a.matchDate).getTime()
    );
  }

  addToWishlist(): void {
    console.log('Otvaranje modala za dodavanje u listu želja');
    this.showWishlistModal = true;
    this.isWishlistLoading = true;
    this.wishlistService.getMyWishlists().subscribe({
      next: (data) => {
        this.myWishlists = data;
        this.isWishlistLoading = false;
      },
      error: (err) => {
        console.error('Greška pri dobavljanju lista želja', err);
        this.isWishlistLoading = false;
      }
    });
  }

  selectWishlist(wishlistId: number, wishlistName: string): void {
    this.wishlistService.addPlayerToWishlist(wishlistId, this.playerId).subscribe({
      next: () => {
        alert(`Igrač ${this.player.name} ${this.player.surname} uspešno dodat u listu "${wishlistName}".`);
        this.showWishlistModal = false;
      },
      error: (err) => {
        console.error('Greška pri dodavanju igrača na listu želja', err);
        alert('Došlo je do greške. ' + err.error.message);
      }
    });
  }

  openCompareModal(): void {
    this.showCompareModal = true;
    this.isCompareLoading = true;
    
    this.playerService.getAllPlayers().subscribe({
      next: (data) => {
        this.allPlayers = data.filter(p => p.id !== this.playerId);
        this.isCompareLoading = false;
      },
      error: (err) => {
        console.error('Greška pri učitavanju svih igrača za poređenje:', err);
        this.isCompareLoading = false;
      }
    });
  }

  selectPlayerForComparison(selectedPlayerId: number | undefined): void {
    this.showCompareModal = false;
    
    this.router.navigate(['/player-comparison'], {
      queryParams: { ids: [this.playerId, selectedPlayerId] }
    });
  }

  formatPosition(position: string | undefined): string {
    if (!position) return '';
    return position
      .toLowerCase()
      .split('_')
      .map(word => word.charAt(0).toUpperCase() + word.slice(1))
      .join(' ');
  }
}