import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ClubService } from '../../services/club.service';
import { PlayerService } from '../../services/player.service'; 
import { ContractService } from '../../services/playsFor.service'; 
import { Club } from '../../models/club.model';
import { switchMap, of } from 'rxjs'; 

@Component({
  selector: 'app-player-entry',
  templateUrl: './player-entry.component.html',
  styleUrls: ['./player-entry.component.css']
})
export class PlayerEntryComponent implements OnInit {
  playerForm!: FormGroup;
  clubs: Club[] = []; 

  // Lista mogućih pozicija igrača (ista kao u edit komponenti)
  playerPositions: string[] = [
    'Goalkeeper',
    'Defender',
    'Midfielder',
    'Forward',
    'Striker',
    'Winger'
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
      pozicija: [''], 
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

  onSubmit(): void {
    if (this.playerForm.valid) {
      const formValue = this.playerForm.value;

      const playerData = {
        name: formValue.ime,
        surname: formValue.prezime,
        dateOfBirth: formValue.datumRodjenja,
        position: formValue.pozicija 
      };

      console.log('1. Kreiram igrača:', playerData);

      this.playerService.createPlayer(playerData).pipe(
        switchMap((savedPlayer) => {
          console.log('Igrač kreiran na bekend-u. Dobijeni objekat:', savedPlayer);

          if (formValue.trenutniKlubId && formValue.trenutniKlubId !== 'null' && savedPlayer.id) {
            
            const contractData = {
              playerId: savedPlayer.id, 
              clubId: Number(formValue.trenutniKlubId),
              jerseyNumber: Number(formValue.brojNaDresu),
              contractStart: formValue.contractStart,
              contractEnd: formValue.contractEnd
            };

            console.log('2. Kreiram ugovor sa dobijenim ID-jem igrača:', contractData);
            return this.contractService.createContract(contractData);
          } else {
            return of(null);
          }
        })
      ).subscribe({
        next: (contractRes) => {
          if (contractRes) {
            alert('Igrač i ugovor su uspešno sačuvani u bazi!');
          } else {
            alert('Igrač je uspešno kreiran (slobodan igrač, bez ugovora)!');
          }
          
          this.playerForm.reset({
            ime: '',
            prezime: '',
            datumRodjenja: '',
            pozicija: '',
            trenutniKlubId: null,
            brojNaDresu: null,
            contractStart: '',
            contractEnd: ''
          });
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