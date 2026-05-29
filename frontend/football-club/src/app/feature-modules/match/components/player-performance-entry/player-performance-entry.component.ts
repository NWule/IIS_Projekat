import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AppearanceService } from '../../services/appearance.service';
import { ContractService } from '../../services/playsFor.service';
import { ClubService } from '../../services/club.service';
import { PlaysFor } from '../../models/player.model';

@Component({
  selector: 'app-player-performance-entry',
  templateUrl: './player-performance-entry.component.html',
  styleUrls: ['./player-performance-entry.component.css']
})
export class PlayerPerformanceEntryComponent implements OnInit {
  performanceForm!: FormGroup;
  roster: PlaysFor[] = [];
  gameId!: number;

  constructor(
    private fb: FormBuilder,
    private appearanceService: AppearanceService,
    private contractService: ContractService,
    private clubService: ClubService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.initForm();

    this.route.queryParams.subscribe(params => {
      if (params['gameId']) {
        this.gameId = Number(params['gameId']);
      }
    });

    this.clubService.getMyClub().subscribe({
      next: (club) => {
        this.contractService.getCurrentRoster(club.id!).subscribe({
          next: (data) => this.roster = data,
          error: (err) => console.error('Greška pri učitavanju rostera:', err)
        });
      },
      error: (err) => console.error('Greška pri učitavanju kluba:', err)
    });
  }

  private initForm(): void {
    this.performanceForm = this.fb.group({
      playsForId:      [null, Validators.required],
      minutesPlayed:   [0,    [Validators.required, Validators.min(0), Validators.max(120)]],
      goals:           [0,    [Validators.required, Validators.min(0)]],
      assists:         [0,    [Validators.required, Validators.min(0)]],
      passingAccuracy: [0,    [Validators.required, Validators.min(0), Validators.max(100)]],
      fouls:           [0,    [Validators.required, Validators.min(0)]],
      yellowCardCheckbox: [false],
      redCard:         [false]
    });
  }

  onSubmit(): void {
    if (this.performanceForm.valid) {
      const formValue = this.performanceForm.value;

      const appearancePayload = {
        gameId:          this.gameId,
        playsForId:      Number(formValue.playsForId),
        minutesPlayed:   Number(formValue.minutesPlayed),
        goals:           Number(formValue.goals),
        assists:         Number(formValue.assists),
        passingAccuracy: Number(formValue.passingAccuracy),
        fouls:           Number(formValue.fouls),
        yellowCards:     formValue.yellowCardCheckbox ? 1 : 0,
        redCard:         formValue.redCard,
        rating:          0.0
      };

      this.appearanceService.createAppearance(appearancePayload).subscribe({
        next: () => {
          alert('Performanse igrača su uspešno sačuvane!');
          this.router.navigate(['/match-details', this.gameId]);
        },
        error: (err) => {
          console.error('Greška sa servera:', err);
          alert(err.error?.message || 'Greška prilikom slanja!');
        }
      });
    } else {
      this.performanceForm.markAllAsTouched();
    }
  }
}