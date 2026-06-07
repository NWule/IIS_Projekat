import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Subscription, forkJoin, of } from 'rxjs';
import { switchMap, catchError } from 'rxjs/operators';
import { PlayerService } from '../../../match/services/player.service';
import { ReportService } from '../../services/report.service';
import { Player, PlaysFor } from '../../../match/models/player.model';
import { ContractService } from 'src/app/feature-modules/match/services/playsFor.service';
import { MetricService } from '../../services/metric.service';
import { Metric } from '../../models/metric.model';
import { ReportSave, ValuedMetricSave } from '../../models/report.model';

@Component({
  selector: 'app-create-report',
  templateUrl: './create-report.component.html',
  styleUrls: ['./create-report.component.css']
})
export class CreateReportComponent implements OnInit, OnDestroy {
  reportForm!: FormGroup;
  players: Player[] = [];
  playerHistory: PlaysFor[] = [];
  
  metricsByCategory: { [category: string]: Metric[] } = {};
  categories: string[] = [];
  
  isSubmitting = false;
  successMessage = '';
  errorMessage = '';
  private playerSub!: Subscription;

  constructor(
    private fb: FormBuilder,
    private playerService: PlayerService,
    private reportService: ReportService,
    private contractService: ContractService,
    private metricService: MetricService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.loadInitialData();
    this.setupPlayerSelectionListener();
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

  private loadInitialData(): void {
    this.playerService.getAllPlayers().subscribe({
      next: (data) => this.players = data,
      error: () => this.errorMessage = 'Failed to load player directory.'
    });

    this.metricService.getAllMetrics().subscribe({
      next: (data) => this.processMetrics(data),
      error: () => this.errorMessage = 'Failed to load metric evaluation templates.'
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

    const reportSaveDTO: ReportSave = {
      playerId: +formValues.playerId,
      overallCommentary: formValues.overallCommentary,
      clubAtTimeId: +formValues.clubAtTimeId,
      leagueMultiplierAtTime: 1.0
    };

    this.reportService.createReport(reportSaveDTO).pipe(
      switchMap((createdReport) => {
        const metricsData = formValues.metrics;
        
        const valuedMetricsPayload: ValuedMetricSave[] = Object.keys(metricsData).map(metricIdStr => ({
          reportId: createdReport.id,
          metricId: +metricIdStr,
          value: +metricsData[metricIdStr]
        }));

        return this.reportService.createValuedMetrics(valuedMetricsPayload);
      }),
      catchError((error) => {
        console.error(error);
        throw new Error('Transaction failed');
      })
    ).subscribe({
      next: () => {
        this.successMessage = 'Scouting report submitted successfully!';
        setTimeout(() => this.router.navigate(['/my-reports']), 2000);
      },
      error: () => {
        this.errorMessage = 'Failed to submit the report. Please try again.';
        this.isSubmitting = false;
      }
    });
  }
}