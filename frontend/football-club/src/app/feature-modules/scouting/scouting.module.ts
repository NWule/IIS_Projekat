import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ScoutingPlayerListComponent } from './components/scouting-player-list/scouting-player-list.component';
import { MetricsComponent } from './components/metrics/metrics.component';
import { RouterModule } from '@angular/router';
import { CreateReportComponent } from './components/create-report/create-report.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { EditReportComponent } from './components/edit-report/edit-report.component';
import { MyReportsComponent } from './components/my-reports/my-reports.component';
import { ViewReportComponent } from './components/view-report/view-report.component';
import { ScoutRequestModalComponent } from './components/scout-request-modal/scout-request-modal.component';
import { ViewPlayerComponent } from './components/player-details/view-player.component';
import { MetricsDashboardComponent } from './components/metrics-dashboard/metrics-dashboard.component';
import { WishlistsComponent } from './components/wishlists/wishlists.component';



@NgModule({
  declarations: [
    ScoutingPlayerListComponent,
    MetricsComponent,
    CreateReportComponent,
    EditReportComponent,
    MyReportsComponent,
    ViewReportComponent,
    ScoutRequestModalComponent,
    ViewPlayerComponent,
    MetricsDashboardComponent,
    WishlistsComponent
  ],
  imports: [
    CommonModule,
    RouterModule,
    ReactiveFormsModule,
    FormsModule
  ]
})
export class ScoutingModule { }
