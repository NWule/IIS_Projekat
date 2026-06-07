import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ScoutingPlayerListComponent } from './components/scouting-player-list/scouting-player-list.component';
import { MetricsComponent } from './components/metrics/metrics.component';
import { RouterModule } from '@angular/router';
import { CreateReportComponent } from './components/create-report/create-report.component';
import { ReactiveFormsModule } from '@angular/forms';
import { EditReportComponent } from './components/edit-report/edit-report.component';
import { MyReportsComponent } from './components/my-reports/my-reports.component';



@NgModule({
  declarations: [
    ScoutingPlayerListComponent,
    MetricsComponent,
    CreateReportComponent,
    EditReportComponent,
    MyReportsComponent
  ],
  imports: [
    CommonModule,
    RouterModule,
    ReactiveFormsModule
  ]
})
export class ScoutingModule { }
