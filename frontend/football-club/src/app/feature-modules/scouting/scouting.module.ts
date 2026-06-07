import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ScoutingPlayerListComponent } from './scouting-player-list/scouting-player-list.component';
import { MetricsComponent } from './metrics/metrics.component';
import { RouterModule } from '@angular/router';



@NgModule({
  declarations: [
    ScoutingPlayerListComponent,
    MetricsComponent
  ],
  imports: [
    CommonModule,
    RouterModule
  ]
})
export class ScoutingModule { }
