import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { PlayerService } from '../../../match/services/player.service';
import { Player, SearchParameters, SearchType } from '../../../match/models/player.model';
import { catchError, forkJoin, map, of, switchMap, Observable } from 'rxjs';
import { ReportService } from '../../services/report.service';
import { PlayerWithReport } from '../../models/report.model';
import { MetricService } from '../../services/metric.service';
import { Metric } from '../../models/metric.model';

interface ActiveFilter {
  metric: Metric;
  value: number;
  searchType: SearchType;
}

@Component({
  selector: 'app-scouting-player-list',
  templateUrl: './scouting-player-list.component.html',
  styleUrls: ['./scouting-player-list.component.css']
})
export class ScoutingPlayerListComponent implements OnInit {
  players: PlayerWithReport[] = [];
  isLoading: boolean = true;
  errorMessage: string = '';

  searchTerm: string = '';
  activeFilters: ActiveFilter[] = [];
  allMetrics: Metric[] = [];
  showModal: boolean = false;
  
  searchTypes = [
    { value: SearchType.EQUAL, label: 'Jednako' },
    { value: SearchType.LESS_THAN, label: 'Manje od' },
    { value: SearchType.GREATER_THAN, label: 'Veće od' }
  ];

  constructor(
    private playerService: PlayerService, 
    private reportService: ReportService,
    private metricService: MetricService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.metricService.getAllMetrics().subscribe({
      next: (metrics) => this.allMetrics = metrics,
      error: (err) => console.error(err)
    });
    this.loadAllPlayers();
  }

  loadAllPlayers(): void {
    this.processPlayers(this.playerService.getAllPlayers());
  }

  applyFilters(): void {
    const params: SearchParameters = {
      searchTerm: this.searchTerm,
      metrics: this.activeFilters.map(f => ({
        metricId: f.metric.id,
        value: f.value,
        searchType: f.searchType
      }))
    };
    this.processPlayers(this.playerService.advancedSearch(params));
  }

  clearFilters(): void {
    this.searchTerm = '';
    this.activeFilters = [];
    this.loadAllPlayers();
  }

  processPlayers(request: Observable<Player[]>): void {
    this.isLoading = true;
    this.errorMessage = '';
    
    request.pipe(
      switchMap((playerList) => {
        if (!playerList || playerList.length === 0) {
          return of([]);
        }
        
        const reportingRequests = playerList.map(player => {
          if (player.id === undefined) {
            return of({ ...player, latestReport: null });
          }
          return this.reportService.getLatestReportForPlayer(player.id).pipe(
            map(report => ({ ...player, latestReport: report })),
            catchError(() => of({ ...player, latestReport: null }))
          )
        });
        
        return forkJoin(reportingRequests);
      })
    ).subscribe({
      next: (combinedData) => {
        this.players = combinedData;
        this.isLoading = false;
      },
      error: (err) => {
        console.error(err);
        this.errorMessage = 'Sistemska greška prilikom preuzimanja profila igrača.';
        this.isLoading = false;
      }
    });
  }

  openModal(): void {
    this.showModal = true;
  }

  closeModal(): void {
    this.showModal = false;
  }

  selectMetric(metric: Metric): void {
    this.activeFilters.push({ metric, value: 0, searchType: SearchType.EQUAL });
    this.closeModal();
  }

  removeFilter(index: number): void {
    this.activeFilters.splice(index, 1);

    if (this.activeFilters.length === 0) {
      this.loadAllPlayers();
    }
  }

  getAvailableMetricsByCategory(): { [key: string]: Metric[] } {
    const available = this.allMetrics.filter(
      m => !this.activeFilters.some(af => af.metric.id === m.id)
    );

    const categories = [
      "PASSING_AND_PROGRESSION",
      "ATTACKING_AND_OUTPUT",
      "DEFENSIVE_ACTIONS",
      "PHYSICAL",
      "IMPACT_AND_EFFICIENCY"
    ]
    
    let metricsByCategory: { [category: string]: Metric[] } = {};
    categories.forEach(category => {
      metricsByCategory[category] = [];
    });

    available.forEach(metric => {
      const cat = metric.category || 'UNCATEGORIZED';
      metricsByCategory[cat].push(metric);
    });

    return metricsByCategory;
  }

  getCategoryKeys(obj: object): string[] {
    return Object.keys(obj);
  }

  viewDetails(id: number | undefined): void {
    if (id === undefined) return;
    this.router.navigate(['/view-player', id]);
  }

  formatPosition(position: string | undefined): string {
    if (!position) return '';
    return position
      .toLowerCase()
      .split('_')
      .map(word => word.charAt(0).toUpperCase() + word.slice(1))
      .join(' ');
  }

  formatCategoryName(category: string): string {
    if (!category) return '';
    return category
      .toLowerCase()
      .split('_')
      .map(word => word.charAt(0).toUpperCase() + word.slice(1))
      .join(' ');
  }
}