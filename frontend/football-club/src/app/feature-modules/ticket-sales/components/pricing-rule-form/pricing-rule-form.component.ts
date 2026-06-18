import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { PricingRuleService } from '../../services/pricing-rule.service';

@Component({
  selector: 'app-pricing-rule-form',
  templateUrl: './pricing-rule-form.component.html',
  styleUrls: ['./pricing-rule-form.component.css']
})
export class PricingRuleFormComponent implements OnInit {
  form: FormGroup;
  isEdit = false;
  ruleId: number | null = null;
  loading = false;
  fetchLoading = false;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private service: PricingRuleService,
    public router: Router,
    private route: ActivatedRoute
  ) {
    this.form = this.fb.group({
      name: ['', Validators.required],
      occupancyThreshold: [null, [Validators.min(0), Validators.max(1)]],
      occupancyConditionAtLeast: [true],
      hoursBeforeMatch: [null, [Validators.min(1)]],
      hoursConditionAtLeast: [false],
      changePercent: [null],
      minPrice: [null, [Validators.min(0)]],
      maxPrice: [null, [Validators.min(0)]],
      active: [true]
    });
  }

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.isEdit = true;
      this.ruleId = Number(idParam);
      this.fetchLoading = true;
      this.service.getById(this.ruleId).subscribe({
        next: (rule) => { this.form.patchValue(rule); this.fetchLoading = false; },
        error: () => { this.errorMessage = 'Pravilo nije pronađeno.'; this.fetchLoading = false; }
      });
    }
  }

  get title(): string {
    return this.isEdit ? 'Izmena cenovnog pravila' : 'Dodavanje cenovnog pravila';
  }

  submit(): void {
    if (this.form.invalid) return;
    this.loading = true;
    this.errorMessage = '';

    const payload = { ...this.form.value, gameIds: this.isEdit ? undefined : [] };
    const request = this.isEdit
      ? this.service.update(this.ruleId!, payload)
      : this.service.create({ ...payload, gameIds: [] });

    request.subscribe({
      next: () => { this.loading = false; this.router.navigate(['/pricing-rules']); },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err.error?.message || 'Greška pri čuvanju pravila.';
      }
    });
  }
}
