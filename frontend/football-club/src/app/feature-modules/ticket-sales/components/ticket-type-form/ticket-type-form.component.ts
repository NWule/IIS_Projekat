import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { TicketTypeCrudService } from '../../services/ticket-type-crud.service';

@Component({
  selector: 'app-ticket-type-form',
  templateUrl: './ticket-type-form.component.html',
  styleUrls: ['./ticket-type-form.component.css']
})
export class TicketTypeFormComponent implements OnInit {
  form: FormGroup;
  isEdit = false;
  typeId: number | null = null;
  loading = false;
  fetchLoading = false;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private service: TicketTypeCrudService,
    public router: Router,
    private route: ActivatedRoute
  ) {
    this.form = this.fb.group({
      name: ['', Validators.required],
      description: ['', Validators.required],
      priceModifier: [1.0, [Validators.required, Validators.min(0.01)]]
    });
  }

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.isEdit = true;
      this.typeId = Number(idParam);
      this.fetchLoading = true;
      this.service.getById(this.typeId).subscribe({
        next: (tt) => { this.form.patchValue(tt); this.fetchLoading = false; },
        error: () => { this.errorMessage = 'Tip karte nije pronađen.'; this.fetchLoading = false; }
      });
    }
  }

  get title(): string {
    return this.isEdit ? 'Izmena tipa karte' : 'Dodavanje tipa karte';
  }

  get previewPrice(): number {
    const modifier = this.form.get('priceModifier')?.value || 1;
    return Math.round(100 * modifier * 100) / 100;
  }

  submit(): void {
    if (this.form.invalid) return;
    this.loading = true;
    this.errorMessage = '';

    const payload = this.form.value;
    const request = this.isEdit
      ? this.service.update(this.typeId!, payload)
      : this.service.create(payload);

    request.subscribe({
      next: () => { this.loading = false; this.router.navigate(['/ticket-types']); },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err.error?.message || 'Greška pri čuvanju tipa karte.';
      }
    });
  }
}
