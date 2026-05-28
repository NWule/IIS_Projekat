import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ZoneService } from '../../services/zone.service';

@Component({
  selector: 'app-zone-form',
  templateUrl: './zone-form.component.html',
  styleUrls: ['./zone-form.component.css']
})
export class ZoneFormComponent implements OnInit {
  form: FormGroup;
  isEdit = false;
  zoneId: number | null = null;
  loading = false;
  fetchLoading = false;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private zoneService: ZoneService,
    public router: Router,
    private route: ActivatedRoute
  ) {
    this.form = this.fb.group({
      name: ['', Validators.required],
      color: ['#3f51b5', Validators.required],
      basePrice: [null, [Validators.required, Validators.min(0)]],
      numberOfRows: [null, [Validators.required, Validators.min(1)]],
      seatsPerRow: [null, [Validators.required, Validators.min(1)]]
    });
  }

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.isEdit = true;
      this.zoneId = Number(idParam);
      this.fetchLoading = true;
      this.zoneService.getById(this.zoneId).subscribe({
        next: (zone) => {
          this.form.patchValue(zone);
          this.fetchLoading = false;
        },
        error: () => {
          this.errorMessage = 'Zona nije pronađena.';
          this.fetchLoading = false;
        }
      });
    }
  }

  get title(): string {
    return this.isEdit ? 'Izmena zone' : 'Dodavanje zone';
  }

  submit(): void {
    if (this.form.invalid) return;
    this.loading = true;
    this.errorMessage = '';

    const payload = this.form.value;
    const request = this.isEdit
      ? this.zoneService.update(this.zoneId!, payload)
      : this.zoneService.create(payload);

    request.subscribe({
      next: () => { this.loading = false; this.router.navigate(['/zones']); },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err.error?.message || 'Greška pri čuvanju zone.';
      }
    });
  }
}
