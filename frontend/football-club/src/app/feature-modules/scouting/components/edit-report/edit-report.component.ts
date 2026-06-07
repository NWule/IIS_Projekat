import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription, forkJoin } from 'rxjs';
import { switchMap, map, catchError } from 'rxjs/operators';
import { PlayerService } from '../../../match/services/player.service';
import { ReportService } from '../../services/report.service';
import { Player, PlaysFor } from '../../../match/models/player.model';
import { ContractService } from 'src/app/feature-modules/match/services/playsFor.service';
import { MetricService } from '../../services/metric.service';
import { Metric } from '../../models/metric.model';
import { ReportSave, ValuedMetricSave } from '../../models/report.model';

@Component({
  selector: 'app-edit-report',
  templateUrl: './edit-report.component.html',
  styleUrls: ['./edit-report.component.css']
})
export class EditReportComponent implements OnInit, OnDestroy {
  reportForm!: FormGroup;
  reportId!: number;
  
  players: Player[] = [];
  playerHistory: PlaysFor[] = [];
  metricsByCategory: { [category: string]: Metric[] } = {};
  categories: string[] = [];
  
  isLoading = true;
  isSubmitting = false;
  successMessage = '';
  errorMessage = '';
  
  private playerSub!: Subscription;
  private valuedMetricIdMap = new Map<number, number>();

  constructor(
    private fb: FormBuilder,
    private playerService: PlayerService,
    private reportService: ReportService,
    private contractService: ContractService,
    private metricService: MetricService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.reportId = +this.route.snapshot.paramMap.get('id')!;
    
    this.initForm();
    this.setupPlayerSelectionListener();
    this.loadData();
  }

  ngOnDestroy(): void {
    if (this.playerSub) this.playerSub.unsubscribe();
  }

  private initForm(): void {
    this.reportForm = this.fb.group({
      playerId: ['', Validators.required],
      clubAtTimeId: ['', Validators.required],
      overallCommentary: ['', [Validators.required, Validators.minLength(10)]],
      metrics: this.fb.group({}) 
    });
  }

  private loadData(): void {
    forkJoin({
      report: this.reportService.getReportById(this.reportId),
      players: this.playerService.getAllPlayers(),
      metrics: this.metricService.getAllMetrics()
    }).pipe(
      switchMap(data => {
        this.players = data.players;
        this.processMetrics(data.metrics);
        
        return this.contractService.getPlayerHistory(data.report.playerId).pipe(
          map(history => ({ ...data, history }))
        );
      })
    ).subscribe({
      next: (data) => {
        this.playerHistory = data.history;

        this.reportForm.patchValue({
          playerId: data.report.playerId,
          clubAtTimeId: data.report.clubAtTimeId,
          overallCommentary: data.report.overallCommentary
        }, { emitEvent: false });

        if (data.report.valuedMetrics) {
          data.report.valuedMetrics.forEach((vm: any) => {
            if (vm.id) {
              this.valuedMetricIdMap.set(vm.metricId, vm.id);
            }
            const control = this.reportForm.get(`metrics.${vm.metricId}`);
            if (control) {
              control.setValue(vm.value);
            }
          });
        }

        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load report data for editing.';
        this.isLoading = false;
      }
    });
  }

  private processMetrics(metrics: Metric[]): void {
    const metricsFormGroup = this.reportForm.get('metrics') as FormGroup;

    metrics.forEach(metric => {
      metricsFormGroup.addControl(
        metric.id.toString(), 
        this.fb.control('', [Validators.required, Validators.min(0), Validators.max(100)])
      );

      const cat = metric.category || 'UNCATEGORIZED';
      if (!this.metricsByCategory[cat]) {
        this.metricsByCategory[cat] = [];
        this.categories.push(cat);
      }
      this.metricsByCategory[cat].push(metric);
    });
  }

  private setupPlayerSelectionListener(): void {
    this.playerSub = this.reportForm.get('playerId')!.valueChanges.subscribe(selectedId => {
      this.reportForm.get('clubAtTimeId')?.setValue('');
      this.playerHistory = [];

      if (selectedId) {
        this.contractService.getPlayerHistory(+selectedId).subscribe({
          next: (history) => this.playerHistory = history,
          error: () => this.errorMessage = 'Could not load career history for this player.'
        });
      }
    });
  }

  formatCategoryName(category: string): string {
    return category
      .toLowerCase()
      .split('_')
      .map(word => word.charAt(0).toUpperCase() + word.slice(1))
      .join(' ');
  }

  onSubmit(): void {
    if (this.reportForm.invalid) {
      this.reportForm.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = '';
    this.successMessage = '';

    const formValues = this.reportForm.value;

    const reportUpdateDTO: ReportSave = {
      playerId: +formValues.playerId,
      overallCommentary: formValues.overallCommentary,
      clubAtTimeId: +formValues.clubAtTimeId,
      leagueMultiplierAtTime: 1.0 
    };

    this.reportService.updateReport(this.reportId, reportUpdateDTO).pipe(
      switchMap(() => {
        const metricsData = formValues.metrics;
        
        const valuedMetricsPayload: ValuedMetricSave[] = Object.keys(metricsData).map(metricIdStr => {
          const metricIdNum = +metricIdStr;
          return {
            id: this.valuedMetricIdMap.get(metricIdNum),
            reportId: this.reportId,
            metricId: metricIdNum,
            value: +metricsData[metricIdStr]
          };
        });

        return this.reportService.updateValuedMetrics(valuedMetricsPayload);
      }),
      catchError((error) => {
        console.error(error);
        throw new Error('Transaction failed');
      })
    ).subscribe({
      next: () => {
        this.successMessage = 'Scouting report updated successfully!';
        setTimeout(() => this.router.navigate(['/my-reports']), 2000);
      },
      error: () => {
        this.errorMessage = 'Failed to update the report. Please try again.';
        this.isSubmitting = false;
      }
    });
  }
}