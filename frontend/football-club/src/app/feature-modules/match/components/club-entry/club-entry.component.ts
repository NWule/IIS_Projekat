import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ClubService } from '../../services/club.service';
import { Club } from '../../models/club.model'; 
import { switchMap, of, Observable } from 'rxjs';

// ptre

@Component({
  selector: 'app-club-entry',
  templateUrl: './club-entry.component.html',
  styleUrls: ['./club-entry.component.css']
})
export class ClubEntryComponent implements OnInit {
  clubForm!: FormGroup;
  selectedFile: File | null = null;
  imagePreview: string | ArrayBuffer | null = null;

  constructor(
    private fb: FormBuilder,
    private clubService: ClubService
  ) {}

  ngOnInit(): void {
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
    if (this.clubForm.valid) {
      const formValue = this.clubForm.value;

      const clubDtoData: Omit<Club, 'id'> = {
        name: formValue.naziv,
        location: formValue.lokacija,
        wins: formValue.pobede,
        losses: formValue.porazi,
        draws: formValue.neresene,
        goalsScored: formValue.golovi,
        goalsConceded: formValue.primljeniGolovi
      };

      this.clubService.createClub(clubDtoData).pipe(
        switchMap((savedClub) => {
          let imageUploadObs: Observable<any> = of(savedClub);
          if (this.selectedFile && savedClub.id) {
            const formData = new FormData();
            formData.append('file', this.selectedFile);
            imageUploadObs = this.clubService.uploadClubImage(savedClub.id, formData);
          }
          return imageUploadObs;
        })
      ).subscribe({
        next: (finalClub) => {
          alert(`Uspešno ste kreirali klub: ${finalClub.name}`);
          this.clubForm.reset({
            naziv: '', lokacija: '', pobede: 0, porazi: 0, neresene: 0,
            golovi: 0, primljeniGolovi: 0, prosecanPosed: 0, faulovi: 0, prosecnoDodavanja: 0
          });
          this.selectedFile = null;
          this.imagePreview = null;
        },
        error: (err) => {
          alert(err.error?.message || 'Došlo je do greške prilikom čuvanja kluba.');
        }
      });
    } else {
      this.clubForm.markAllAsTouched(); 
    }
  }
}