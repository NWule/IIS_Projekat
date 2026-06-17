import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { forkJoin, Subscription, interval } from 'rxjs';
import { switchMap, startWith } from 'rxjs/operators';

import { GameService } from '../../services/game.service';
import { ClubService } from '../../services/club.service';
import { AppearanceService } from '../../services/appearance.service';
import { TeamStatisticService } from '../../services/team-statistic.service';
import { TacticalAnalysisService } from '../../services/tactical-analysis.service';

import { Game } from '../../models/game.model';
import { Club } from '../../models/club.model';
import { Appearance } from '../../models/appearance.model';
import { TeamStatistic } from '../../models/team-statistic.model';

@Component({
  selector: 'app-live-match-coach',
  templateUrl: './live-match-coach.component.html',
  styleUrls: ['./live-match-coach.component.css']
})
export class LiveMatchCoachComponent implements OnInit, OnDestroy {
  isLoading = true;
  gameId!: number;
  game!: Game;
  homeClub!: Club;
  awayClub!: Club;

  homeLineup: Appearance[] = [];
  awayLineup: Appearance[] = [];
  
  statistics: TeamStatistic | null = null;
  ruleBasedRecommendations: any[] = [];

  private pollingSubscription?: Subscription;

  constructor(
    private route: ActivatedRoute,
    private gameService: GameService,
    private clubService: ClubService,
    private appearanceService: AppearanceService,
    private statisticService: TeamStatisticService,
    private tacticalService: TacticalAnalysisService
  ) {}

  ngOnInit(): void {
    this.gameId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadStaticData();
  }

  ngOnDestroy(): void {
    this.pollingSubscription?.unsubscribe();
  }

  private loadStaticData(): void {
    this.isLoading = true;
    this.gameService.getGameById(this.gameId).pipe(
      switchMap(game => {
        this.game = game;
        return forkJoin({
          homeClub: this.clubService.getClubById(game.homeClubId),
          awayClub: this.clubService.getClubById(game.awayClubId),
          appearances: this.appearanceService.getAppearancesByGame(this.gameId)
        });
      })
    ).subscribe({
      next: ({ homeClub, awayClub, appearances }) => {
        this.homeClub = homeClub;
        this.awayClub = awayClub;
        this.homeLineup = appearances.filter(p => p.clubId === this.game.homeClubId);
        this.awayLineup = appearances.filter(p => p.clubId === this.game.awayClubId);
        
        this.isLoading = false;
        this.startLivePolling();
      },
      error: (err) => {
        console.error('Greška pri učitavanju statičkih podataka:', err);
        this.isLoading = false;
      }
    });
  }

  private startLivePolling(): void {
    this.pollingSubscription = interval(5000).pipe(
      startWith(0),
      switchMap(() => {
        return forkJoin({
          stats: this.statisticService.getStatisticByGameId(this.gameId),
          analysis: this.tacticalService.getAnalysisByGame(this.gameId)
        });
      })
    ).subscribe({
      next: ({ stats, analysis }) => {
        this.statistics = stats;
        this.ruleBasedRecommendations = analysis;
      },
      error: (err) => {
        console.error('Greška tokom automatskog osvežavanja podataka:', err);
      }
    });
  }
}