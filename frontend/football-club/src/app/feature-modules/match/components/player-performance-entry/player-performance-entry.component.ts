import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { PlayerService } from '../../services/player.service';
import { AppearanceService } from '../../services/appearance.service';
import { Player } from '../../models/player.model';

@Component({
  selector: 'app-player-performance-entry',
  templateUrl: './player-performance-entry.component.html',
  styleUrls: ['./player-performance-entry.component.css']
})
export class PlayerPerformanceEntryComponent implements OnInit {
  performanceForm!: FormGroup;
  players: Player[] = [];
  
  temporaryGameId: number = 1; 

  constructor(
    private fb: FormBuilder,
    private playerService: PlayerService,
    private appearanceService: AppearanceService
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.loadPlayers();
  }

  private initForm(): void {
    this.performanceForm = this.fb.group({
      playsForId: [null, Validators.required],
      minutesPlayed: [0, [Validators.required, Validators.min(0), Validators.max(120)]],
      goals: [0, [Validators.required, Validators.min(0)]],
      assists: [0, [Validators.required, Validators.min(0)]],
      passingAccuracy: [0, [Validators.required, Validators.min(0), Validators.max(100)]],
      fouls: [0, [Validators.required, Validators.min(0)]],
      yellowCardCheckbox: [false],
      redCard: [false]
    });
  }

  private loadPlayers(): void {
    this.playerService.getAllPlayers().subscribe({
      next: (data) => this.players = data,
      error: (err) => console.error('Greška pri učitavanju igrača:', err)
    });
  }

  onSubmit(): void {
    if (this.performanceForm.valid) {
      const formValue = this.performanceForm.value;

      const appearancePayload: any = {
        gameId: this.temporaryGameId,
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

      this.appearanceService.createAppearance(appearancePayload).subscribe({
        next: (response) => {
          alert('Performanse igrača su uspešno sačuvane u bazu!');
          this.resetFormAfterSubmit();
        },
        error: (err) => {
          console.error('Greška sa servera:', err);
          alert('Greška prilikom slanja! Proveri konzolu i poklapanje tipova podataka.');
        }
      });
    } else {
      this.performanceForm.markAllAsTouched();
    }
  }

  private resetFormAfterSubmit(): void {
    this.performanceForm.reset({
      playsForId: null,
      minutesPlayed: 0,
      goals: 0,
      assists: 0,
      passingAccuracy: 0,
      fouls: 0,
      yellowCardCheckbox: false,
      redCard: false
    });
  }
}