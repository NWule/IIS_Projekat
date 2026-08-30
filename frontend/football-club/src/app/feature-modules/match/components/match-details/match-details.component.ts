import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { HttpClient } from '@angular/common/http';
import { environment } from 'src/env/environment';

import { GameService } from '../../services/game.service';
import { AppearanceService } from '../../services/appearance.service';
import { TeamStatisticService } from '../../services/team-statistic.service';
import { ContractService } from '../../services/playsFor.service';
import { ClubService } from '../../services/club.service';
import { AuthService } from '../../../../infrastructure/auth/auth.service';
import { ReportService } from 'src/app/feature-modules/match/services/report.service';

import { Game } from '../../models/game.model';
import { Appearance } from '../../models/appearance.model';
import { TeamStatistic } from '../../models/team-statistic.model';
import { PlaysFor } from '../../models/player.model';
import { Club } from '../../models/club.model';

@Component({
  selector: 'app-match-detail',
  templateUrl: './match-details.component.html',
  styleUrls: ['./match-details.component.css']
})
export class MatchDetailsComponent implements OnInit {
  activeTab: 'statistics' | 'performances' | 'ai-analysis' = 'statistics';
  gameId!: number;
  game!: Game;
  homeClub!: Club;
  awayClub!: Club;
  myClub!: Club;

  isHomeClub = false;
  isStatistician = false; 
  isUpcoming = false;

  statistics: TeamStatistic | null = null;
  statsForm!: FormGroup;
  statsExists = false;

  homePerformances: Appearance[] = [];
  awayPerformances: Appearance[] = [];

  myRoster: PlaysFor[] = [];

  showAddForm = false;
  addForm!: FormGroup;

  loading = true;

  canGenerateReport: boolean = false;

  searchQuery: string = '';
  filteredHomePerformances: Appearance[] = [];
  filteredAwayPerformances: Appearance[] = [];

