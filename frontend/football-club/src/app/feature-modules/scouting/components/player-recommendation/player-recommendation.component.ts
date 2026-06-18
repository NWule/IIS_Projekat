import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MetricService } from '../../services/metric.service';
import { Metric } from '../../models/metric.model';
import { RecommendationService } from '../../services/recommendation.service';
import { SelectedMetric, PlayerRecommendation, RecommendationRequest, ShownRecommendation } from '../../models/recommendation.model';
import { RecommendationStateService } from '../../services/recommendation-state.service';
import { Player } from 'src/app/feature-modules/match/models/player.model';
import { PlayerService } from 'src/app/feature-modules/match/services/player.service';
import { ReportService } from '../../services/report.service';
import { catchError, forkJoin, map, of, switchMap } from 'rxjs';

@Component({
  selector: 'app-player-recommendation',
  templateUrl: './player-recommendation.component.html',
  styleUrls: ['./player-recommendation.component.css']
})
export class PlayerRecommendationComponent implements OnInit {
  allMetrics: Metric[] = [];
  showModal: boolean = false;

  playerPositions: string[] = [
    'GOALKEEPER',
    'CENTER_BACK',
    'WING_BACK',
    'DEFENSIVE_MIDFIELDER',
    'CENTRAL_MIDFIELDER',
    'ATTACKING_MIDFIELDER',
    'WIDE_MIDFIELDER',
    'STRIKER',
    'WINGER'
  ];

  constructor(
    private metricService: MetricService,
    private reportService: ReportService,
    private recommendationService: RecommendationService,
    private playerService: PlayerService,
    private stateService: RecommendationStateService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.metricService.getAllMetrics().subscribe(metrics => {
      this.allMetrics = metrics;
    });
  }

  get selectedPosition(): string {
    return this.stateService.selectedPosition;
  }

  set selectedPosition(value: string) {
    this.stateService.selectedPosition = value;
  }

  get selectedMetrics(): SelectedMetric[] {
    return this.stateService.selectedMetrics;
  }

  set selectedMetrics(value: SelectedMetric[]) {
    this.stateService.selectedMetrics = value;
  }

  get recommendations(): ShownRecommendation[] {
    return this.stateService.recommendations;
  }

  set recommendations(value: ShownRecommendation[]) {
    this.stateService.recommendations = value;
  }

  openModal(): void {
    this.showModal = true;
  }

  closeModal(): void {
    this.showModal = false;
  }

  selectMetric(metric: Metric): void {
    this.selectedMetrics.push({ metric, weight: 3 });
    this.closeModal();
  }

  removeMetric(metricId: number): void {
    this.selectedMetrics = this.selectedMetrics.filter(m => m.metric.id !== metricId);
  }

  getAvailableMetricsByCategory(): { [key: string]: Metric[] } {
    const available = this.allMetrics.filter(
      m => !this.selectedMetrics.some(sm => sm.metric.id === m.id)
    );
    
    return available.reduce((acc, metric) => {
      if (!acc[metric.category]) {
        acc[metric.category] = [];
      }
      acc[metric.category].push(metric);
      return acc;
    }, {} as { [key: string]: Metric[] });
  }

  getCategoryKeys(obj: object): string[] {
    return Object.keys(obj);
  }

  submitRecommendation(): void {
    if (this.selectedMetrics.length === 0) return;

    const request: RecommendationRequest = {
      position: this.selectedPosition,
      metricWeights: this.selectedMetrics.map(sm => ({
        metricdId: sm.metric.id,
        weight: sm.weight
      }))
    };

    this.recommendationService.getRecommendations(request).pipe(
      switchMap((results) => {
        if (!results || results.length === 0) {
          return of([]);
        }

        const playersWithReports = results.map(player => {
          if (player.playerId === undefined) {
            return of({...player, latestReport: null });
          }
          return this.reportService.getLatestReportForPlayer(player.playerId).pipe(
            map(report => ({ ...player, latestReport: report })),
            catchError(() => of({ ...player, latestReport: null }))
          )
        })
        
        return forkJoin(playersWithReports);
      })
      ).subscribe({
        next: (combinedData) => {
          this.recommendations = combinedData.sort((a, b) => b.score - a.score);
        },
        error: (err) => {
          console.error(err);
        }
      });
  }

  viewPlayer(playerId: number | undefined): void {
    this.router.navigate(['/view-player', playerId]);
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
    return category
      .toLowerCase()
      .split('_')
      .map(word => word.charAt(0).toUpperCase() + word.slice(1))
      .join(' ');
  }
}