import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { forkJoin, interval, Subscription } from 'rxjs';
import { switchMap, startWith } from 'rxjs/operators';
import { Client } from '@stomp/stompjs';
import * as SockJS from 'sockjs-client';
import { environment } from 'src/env/environment';

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

  homeStartingXI: Appearance[] = [];
  homeBench: Appearance[] = [];
  awayStartingXI: Appearance[] = [];
  awayBench: Appearance[] = [];
  
  statistics: TeamStatistic | null = null;
  ruleBasedRecommendations: any[] = [];

  private stompClient: Client | null = null;
  private analysisPollingSub?: Subscription;

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
    if (this.stompClient) {
      this.stompClient.deactivate();
    }
    if (this.analysisPollingSub) {
      this.analysisPollingSub.unsubscribe();
    }
  }

  private loadStaticData(): void {
    this.isLoading = true;
    this.gameService.getGameById(this.gameId).pipe(
      switchMap(game => {
        this.game = game;
        return forkJoin({
          homeClub: this.clubService.getClubById(game.homeClubId),
          awayClub: this.clubService.getClubById(game.awayClubId),
          appearances: this.appearanceService.getAppearancesByGame(this.gameId),
          stats: this.statisticService.getStatisticByGameId(this.gameId)
        });
      })
    ).subscribe({
      next: ({ homeClub, awayClub, appearances, stats }) => {
        this.homeClub = homeClub;
        this.awayClub = awayClub;

        const homeApps = appearances.filter(p => p.clubId === this.game.homeClubId);
        const awayApps = appearances.filter(p => p.clubId === this.game.awayClubId);

        this.homeStartingXI = homeApps.filter(p => p.matchRole === 'STARTING_XI');
        this.homeBench = homeApps.filter(p => p.matchRole === 'BENCH');
        
        this.awayStartingXI = awayApps.filter(p => p.matchRole === 'STARTING_XI');
        this.awayBench = awayApps.filter(p => p.matchRole === 'BENCH');

        this.statistics = stats;
        this.isLoading = false;
        
        this.connectToWebSocket();
        this.startAnalysisPolling();
      },
      error: (err) => {
        console.error('Greška pri učitavanju statičkih podataka:', err);
        this.isLoading = false;
      }
    });
  }

  private connectToWebSocket(): void {
    const baseUrl = environment.apiHost.replace('/api/', ''); 
    const socket = new (SockJS as any)(`${baseUrl}/ws-live`);
    
    this.stompClient = new Client({
      webSocketFactory: () => socket,
      reconnectDelay: 5000,
      debug: (str) => {
      }
    });

    this.stompClient.onConnect = (frame) => {
      console.log('Povezan na WebSocket kanal za statistiku utakmice #' + this.gameId);

      this.stompClient?.subscribe(`/topic/game/${this.gameId}/team-stats`, (message) => {
        if (message.body) {
          this.statistics = JSON.parse(message.body);
        }
      });

      this.stompClient?.subscribe(`/topic/game/${this.gameId}/appearances`, () => {
        this.reloadAppearancesQuietly();
      });
    };

    this.stompClient.activate();
  }

  private startAnalysisPolling(): void {
    this.analysisPollingSub = interval(10000).pipe(
      startWith(0), 
      switchMap(() => this.tacticalService.getAnalysisByGame(this.gameId))
    ).subscribe({
      next: (analysisData) => {
        this.ruleBasedRecommendations = analysisData;
      },
      error: (err) => {
        console.error('Greška pri polling-u taktičkih preporuka:', err);
      }
    });
  }

  private reloadAppearancesQuietly(): void {
    this.appearanceService.getAppearancesByGame(this.gameId).subscribe({
      next: (appearances) => {
        const homeApps = appearances.filter(p => p.clubId === this.game.homeClubId);
        const awayApps = appearances.filter(p => p.clubId === this.game.awayClubId);

        this.homeStartingXI = homeApps.filter(p => p.matchRole === 'STARTING_XI');
        this.homeBench = homeApps.filter(p => p.matchRole === 'BENCH');
        
        this.awayStartingXI = awayApps.filter(p => p.matchRole === 'STARTING_XI');
        this.awayBench = awayApps.filter(p => p.matchRole === 'BENCH');
      },
      error: (err) => console.error('Greška pri učitavanju izmene preko WS:', err)
    });
  }
}