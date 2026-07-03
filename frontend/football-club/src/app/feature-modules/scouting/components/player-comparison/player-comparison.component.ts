import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { PlayerService } from '../../../match/services/player.service';
import { PlayerWithReport } from '../../../match/models/player.model';
import { ValuedMetric, Report } from '../../models/report.model';

@Component({
  selector: 'app-player-comparison',
  templateUrl: './player-comparison.component.html',
  styleUrls: ['./player-comparison.component.css']
})
export class PlayerComparisonComponent implements OnInit {
  playerA!: PlayerWithReport;
  playerB!: PlayerWithReport;
  isLoading: boolean = true;

  constructor(
    private route: ActivatedRoute,
    private playerService: PlayerService
  ) {}

  ngOnInit(): void {
    this.route.queryParamMap.subscribe(params => {
      const idsParam = params.getAll('ids');
      let targetIds: number[] = [];

      if (idsParam.length > 0) {
        targetIds = idsParam.map(id => Number(id));
      } else {
        const singleParam = params.get('ids');
        if (singleParam) {
          targetIds = singleParam.split(',').map(id => Number(id.trim()));
        }
      }

      console.log('Target IDs for comparison:', targetIds.slice(0, 2));

      if (targetIds.length >= 2) {
        this.loadComparisonData(targetIds.slice(0, 2));
      }
    });
  }

  loadComparisonData(ids: number[]): void {
    this.isLoading = true;
    this.playerService.getPlayersForComparison(ids).subscribe({
      next: (data) => {
        if (data && data.length >= 2) {
          this.playerA = data[0];
          this.playerB = data[1];
          console.log('Player A:', this.playerA);
          console.log('Player B:', this.playerB);
        }
        this.isLoading = false;
      },
      error: (err) => {
        console.error(err);
        this.isLoading = false;
      }
    });
  }

  getMetricComparisonClass(currentMetric: ValuedMetric, opponentReport: Report | null): string {
    if (!opponentReport || !opponentReport.valuedMetrics) {
      return '';
    }

    const opponentMetric = opponentReport.valuedMetrics.find(
      m => m.metricId === currentMetric.metricId || m.metricName === currentMetric.metricName
    );

    if (!opponentMetric || currentMetric.value === opponentMetric.value) {
      return 'metric-neutral';
    }

    const isPositive = currentMetric.type === 'POSITIVE';
    const isHigherBetter = currentMetric.value > opponentMetric.value;

    if (isPositive) {
      return isHigherBetter ? 'metric-better' : 'metric-worse';
    } else {
      return isHigherBetter ? 'metric-worse' : 'metric-better';
    }
  }

  formatPosition(position: string | undefined): string {
    if (!position) return '';
    return position.replace('_', ' ');
  }
}