import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, AbstractControl } from '@angular/forms';
import { ClubService } from '../../services/club.service';
import { GameService } from '../../services/game.service';
import { TeamStatisticService } from '../../services/team-statistic.service';
import { Club } from '../../models/club.model';
import { switchMap } from 'rxjs/operators';

// stat

@Component({
  selector: 'app-match-entry',
  templateUrl: './match-entry.component.html',
  styleUrls: ['./match-entry.component.css']
})
export class MatchEntryComponent implements OnInit {
  matchForm!: FormGroup;
  clubs: Club[] = [];

  constructor(
    private fb: FormBuilder,
    private clubService: ClubService,
    private gameService: GameService,
    private statisticService: TeamStatisticService
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.loadClubs();
  }

  private initForm(): void {
    this.matchForm = this.fb.group({
      homeClubId: [null, Validators.required],
      awayClubId: [null, Validators.required],
      
      homeGoals: [0, [Validators.required, Validators.min(0)]],
      awayGoals: [0, [Validators.required, Validators.min(0)]],
      
      homeShots: [0, [Validators.required, Validators.min(0)]],
      awayShots: [0, [Validators.required, Validators.min(0)]],
      
      homeShotsOnTarget: [0, [Validators.required, Validators.min(0)]],
      awayShotsOnTarget: [0, [Validators.required, Validators.min(0)]],
      
      homePossession: [50, [Validators.required, Validators.min(0), Validators.max(100)]],
      awayPossession: [50, [Validators.required, Validators.min(0), Validators.max(100)]],
      
      homeCorners: [0, [Validators.required, Validators.min(0)]],
      awayCorners: [0, [Validators.required, Validators.min(0)]],
      
      homeFouls: [0, [Validators.required, Validators.min(0)]],
      awayFouls: [0, [Validators.required, Validators.min(0)]],
      
      homeOffsides: [0, [Validators.required, Validators.min(0)]],
      awayOffsides: [0, [Validators.required, Validators.min(0)]],
      
      homePassAccuracy: [80, [Validators.required, Validators.min(0), Validators.max(100)]],
      awayPassAccuracy: [80, [Validators.required, Validators.min(0), Validators.max(100)]],
      
      day: ['', [Validators.required, Validators.min(1), Validators.max(31)]],
      month: ['', [Validators.required, Validators.min(1), Validators.max(12)]],
      year: ['', [Validators.required, Validators.minLength(4), Validators.maxLength(4)]]
    }, { validators: this.clubsDifferentValidator });
  }

  private clubsDifferentValidator(control: AbstractControl): { [key: string]: boolean } | null {
    const home = control.get('homeClubId')?.value;
    const away = control.get('awayClubId')?.value;
    if (home && away && home === away) {
      return { sameClubs: true };
    }
    return null;
  }

  private loadClubs(): void {
    this.clubService.getAllClubs().subscribe({
      next: (data) => this.clubs = data,
      error: (err) => console.error('Greška pri učitavanju klubova:', err)
    });
  }

  onSubmit(): void {
    if (this.matchForm.valid) {
      const formValue = this.matchForm.value;

      const formattedDay = String(formValue.day).padStart(2, '0');
      const formattedMonth = String(formValue.month).padStart(2, '0');
      const isoDateTime = `${formValue.year}-${formattedMonth}-${formattedDay}T15:00:00`;

      const gamePayload = {
        homeClubId: Number(formValue.homeClubId),
        awayClubId: Number(formValue.awayClubId),
        matchDate: isoDateTime,
        status: 'FINISHED' as const
      };

      this.gameService.createGame(gamePayload).pipe(
        switchMap((createdGame) => {
          console.log('Utakmica uspešno kreirana pod ID-jem:', createdGame.id);

          const statisticPayload = {
            gameId: createdGame.id!,
            homeGoals: Number(formValue.homeGoals),
            awayGoals: Number(formValue.awayGoals),
            homeShots: Number(formValue.homeShots),
            awayShots: Number(formValue.awayShots),
            homeShotsOnTarget: Number(formValue.homeShotsOnTarget),
            awayShotsOnTarget: Number(formValue.awayShotsOnTarget),
            homePossession: Number(formValue.homePossession),
            awayPossession: Number(formValue.awayPossession),
            homeCorners: Number(formValue.homeCorners),
            awayCorners: Number(formValue.awayCorners),
            homeFouls: Number(formValue.homeFouls),
            awayFouls: Number(formValue.awayFouls),
            homeOffsides: Number(formValue.homeOffsides),
            awayOffsides: Number(formValue.awayOffsides),
            homePassAccuracy: Number(formValue.homePassAccuracy),
            awayPassAccuracy: Number(formValue.awayPassAccuracy)
          };

          return this.statisticService.saveFinalStatistic(statisticPayload);
        })
      ).subscribe({
        next: (statRes) => {
          alert('Utakmica i njena finalna statistika su uspešno sačuvane!');
          this.matchForm.reset();
        },
        error: (err) => {
          console.error('Greška u toku čuvanja:', err);
          alert(err.error || 'Došlo je do greške prilikom čuvanja utakmice.');
        }
      });

    } else {
      this.matchForm.markAllAsTouched();
    }
  }
}