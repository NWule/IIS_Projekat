import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { NgChartsModule } from 'ng2-charts';

import { ClubEntryComponent } from './components/club-entry/club-entry.component';
import { ClubEditComponent } from './components/club-edit/club-edit.component'; 
import { PlayerEntryComponent } from './components/player-entry/player-entry.component';
import { PlayerEditComponent } from './components/player-edit/player-edit.component';
import { MatchEntryComponent } from './components/match-entry/match-entry.component';
import { PlayerPerformanceEntryComponent } from './components/player-performance-entry/player-performance-entry.component';
import { ClubListComponent } from './components/club-list/club-list.component';
import { MatchListComponent } from './components/match-list/match-list.component';
import { MatchDetailsComponent } from './components/match-details/match-details.component';

import { ClubService } from './services/club.service';
import { PlayerService } from './services/player.service';
import { ContractService } from './services/playsFor.service';
import { PlayerDetailsComponent } from './components/player-details/player-details.component';
import { ClubDetailsComponent } from './components/club-details/club-details.component';
import { MatchPreparationComponent } from './components/match-preparation/match-preparation.component';
import { UpcomingMatchesComponent } from './components/upcoming-matches/upcoming-matches.component';
import { LiveTrackingComponent } from './components/live-tracking/live-tracking.component';
import { LiveMatchCoachComponent } from './components/live-match-coach/live-match-coach.component'; 
import { LiveMatchFinderComponent } from './components/live-match-finder/live-match-finder.component';
import { MarkdownModule } from 'ngx-markdown';


@NgModule({
  declarations: [
    ClubEntryComponent,
    ClubEditComponent, 
    PlayerEntryComponent,
    PlayerEditComponent,
    MatchEntryComponent,
    PlayerPerformanceEntryComponent,
    ClubListComponent,
    ClubDetailsComponent,
    PlayerDetailsComponent,
    MatchPreparationComponent,
    MatchDetailsComponent,
    MatchListComponent,
    UpcomingMatchesComponent,
    LiveTrackingComponent,
    LiveMatchCoachComponent,
    LiveMatchFinderComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    NgChartsModule,
    MarkdownModule.forRoot(),
  ],
  exports: [
    ClubEntryComponent,
    ClubEditComponent, 
    PlayerEntryComponent,
    PlayerEditComponent,
    MatchEntryComponent,
    PlayerPerformanceEntryComponent,
    ClubListComponent,
    ClubDetailsComponent,
    PlayerDetailsComponent,
    MatchPreparationComponent,
    MatchDetailsComponent,
    MatchListComponent,
    UpcomingMatchesComponent,
    LiveTrackingComponent,
    LiveMatchCoachComponent,
    LiveMatchFinderComponent
  ]
})
export class MatchModule { }