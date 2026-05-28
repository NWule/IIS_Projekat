import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { StadiumMapService } from '../../services/stadium-map.service';
import { StadiumMapDTO, ZoneMapDTO, SeatMapDTO, TicketTypeDTO, SelectedSeat } from '../../models/stadium-map.model';

@Component({
  selector: 'app-stadium-map',
  templateUrl: './stadium-map.component.html',
  styleUrls: ['./stadium-map.component.css']
})
export class StadiumMapComponent implements OnInit {
  gameId!: number;
  stadiumMap: StadiumMapDTO | null = null;
  ticketTypes: TicketTypeDTO[] = [];
  selectedTicketTypeId: number | null = null;
  selectedSeats: Map<number, SelectedSeat> = new Map();
  loading = true;
  errorMessage = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private stadiumMapService: StadiumMapService
  ) {}

  ngOnInit(): void {
    this.gameId = Number(this.route.snapshot.paramMap.get('gameId'));
    this.loadData();
  }

  private loadData(): void {
    this.stadiumMapService.getStadiumMap(this.gameId).subscribe({
      next: (map) => {
        this.stadiumMap = map;
        this.loadTicketTypes();
      },
      error: () => {
        this.errorMessage = 'Greška pri učitavanju mape stadiona.';
        this.loading = false;
      }
    });
  }

  private loadTicketTypes(): void {
    this.stadiumMapService.getAllTicketTypes().subscribe({
      next: (types) => {
        this.ticketTypes = types;
        if (types.length > 0) this.selectedTicketTypeId = types[0].id;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  isSeatSelected(seatId: number): boolean {
    return this.selectedSeats.has(seatId);
  }

  toggleSeat(seat: SeatMapDTO, zone: ZoneMapDTO): void {
    if (seat.status !== 'AVAILABLE') return;

    if (this.selectedSeats.has(seat.id)) {
      this.selectedSeats.delete(seat.id);
    } else {
      this.selectedSeats.set(seat.id, {
        seatId: seat.id,
        rowNumber: seat.rowNumber,
        seatNumber: seat.seatNumber,
        zoneId: zone.id,
        zoneName: zone.name,
        basePrice: zone.basePrice
      });
    }
  }

  getSeatClass(seat: SeatMapDTO): string {
    if (this.isSeatSelected(seat.id)) return 'seat selected';
    if (seat.status === 'AVAILABLE') return 'seat available';
    return 'seat unavailable';
  }

  getSelectedTicketType(): TicketTypeDTO | null {
    return this.ticketTypes.find(t => t.id === this.selectedTicketTypeId) || null;
  }

  getSeatPrice(basePrice: number): number {
    const tt = this.getSelectedTicketType();
    if (!tt) return basePrice;
    return Math.round(basePrice * tt.priceModifier * 100) / 100;
  }

  getTotalAmount(): number {
    let total = 0;
    this.selectedSeats.forEach(seat => {
      total += this.getSeatPrice(seat.basePrice);
    });
    return Math.round(total * 100) / 100;
  }

  getSelectedSeatsList(): SelectedSeat[] {
    return Array.from(this.selectedSeats.values());
  }

  canProceed(): boolean {
    return this.selectedSeats.size > 0 && this.selectedTicketTypeId !== null;
  }

  proceed(): void {
    if (!this.canProceed() || !this.stadiumMap) return;
    this.router.navigate(['/purchase', this.gameId], {
      state: {
        gameId: this.gameId,
        homeClubName: this.stadiumMap.homeClubName,
        awayClubName: this.stadiumMap.awayClubName,
        matchDate: '',
        selectedSeats: this.getSelectedSeatsList(),
        ticketTypeId: this.selectedTicketTypeId,
        ticketTypeName: this.getSelectedTicketType()?.name,
        totalAmount: this.getTotalAmount()
      }
    });
  }

  trackByZone(_: number, zone: ZoneMapDTO): number { return zone.id; }
  trackBySeat(_: number, seat: SeatMapDTO): number { return seat.id; }
}
