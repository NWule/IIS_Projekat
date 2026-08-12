import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ClubService } from '../../services/club.service';
import { PlayerService } from '../../services/player.service'; 
import { ContractService } from '../../services/playsFor.service'; 
import { Club } from '../../models/club.model';
import { switchMap, of } from 'rxjs'; 

// ptre

@Component({
  selector: 'app-player-entry',
  templateUrl: './player-entry.component.html',
  styleUrls: ['./player-entry.component.css']
})
export class PlayerEntryComponent implements OnInit {
  playerForm!: FormGroup;
  clubs: Club[] = []; 
  selectedFile: File | null = null;
  imagePreview: string | ArrayBuffer | null = null;

  playerPositions: string[] = [
  'GOALKEEPER',
  'CENTER_BACK',
  'WING_BACK',
  'DEFENSIVE_MIDFIELDER',
  'CENTRAL_MIDFIELDER',
  'ATTACKING_MIDFIELDER',
  'WIDE_MIDFIELDER',
  'STRIKER',
  'WINGER'
];

  constructor(
    private fb: FormBuilder,
    private clubService: ClubService,
    private playerService: PlayerService,
    private contractService: ContractService 
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.loadClubs();
  }

  private initForm(): void {
    this.playerForm = this.fb.group({
      ime: ['', Validators.required],
      prezime: ['', Validators.required],
      datumRodjenja: ['', Validators.required],
      pozicija: [null], 
      trenutniKlubId: [null], 
      brojNaDresu: [null, [Validators.min(1), Validators.max(99)]],
      contractStart: [''],
      contractEnd: ['']
    });

    this.playerForm.get('trenutniKlubId')?.valueChanges.subscribe(clubId => {
      const jerseyCtrl = this.playerForm.get('brojNaDresu');
      const startCtrl = this.playerForm.get('contractStart');
      const endCtrl = this.playerForm.get('contractEnd');

      if (clubId && clubId !== 'null') {
        jerseyCtrl?.setValidators([Validators.required, Validators.min(1), Validators.max(99)]);
        startCtrl?.setValidators([Validators.required]);
        endCtrl?.setValidators([Validators.required]);
      } else {
        jerseyCtrl?.clearValidators();
        startCtrl?.clearValidators();
        endCtrl?.clearValidators();
      }
      jerseyCtrl?.updateValueAndValidity();
      startCtrl?.updateValueAndValidity();
      endCtrl?.updateValueAndValidity();
    });
  }

  private loadClubs(): void {
    this.clubService.getAllClubs().subscribe({
      next: (data) => this.clubs = data,
      error: (err) => console.error('Greška pri učitavanju klubova:', err)
    });
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
      const reader = new FileReader();
      reader.onload = e => this.imagePreview = reader.result;
      reader.readAsDataURL(file);
    }
  }

  onSubmit(): void {
    if (this.playerForm.valid) {
      const formValue = this.playerForm.value;

      const playerData = {
        name: formValue.ime,
        surname: formValue.prezime,
        dateOfBirth: formValue.datumRodjenja,
        playerPosition: formValue.pozicija 
      };

      this.playerService.createPlayer(playerData).pipe(
        switchMap((savedPlayer) => {
          let imageUploadObs = of(savedPlayer); 
          if (this.selectedFile && savedPlayer.id) {
            const formData = new FormData();
            formData.append('file', this.selectedFile);
            imageUploadObs = this.playerService.uploadPlayerImage(savedPlayer.id, formData);
          }

          return imageUploadObs.pipe(
            switchMap(() => {
              if (formValue.trenutniKlubId && formValue.trenutniKlubId !== 'null' && savedPlayer.id) {
                const contractData = {
                  playerId: savedPlayer.id, 
                  clubId: Number(formValue.trenutniKlubId),
                  jerseyNumber: Number(formValue.brojNaDresu),
                  contractStart: formValue.contractStart,
                  contractEnd: formValue.contractEnd
                };
                return this.contractService.createContract(contractData);
              } else {
                return of(null);
              }
            })
          );
        })
      ).subscribe({
        next: (contractRes) => {
          alert('Igrač, slika (opciono) i ugovor su uspešno sačuvani u bazi!');
          this.playerForm.reset();
          this.selectedFile = null;
          this.imagePreview = null;
        },
        error: (err) => {
          alert(err.error?.message || 'Došlo je do greške prilikom čuvanja.');
          console.error('Greška u lancu:', err);
        }
      });
    } else {
      this.playerForm.markAllAsTouched();
    }
  }
}