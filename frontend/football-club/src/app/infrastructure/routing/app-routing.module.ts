import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AuthGuard } from '../auth/auth.guard';
import { LoginComponent } from '../auth/components/login/login.component';
import { RegistrationComponent } from '../auth/components/registration/registration.component';

import { ClubEntryComponent } from '../../feature-modules/match/components/club-entry/club-entry.component';
import { ClubEditComponent } from '../../feature-modules/match/components/club-edit/club-edit.component';
import { ClubListComponent } from '../../feature-modules/match/components/club-list/club-list.component';
import { ClubDetailsComponent } from '../../feature-modules/match/components/club-details/club-details.component';

import { PlayerEntryComponent } from '../../feature-modules/match/components/player-entry/player-entry.component';
import { PlayerEditComponent } from '../../feature-modules/match/components/player-edit/player-edit.component';
import { PlayerDetailsComponent } from '../../feature-modules/match/components/player-details/player-details.component';

import { MatchEntryComponent } from '../../feature-modules/match/components/match-entry/match-entry.component';
import { MatchListComponent } from '../../feature-modules/match/components/match-list/match-list.component';
import { MatchDetailsComponent } from '../../feature-modules/match/components/match-details/match-details.component';
import { MatchPreparationComponent } from '../../feature-modules/match/components/match-preparation/match-preparation.component';

import { PlayerPerformanceEntryComponent } from '../../feature-modules/match/components/player-performance-entry/player-performance-entry.component';

import { MatchSearchComponent } from '../../feature-modules/ticket-sales/components/match-search/match-search.component';
import { StadiumMapComponent } from '../../feature-modules/ticket-sales/components/stadium-map/stadium-map.component';
import { PurchaseFormComponent } from '../../feature-modules/ticket-sales/components/purchase-form/purchase-form.component';
import { MyTicketsComponent } from '../../feature-modules/ticket-sales/components/my-tickets/my-tickets.component';

import { ZoneListComponent } from '../../feature-modules/ticket-sales/components/zone-list/zone-list.component';
import { ZoneFormComponent } from '../../feature-modules/ticket-sales/components/zone-form/zone-form.component';
import { SeatListComponent } from '../../feature-modules/ticket-sales/components/seat-list/seat-list.component';
import { TicketTypeListComponent } from '../../feature-modules/ticket-sales/components/ticket-type-list/ticket-type-list.component';
import { TicketTypeFormComponent } from '../../feature-modules/ticket-sales/components/ticket-type-form/ticket-type-form.component';
import { PricingRuleListComponent } from '../../feature-modules/ticket-sales/components/pricing-rule-list/pricing-rule-list.component';
import { PricingRuleFormComponent } from '../../feature-modules/ticket-sales/components/pricing-rule-form/pricing-rule-form.component';
import { GameZonePriceComponent } from '../../feature-modules/ticket-sales/components/game-zone-price/game-zone-price.component';
import { PriceChangeLogComponent } from '../../feature-modules/ticket-sales/components/price-change-log/price-change-log.component';
import { TicketAnalyticsComponent } from '../../feature-modules/ticket-sales/components/ticket-analytics/ticket-analytics.component';

import { ScoutingPlayerListComponent } from '../../feature-modules/scouting/components/scouting-player-list/scouting-player-list.component';
import { CreateReportComponent } from '../../feature-modules/scouting/components/create-report/create-report.component';
import { MyReportsComponent } from 'src/app/feature-modules/scouting/components/my-reports/my-reports.component';
import { EditReportComponent } from 'src/app/feature-modules/scouting/components/edit-report/edit-report.component';
import { ViewReportComponent } from 'src/app/feature-modules/scouting/components/view-report/view-report.component';
import { ViewPlayerComponent } from 'src/app/feature-modules/scouting/components/player-details/view-player.component';
import { MetricsDashboardComponent } from 'src/app/feature-modules/scouting/components/metrics-dashboard/metrics-dashboard.component';
import { WishlistsComponent } from 'src/app/feature-modules/scouting/components/wishlists/wishlists.component';
import { ScoutingRequestsComponent } from 'src/app/feature-modules/scouting/components/scouting-requests/scouting-requests.component';
import { PlayerRecommendationComponent } from 'src/app/feature-modules/scouting/components/player-recommendation/player-recommendation.component';
import { UpcomingMatchesComponent } from 'src/app/feature-modules/match/components/upcoming-matches/upcoming-matches.component';
import { LiveMatchCoachComponent } from 'src/app/feature-modules/match/components/live-match-coach/live-match-coach.component';
import { LiveTrackingComponent } from 'src/app/feature-modules/match/components/live-tracking/live-tracking.component';
import { LiveMatchFinderComponent } from 'src/app/feature-modules/match/components/live-match-finder/live-match-finder.component';

