import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { switchMap } from 'rxjs/operators';

import { GameService } from '../../services/game.service';
import { AppearanceService } from '../../services/appearance.service';
import { TeamStatisticService } from '../../services/team-statistic.service';
import { ContractService } from '../../services/playsFor.service';
import { ClubService } from '../../services/club.service';

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
  activeTab: 'statistike' | 'performanse' = 'statistike';
  gameId!: number;
  game!: Game;
  homeClub!: Club;
  awayClub!: Club;
  myClub!: Club;

  isHomeClub = false;

  statistike: TeamStatistic | null = null;
  statsForm!: FormGroup;
  statsPostoji = false;

  homePerformanse: Appearance[] = [];
  awayPerformanse: Appearance[] = [];

  mojRoster: PlaysFor[] = [];

  showAddForma = false;
  addForma!: FormGroup;

  loading = true;

  constructor(
    private route: ActivatedRoute,
    private fb: FormBuilder,
    private gameService: GameService,
    private appearanceService: AppearanceService,
    private statisticService: TeamStatisticService,
    private contractService: ContractService,
    private clubService: ClubService
  ) {}

  ngOnInit(): void {
    this.gameId = Number(this.route.snapshot.paramMap.get('id'));
    this.initAddForma();
    this.initStatsForma();
    this.ucitajPodatke();
  }

  private ucitajPodatke(): void {
    this.loading = true;

    this.gameService.getGameById(this.gameId).pipe(
      switchMap(game => {
        this.game = game;
        return forkJoin({
          homeClub: this.clubService.getClubById(game.homeClubId),
          awayClub: this.clubService.getClubById(game.awayClubId),
          myClub: this.clubService.getMyClub(),
          performanse: this.appearanceService.getAppearancesByGame(this.gameId)
        });
      })
    ).subscribe({
      next: ({ homeClub, awayClub, myClub, performanse }) => {
        this.homeClub = homeClub;
        this.awayClub = awayClub;
        this.myClub = myClub;
        this.isHomeClub = myClub.id === this.game.homeClubId;

        this.homePerformanse = performanse.filter(p => p.clubId === this.game.homeClubId);
        this.awayPerformanse = performanse.filter(p => p.clubId === this.game.awayClubId);

        this.contractService.getCurrentRoster(myClub.id!).subscribe(roster => {
          this.mojRoster = roster;
          this.loading = false;
        });
      },
      error: (err) => {
        console.error('Greška pri učitavanju podataka:', err);
        this.loading = false;
      }
    });

    this.statisticService.getStatisticByGameId(this.gameId).subscribe({
      next: (stat) => {
        this.statistike = stat;
        this.statsForm.patchValue(stat);
        this.statsPostoji = true;
      },
      error: () => {
        this.statsPostoji = false;
      }
    });
  }

  private initStatsForma(): void {
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
      homePassAccuracy:   [0, [Validators.required, Validators.min(0), Validators.max(100)]],
      awayPassAccuracy:   [0, [Validators.required, Validators.min(0), Validators.max(100)]]
    });
  }

  private initAddForma(): void {
    this.addForma = this.fb.group({
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

  sacuvajStatistike(): void {
    if (this.statsForm.invalid) return;
    const payload: TeamStatistic = { ...this.statsForm.value, gameId: this.gameId };
    this.statisticService.saveFinalStatistic(payload).subscribe({
      next: (saved) => {
        this.statistike = saved;
        this.statsPostoji = true;
        alert('Statistike su uspešno sačuvane!');
      },
      error: (err) => console.error('Greška:', err)
    });
  }

  toggleAddForma(): void {
    this.showAddForma = !this.showAddForma;
    if (!this.showAddForma) this.addForma.reset({
      playsForId: null, minutesPlayed: 0, goals: 0, assists: 0,
      passingAccuracy: 0, fouls: 0, yellowCardCheckbox: false, redCard: false
    });
  }

  sacuvajPerformansu(): void {
    if (this.addForma.invalid) return;
    const formValue = this.addForma.value;

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
          this.homePerformanse = [...this.homePerformanse, created];
        } else {
          this.awayPerformanse = [...this.awayPerformanse, created];
        }
        alert('Performansa je uspešno sačuvana!');
        this.toggleAddForma();
      },
      error: (err) => {
        console.error('Greška:', err);
        alert('Greška prilikom čuvanja performanse!');
      }
    });
  }
}