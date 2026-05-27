import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ClubService } from '../../services/club.service'; 
import { Club } from '../../models/club.model';             

@Component({
  selector: 'app-club-edit',
  templateUrl: './club-edit.component.html',
  styleUrls: ['./club-edit.component.css']
})
export class ClubEditComponent implements OnInit {
  clubForm!: FormGroup;
  clubId!: number;
  isLoading = true;

  constructor(
    private fb: FormBuilder,
    private clubService: ClubService,
    private route: ActivatedRoute,
    private router: Router         
  ) {}

  ngOnInit(): void {
    this.initForm();

    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.clubId = Number(idParam);
      this.loadClubData();
    } else {
      console.error('ID kluba nije prosleđen u ruti!');
      this.isLoading = false;
    }
  }

  private initForm(): void {
    this.clubForm = this.fb.group({
      naziv: ['', Validators.required],
      lokacija: ['', Validators.required],
      pobede: [0, [Validators.required, Validators.min(0)]],
      porazi: [0, [Validators.required, Validators.min(0)]],
      neresene: [0, [Validators.required, Validators.min(0)]],
      golovi: [0, [Validators.required, Validators.min(0)]],
      primljeniGolovi: [0, [Validators.required, Validators.min(0)]],
      prosecanPosed: [0, [Validators.required, Validators.min(0), Validators.max(100)]],
      faulovi: [0, [Validators.required, Validators.min(0)]],
      prosecnoDodavanja: [0, [Validators.required, Validators.min(0)]]
    });
  }

  private loadClubData(): void {
    this.clubService.getClubById(this.clubId).subscribe({
      next: (club: Club) => {
        this.clubForm.patchValue({
          naziv: club.name,
          lokacija: club.location,
          pobede: club.wins,
          porazi: club.losses,
          neresene: club.draws,
          golovi: club.goalsScored,
          primljeniGolovi: club.goalsConceded
        });
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Greška pri učitavanju podataka o klubu:', err);
        alert('Nije moguće učitati podatke o klubu.');
        this.isLoading = false;
      }
    });
  }

  onSubmit(): void {
    if (this.clubForm.valid) {
      const formValue = this.clubForm.value;

      const updatedClubData: Club = {
        id: this.clubId,
        name: formValue.naziv,
        location: formValue.lokacija,
        wins: formValue.pobede,
        losses: formValue.porazi,
        draws: formValue.neresene,
        goalsScored: formValue.golovi,
        goalsConceded: formValue.primljeniGolovi
      };

      this.clubService.updateClub(this.clubId, updatedClubData).subscribe({
        next: (response) => {
          alert(`Uspešno izmenjen klub: ${response.name}`);
        },
        error: (err) => {
          console.error('Greška pri izmeni kluba:', err);
          alert(err.error?.message || 'Došlo je do greške prilikom izmene.');
        }
      });
    } else {
      this.clubForm.markAllAsTouched();
    }
  }
}