import { SearchTemplateManagementComponent } from 'src/app/feature-modules/scouting/components/search-template-management/search-template-management.component';
import { PlayerComparisonComponent } from 'src/app/feature-modules/scouting/components/player-comparison/player-comparison.component';

const routes: Routes = [
  { path: '',                     redirectTo: 'login', pathMatch: 'full' },

  // Auth
  { path: 'login',               component: LoginComponent },
  { path: 'register',            component: RegistrationComponent },

  // Klubovi
  { path: 'clubs',                   component: ClubListComponent },
  { path: 'add-club',                component: ClubEntryComponent },
  { path: 'edit-club/:id',           component: ClubEditComponent },
  { path: 'club-details/:id',        component: ClubDetailsComponent },

  // Igrači
  { path: 'add-player',              component: PlayerEntryComponent },
  { path: 'edit-player/:id',         component: PlayerEditComponent },
  { path: 'player-details/:id',      component: PlayerDetailsComponent },

  // Mečevi
  { path: 'matches',                 component: MatchListComponent },
  { path: 'match-entry', component: MatchEntryComponent },
  { path: 'match-details/:id',       component: MatchDetailsComponent },
  { path: 'match-preparation/:id',   component: MatchPreparationComponent },
  { path: 'upcoming-matches',          component: UpcomingMatchesComponent },
  { path: 'live-tracking/:id',       component: LiveTrackingComponent }, 
  { path: 'live-match-coach/:id',    component: LiveMatchCoachComponent },
  { path: 'live-match-finder', component: LiveMatchFinderComponent },

  // Performanse
  { path: 'add-performance',         component: PlayerPerformanceEntryComponent },

  // Kupovina karata
  { path: 'search-matches',          component: MatchSearchComponent },
  { path: 'stadium-map/:gameId',     component: StadiumMapComponent },
  { path: 'purchase/:gameId',        component: PurchaseFormComponent },
  { path: 'my-tickets',              component: MyTicketsComponent },

  // Admin — Zone
  { path: 'zones',                   component: ZoneListComponent },
  { path: 'add-zone',                component: ZoneFormComponent },
  { path: 'edit-zone/:id',           component: ZoneFormComponent },
  { path: 'zone-seats/:zoneId',      component: SeatListComponent },

  // Admin — Tipovi karata
  { path: 'ticket-types',            component: TicketTypeListComponent },
  { path: 'add-ticket-type',         component: TicketTypeFormComponent },
  { path: 'edit-ticket-type/:id',    component: TicketTypeFormComponent },
  { path: 'pricing-rules',           component: PricingRuleListComponent },
  { path: 'add-pricing-rule',        component: PricingRuleFormComponent },
  { path: 'edit-pricing-rule/:id',   component: PricingRuleFormComponent },
  { path: 'game-zone-prices',        component: GameZonePriceComponent },
  { path: 'price-change-log',        component: PriceChangeLogComponent },
  { path: 'ticket-analytics',        component: TicketAnalyticsComponent },

  // Skaut/Direktor
  { path: 'scouting-dashboard',       component: ScoutingPlayerListComponent },
  { path: 'view-player/:id',          component: ViewPlayerComponent },
  { path: 'scouting-requests',        component: ScoutingRequestsComponent },

  // Skaut
  { path: 'create-report',           component: CreateReportComponent },
  { path: 'edit-report/:id',         component: EditReportComponent },
  { path: 'my-reports',              component: MyReportsComponent },
  { path: 'report/:id',              component: ViewReportComponent },

  // Direktor
  { path: 'metrics-dashboard',      component: MetricsDashboardComponent },
  { path: 'wishlists',              component: WishlistsComponent },
  { path: 'player-recommendation',  component: PlayerRecommendationComponent },
  { path: 'search-templates',       component: SearchTemplateManagementComponent },
  { path: 'player-comparison',       component: PlayerComparisonComponent },

  { path: '**',                      redirectTo: 'matches' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