  isGeneratingAi = false;
  aiReportContent: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private fb: FormBuilder,
    private gameService: GameService,
    private appearanceService: AppearanceService,
    private statisticService: TeamStatisticService,
    private contractService: ContractService,
    private clubService: ClubService,
    private authService: AuthService,
    private reportService: ReportService,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.gameId = Number(this.route.snapshot.paramMap.get('id'));
    this.initAddForm();
    this.initStatsForm();
    this.loadData();
    this.checkUserRole();
  }

  private checkUserRole(): void {
    this.authService.user$.subscribe(user => {
      this.isStatistician = user?.role === 'ROLE_STATISTICIAN';
      if (user) {
         this.canGenerateReport = user.role === 'ROLE_HEAD_COACH' ||  user.role === 'ROLE_STATISTICIAN' || 
                                  user.role === 'ROLE_ADMIN';
      } else {
         this.canGenerateReport = false;
      }
      console.log('ROLE DIAGNOSTICS:', {
        currentRole: user?.role,
        isStatistician: this.isStatistician,
        canGenerateReport: this.canGenerateReport
      });
    });
  }

  private loadData(): void {
    this.loading = true;

    this.gameService.getGameById(this.gameId).pipe(
      switchMap(game => {
        this.game = game;
        return forkJoin({
          homeClub: this.clubService.getClubById(game.homeClubId),
          awayClub: this.clubService.getClubById(game.awayClubId),
          myClub: this.clubService.getMyClub(),
          performances: this.appearanceService.getAppearancesByGame(this.gameId)
        });
      })
    ).subscribe({
      next: ({ homeClub, awayClub, myClub, performances }) => {
        this.homeClub = homeClub;
        this.awayClub = awayClub;
        this.myClub = myClub;
        this.isHomeClub = myClub.id === this.game.homeClubId;
        this.isUpcoming = this.game.status === 'UPCOMING';

        const isMyClubParticipating = myClub.id === this.game.homeClubId || myClub.id === this.game.awayClubId;
        
        this.isStatistician = this.isStatistician && isMyClubParticipating;

        console.log('MATCH PARTICIPATION DIAGNOSTICS:', {
          myClubId: myClub.id,
          homeClubId: this.game.homeClubId,
          awayClubId: this.game.awayClubId,
          doesMyClubParticipate: isMyClubParticipating,
          finalManagementRight: this.isStatistician
        });

        if (!this.isStatistician && this.statsForm) {
          this.statsForm.disable();
        }

        this.homePerformances = performances.filter(p => p.clubId === this.game.homeClubId);
        this.awayPerformances = performances.filter(p => p.clubId === this.game.awayClubId);

        this.filteredHomePerformances = [...this.homePerformances];
        this.filteredAwayPerformances = [...this.awayPerformances];

        if (isMyClubParticipating) {
          this.contractService.getCurrentRoster(myClub.id!).subscribe({
            next: (roster) => {
              this.myRoster = roster;
              this.loading = false;
            },
            error: (err) => {
              console.error('Error loading roster:', err);
              this.loading = false;
            }
          });
        } else {
          this.myRoster = [];
          this.loading = false;
        }
      },
      error: (err) => {
        console.error('Error loading match data:', err);
        this.loading = false;
      }
    });

    this.statisticService.getStatisticByGameId(this.gameId).subscribe({
      next: (stat) => {
        this.statistics = stat;
        this.statsForm.patchValue(stat);
        this.statsExists = true;
        if (!this.isStatistician) {
          this.statsForm.disable();
        }
      },
      error: () => {
        this.statsExists = false;
        if (!this.isStatistician) {
          this.statsForm.disable();
        }
      }
    });
  }

  private initStatsForm(): void {
    this.statsForm = this.fb.group({
      homeGoals:          [0, [Validators.required, Validators.min(0)]],
      awayGoals:          [0, [Validators.required, Validators.min(0)]],
      homeShots:          [0, [Validators.required, Validators.min(0)]],
      awayShots:          [0, [Validators.required, Validators.min(0)]],
      homeShotsOnTarget:  [0, [Validators.required, Validators.min(0)]],
      awayShotsOnTarget:  [0, [Validators.required, Validators.min(0)]],
      homePossession:     [50, [Validators.required, Validators.min(0), Validators.max(100)]],
      awayPossession:     [50, [Validators.required, Validators.min(0), Validators.max(100)]],
      homeCorners:        [0, [Validators.required, Validators.min(0)]],
      awayCorners:        [0, [Validators.required, Validators.min(0)]],
      homeFouls:          [0, [Validators.required, Validators.min(0)]],
      awayFouls:          [0, [Validators.required, Validators.min(0)]],
      homeOffsides:       [0, [Validators.required, Validators.min(0)]],
      awayOffsides:       [0, [Validators.required, Validators.min(0)]],
      homePassSuccessRate:   [0, [Validators.required, Validators.min(0), Validators.max(100)]],
      awayPassSuccessRate:   [0, [Validators.required, Validators.min(0), Validators.max(100)]]
    });
  }

  private initAddForm(): void {
    this.addForm = this.fb.group({
      playsForId:       [null, Validators.required],
      minutesPlayed:    [0,   [Validators.required, Validators.min(0), Validators.max(120)]],
      goals:            [0,   [Validators.required, Validators.min(0)]],
      assists:          [0,   [Validators.required, Validators.min(0)]],
      passingAccuracy:  [0,   [Validators.required, Validators.min(0), Validators.max(100)]],
      fouls:            [0,   [Validators.required, Validators.min(0)]],
      yellowCardCheckbox: [false],
      redCard:          [false]
    });
  }

  startMatch(): void {
    if (!this.isStatistician) return;

    const updatedGamePayload: Game = {
      ...this.game,
      status: 'LIVE'
    };

    this.gameService.updateGame(this.gameId, updatedGamePayload).subscribe({
      next: () => {
        alert('Match successfully started live!');
        this.router.navigate(['/live-tracking', this.gameId]);
      },
      error: (err) => {
        console.error('Error starting the match:', err);
        alert('A server error occurred while starting the match.');
      }
    });
  }

  saveStatistics(): void {
    if (this.statsForm.invalid || !this.isStatistician) return;
    const payload: TeamStatistic = { ...this.statsForm.value, gameId: this.gameId };
    this.statisticService.saveFinalStatistic(payload).subscribe({
      next: (saved) => {
        this.statistics = saved;
        this.statsExists = true;
        alert('Statistics successfully saved!');
      },
      error: (err) => console.error('Error saving statistics:', err)
    });
  }

  toggleAddForm(): void {
    this.showAddForm = !this.showAddForm;
    if (!this.showAddForm) this.addForm.reset({
      playsForId: null, minutesPlayed: 0, goals: 0, assists: 0,
      passingAccuracy: 0, fouls: 0, yellowCardCheckbox: false, redCard: false
    });
  }

  savePerformance(): void {
    if (this.addForm.invalid) return;
    const formValue = this.addForm.value;

    const payload: Appearance = {
      gameId: this.gameId,
      playsForId: Number(formValue.playsForId),
      minutesPlayed: Number(formValue.minutesPlayed),
      goals: Number(formValue.goals),
      assists: Number(formValue.assists),
      passingAccuracy: Number(formValue.passingAccuracy),
      fouls: Number(formValue.fouls),
      yellowCards: formValue.yellowCardCheckbox ? 1 : 0,
      redCard: formValue.redCard,
      rating: 0.0
    };

    this.appearanceService.createAppearance(payload).subscribe({
      next: (created) => {
        if (this.isHomeClub) {
          this.homePerformances = [...this.homePerformances, created];
        } else {
          this.awayPerformances = [...this.awayPerformances, created];
        }
        this.onSearchChange(this.searchQuery);
        alert('Performance successfully saved!');
        this.toggleAddForm();
      },
      error: (err) => {
        console.error('Error saving performance:', err);
        alert('An error occurred while saving player performance!');
      }
    });
  }

 onSearchChange(query: string): void {
    const lowerQuery = query.toLowerCase().trim();
    
    if (!lowerQuery) {
      this.filteredHomePerformances = [...this.homePerformances];
      this.filteredAwayPerformances = [...this.awayPerformances];
      return;
    }

    this.filteredHomePerformances = this.homePerformances.filter(p => 
      (p.playerName || '').toLowerCase().includes(lowerQuery) || 
      (p.playerSurname || '').toLowerCase().includes(lowerQuery)
    );

    this.filteredAwayPerformances = this.awayPerformances.filter(p => 
      (p.playerName || '').toLowerCase().includes(lowerQuery) || 
      (p.playerSurname || '').toLowerCase().includes(lowerQuery)
    );
  }

  goToAddPerformance(): void {
    this.router.navigate(['/add-performance'], { queryParams: { gameId: this.gameId } });
  }

  goToMatchPreparation(): void {
    this.router.navigate(['/match-preparation', this.gameId]);
  }

  downloadReport(): void {
    if (this.canGenerateReport && this.gameId) {
      this.reportService.downloadGameReportPdf(this.gameId).subscribe({
        next: (blob) => {
          const url = window.URL.createObjectURL(blob);
          const a = document.createElement('a');
          a.href = url;
          a.download = `report-${this.gameId}.pdf`;
          document.body.appendChild(a);
          a.click();
          document.body.removeChild(a);
          window.URL.revokeObjectURL(url);
        },
        error: (err) => console.error('Greška pri generisanju izveštaja:', err)
      });
    }
  }

  generateAiReport(): void {
    if (!this.canGenerateReport) return;
    
    this.isGeneratingAi = true;
    
    this.http.post(`${environment.apiHost}ai-analysis/game/${this.gameId}`, {}, { responseType: 'text' })
      .subscribe({
        next: (reportText) => {
          this.aiReportContent = reportText;
          this.isGeneratingAi = false;
        },
        error: (err) => {
          console.error('Greška pri generisanju AI izveštaja', err);
          alert('Došlo je do greške prilikom generisanja AI izveštaja. Proverite da li su servisi podignuti.');
          this.isGeneratingAi = false;
        }
      });
  }

}