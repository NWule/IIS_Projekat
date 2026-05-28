import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { PurchaseService } from '../../services/purchase.service';
import { SelectedSeat } from '../../models/stadium-map.model';
import { PurchaseRequestDTO } from '../../models/purchase.model';

@Component({
  selector: 'app-purchase-form',
  templateUrl: './purchase-form.component.html',
  styleUrls: ['./purchase-form.component.css']
})
export class PurchaseFormComponent implements OnInit {
  form: FormGroup;
  gameId!: number;
  homeClubName = '';
  awayClubName = '';
  selectedSeats: SelectedSeat[] = [];
  ticketTypeId!: number;
  ticketTypeName = '';
  totalAmount = 0;
  loading = false;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private purchaseService: PurchaseService,
    public router: Router,
    private route: ActivatedRoute
  ) {
    this.form = this.fb.group({
      buyerFirstName: ['', Validators.required],
      buyerLastName: ['', Validators.required],
      buyerEmail: ['', [Validators.required, Validators.email]],
      cardHolderName: ['', Validators.required],
      cardNumber: ['', [Validators.required, Validators.pattern(/^[\d\s]{13,19}$/)]],
      cardExpiry: ['', [Validators.required, Validators.pattern(/^(0[1-9]|1[0-2])\/\d{2}$/)]],
      cvv: ['', [Validators.required, Validators.pattern(/^\d{3,4}$/)]]
    });
  }

  ngOnInit(): void {
    const nav = this.router.getCurrentNavigation();
    const state = (nav?.extras?.state || window.history.state) as any;

    this.gameId = Number(this.route.snapshot.paramMap.get('gameId'));
    this.homeClubName = state?.homeClubName || '';
    this.awayClubName = state?.awayClubName || '';
    this.selectedSeats = state?.selectedSeats || [];
    this.ticketTypeId = state?.ticketTypeId;
    this.ticketTypeName = state?.ticketTypeName || '';
    this.totalAmount = state?.totalAmount || 0;

    if (this.selectedSeats.length === 0) {
      this.router.navigate(['/search-matches']);
    }
  }

  submit(): void {
    if (this.form.invalid) return;
    this.loading = true;
    this.errorMessage = '';

    const v = this.form.value;
    const request: PurchaseRequestDTO = {
      gameId: this.gameId,
      buyerFirstName: v.buyerFirstName,
      buyerLastName: v.buyerLastName,
      buyerEmail: v.buyerEmail,
      cardHolderName: v.cardHolderName,
      cardNumber: v.cardNumber.replace(/\s/g, ''),
      cardExpiry: v.cardExpiry,
      cvv: v.cvv,
      items: this.selectedSeats.map(s => ({
        seatId: s.seatId,
        ticketTypeId: this.ticketTypeId
      }))
    };

    this.purchaseService.createPurchase(request).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/my-tickets']);
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err.error?.message || err.error || 'Greška pri kupovini. Proverite podatke.';
      }
    });
  }
}
