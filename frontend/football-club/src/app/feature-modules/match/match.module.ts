import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';

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
    MatchListComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule
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
    MatchListComponent
  ]
})
export class MatchModule { }