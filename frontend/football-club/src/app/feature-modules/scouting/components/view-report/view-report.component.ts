import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import { ReportService } from '../../services/report.service';
import { MetricService } from '../../services/metric.service';
import { Metric } from '../../models/metric.model';
import { Report } from '../../models/report.model';

@Component({
  selector: 'app-view-report',
  templateUrl: './view-report.component.html',
  styleUrls: ['./view-report.component.css']
})
export class ViewReportComponent implements OnInit {
  reportId!: number;
  reportData!: Report;
  
  metricsByCategory: { [category: string]: { name: string, value: number }[] } = {};
  categories: string[] = [];
  
  isLoading = true;
  errorMessage = '';

  constructor(
    private reportService: ReportService,
    private metricService: MetricService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.reportId = +this.route.snapshot.paramMap.get('id')!;
    this.loadData();
  }

  private loadData(): void {
    forkJoin({
      report: this.reportService.getReportById(this.reportId),
      metrics: this.metricService.getAllMetrics()
    }).subscribe({
      next: (data) => {
        this.reportData = data.report;
        this.processMetrics(data.metrics, data.report.valuedMetrics || []);
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'Došlo je do greške prilikom učitavanja izveštaja.';
        this.isLoading = false;
      }
    });
  }

  private processMetrics(allMetrics: Metric[], valuedMetrics: any[]): void {
    const valuesMap = new Map<number, number>();
    valuedMetrics.forEach(vm => valuesMap.set(vm.metricId, vm.value));

    allMetrics.forEach(metric => {
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
    return category
      .toLowerCase()
      .split('_')
      .map(word => word.charAt(0).toUpperCase() + word.slice(1))
      .join(' ');
  }
}