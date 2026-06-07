import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ReportService } from '../../services/report.service';
import { Report } from '../../models/report.model';

@Component({
  selector: 'app-my-reports',
  templateUrl: './my-reports.component.html',
  styleUrls: ['./my-reports.component.css']
})
export class MyReportsComponent implements OnInit {
  reports: Report[] = [];
  isLoading = true;
  errorMessage = '';

  constructor(
    private reportService: ReportService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.fetchMyReports();
  }

  private fetchMyReports(): void {
    this.reportService.getMyReports().subscribe({
      next: (data) => {
        this.reports = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error fetching reports:', err);
        this.errorMessage = 'Došlo je do greške prilikom učitavanja izveštaja.';
        this.isLoading = false;
      }
    });
  }

  viewReport(reportId: number): void {
    this.router.navigate(['/scouting/view', reportId]);
  }
}