import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ClubService } from '../../services/club.service';
import { PlayerService } from '../../services/player.service';
import { ContractService } from '../../services/playsFor.service';
import { Club } from '../../models/club.model';
import { forkJoin, of, Observable } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';
import { environment } from 'src/env/environment';

// ptre

@Component({
  selector: 'app-player-edit',
  templateUrl: './player-edit.component.html',
  styleUrls: ['./player-edit.component.css']
})
export class PlayerEditComponent implements OnInit {
  playerForm!: FormGroup;
  clubs: Club[] = [];
  playerId!: number;
  currentContractId: number | null = null; 
  isLoading = true;
  selectedFile: File | null = null;
  imagePreview: string | ArrayBuffer | null = null;

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
    private contractService: ContractService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.initForm();

    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.playerId = Number(idParam);
      this.loadAllData();
    } else {
      console.error('ID igrača nije prosleđen!');
      this.isLoading = false;
    }
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

  private loadAllData(): void {
    forkJoin({
      clubs: this.clubService.getAllClubs(),
      player: this.playerService.getPlayerById(this.playerId),
      contract: this.contractService.getCurrentContract(this.playerId).pipe(
        catchError(() => of(null))
      )
    }).subscribe({
      next: (res) => {
        this.clubs = res.clubs;

        this.playerForm.patchValue({
          ime: res.player.name,
          prezime: res.player.surname,
          datumRodjenja: res.player.dateOfBirth,
          pozicija: res.player.playerPosition || ''
        });

        if (res.contract) {
          this.currentContractId = res.contract.id || null;
          this.playerForm.patchValue({
            trenutniKlubId: res.contract.clubId,
            brojNaDresu: res.contract.jerseyNumber,
            contractStart: res.contract.contractStart,
            contractEnd: res.contract.contractEnd
          });
        }

        if (res.player.imagePath) {
          const baseUrl = environment.apiHost.replace('/api/', ''); 
          this.imagePreview = baseUrl + res.player.imagePath;
        }

        if (res.contract) {
          this.currentContractId = res.contract.id || null;
          this.playerForm.patchValue({
            trenutniKlubId: res.contract.clubId,
            brojNaDresu: res.contract.jerseyNumber,
            contractStart: res.contract.contractStart,
            contractEnd: res.contract.contractEnd
          });
        }

        this.isLoading = false;
      },
      error: (err) => {
        console.error('Greška pri učitavanju podataka:', err);
        alert('Nije moguće učitati podatke za izmenu.');
        this.isLoading = false;
      }
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

      const updatedPlayerData = {
        id: this.playerId,
        name: formValue.ime,
        surname: formValue.prezime,
        dateOfBirth: formValue.datumRodjenja,
        position: formValue.pozicija
      };

      this.playerService.updatePlayer(this.playerId, updatedPlayerData).pipe(
        switchMap(() => {
          let imageUploadObs: Observable<any> = of(null);
          if (this.selectedFile) {
            const formData = new FormData();
            formData.append('file', this.selectedFile);
            imageUploadObs = this.playerService.uploadPlayerImage(this.playerId, formData);
          }

          return imageUploadObs.pipe(
            switchMap(() => {
              const hasSelectedClub = formValue.trenutniKlubId && formValue.trenutniKlubId !== 'null';
              if (hasSelectedClub) {
                const contractPayload: any = {
                  playerId: this.playerId,
                  clubId: Number(formValue.trenutniKlubId),
                  jerseyNumber: Number(formValue.brojNaDresu),
                  contractStart: formValue.contractStart,
                  contractEnd: formValue.contractEnd
                };
    
                if (this.currentContractId) {
                  contractPayload.id = this.currentContractId;
                  return this.contractService.updateContract(this.currentContractId, contractPayload);
                } else {
                  return this.contractService.createContract(contractPayload);
                }
              } else {
                return of(null);
              }
            })
          );
        })
      ).subscribe({
        next: (contractRes) => {
          alert('Podaci o igraču su uspešno ažurirani!');
        },
        error: (err) => {
          console.error('Greška prilikom izmene:', err);
          alert(err.error?.message || 'Došlo je do greške prilikom čuvanja izmena.');
        }
      });
    } else {
      this.playerForm.markAllAsTouched();
    }
  }
}