import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { SeatAdminService } from '../../services/seat-admin.service';
import { ZoneService } from '../../services/zone.service';
import { SeatAdminDTO, SeatStatus } from '../../models/admin.model';
import { ZoneDTO } from '../../models/admin.model';

@Component({
  selector: 'app-seat-list',
  templateUrl: './seat-list.component.html',
  styleUrls: ['./seat-list.component.css']
})
export class SeatListComponent implements OnInit {
  zoneId!: number;
  zone: ZoneDTO | null = null;
  seats: SeatAdminDTO[] = [];
  loading = true;
  generating = false;
  errorMessage = '';
  editingSeatId: number | null = null;
  editingStatus: SeatStatus = 'AVAILABLE';

  readonly statusOptions: SeatStatus[] = ['AVAILABLE', 'RESERVED', 'SOLD'];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private seatService: SeatAdminService,
    private zoneService: ZoneService
  ) {}

  ngOnInit(): void {
    this.zoneId = Number(this.route.snapshot.paramMap.get('zoneId'));
    this.zoneService.getById(this.zoneId).subscribe({
      next: (z) => { this.zone = z; }
    });
    this.loadSeats();
  }

  loadSeats(): void {
    this.loading = true;
    this.seatService.getByZone(this.zoneId).subscribe({
      next: (data) => { this.seats = data; this.loading = false; },
      error: () => { this.errorMessage = 'Greška pri učitavanju sedišta.'; this.loading = false; }
    });
  }

  startEdit(seat: SeatAdminDTO): void {
    this.editingSeatId = seat.id;
    this.editingStatus = seat.status;
  }

  cancelEdit(): void {
    this.editingSeatId = null;
  }

  saveStatus(seat: SeatAdminDTO): void {
    this.seatService.update(seat.id, {
      rowNumber: seat.rowNumber,
      seatNumber: seat.seatNumber,
      status: this.editingStatus,
      zoneId: this.zoneId
    }).subscribe({
      next: (updated) => {
        seat.status = updated.status;
        this.editingSeatId = null;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Greška pri izmeni statusa.';
      }
    });
  }

  deleteSeat(seat: SeatAdminDTO): void {
    if (!confirm(`Brisanje sedišta Red ${seat.rowNumber}, Mesto ${seat.seatNumber}?`)) return;
    this.seatService.delete(seat.id).subscribe({
      next: () => this.loadSeats(),
      error: (err) => { this.errorMessage = err.error?.message || 'Greška pri brisanju sedišta.'; }
    });
  }

  statusLabel(status: SeatStatus): string {
    const map: Record<SeatStatus, string> = {
      AVAILABLE: 'Dostupno',
      RESERVED: 'Rezervisano',
      SOLD: 'Prodato'
    };
    return map[status];
  }

  statusClass(status: SeatStatus): string {
    const map: Record<SeatStatus, string> = {
      AVAILABLE: 'status-available',
      RESERVED: 'status-reserved',
      SOLD: 'status-sold'
    };
    return map[status];
  }

  generateSeats(): void {
    if (!confirm(`Generisati sva sedišta za zonu "${this.zone?.name}"? Postojeća sedišta neće biti duplirana.`)) return;
    this.generating = true;
    this.errorMessage = '';
    this.seatService.generateForZone(this.zoneId).subscribe({
      next: (created) => {
        this.generating = false;
        this.loadSeats();
      },
      error: (err) => {
        this.generating = false;
        this.errorMessage = err.error?.message || 'Greška pri generisanju sedišta.';
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/zones']);
  }

  get rowGroups(): number[] {
    const rows = [...new Set(this.seats.map(s => s.rowNumber))];
    return rows.sort((a, b) => a - b);
  }

  seatsInRow(row: number): SeatAdminDTO[] {
    return this.seats.filter(s => s.rowNumber === row).sort((a, b) => a.seatNumber - b.seatNumber);
  }
}
