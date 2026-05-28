import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ZoneService } from '../../services/zone.service';
import { ZoneDTO } from '../../models/admin.model';

@Component({
  selector: 'app-zone-list',
  templateUrl: './zone-list.component.html',
  styleUrls: ['./zone-list.component.css']
})
export class ZoneListComponent implements OnInit {
  zones: ZoneDTO[] = [];
  loading = true;
  errorMessage = '';

  constructor(private zoneService: ZoneService, private router: Router) {}

  ngOnInit(): void {
    this.loadZones();
  }

  loadZones(): void {
    this.loading = true;
    this.zoneService.getAll().subscribe({
      next: (data) => { this.zones = data; this.loading = false; },
      error: () => { this.errorMessage = 'Greška pri učitavanju zona.'; this.loading = false; }
    });
  }

  addZone(): void {
    this.router.navigate(['/add-zone']);
  }

  editZone(id: number): void {
    this.router.navigate(['/edit-zone', id]);
  }

  manageSeats(id: number): void {
    this.router.navigate(['/zone-seats', id]);
  }

  deleteZone(zone: ZoneDTO): void {
    if (!confirm(`Brisanje zone "${zone.name}"? Ova akcija je nepovratna.`)) return;
    this.zoneService.delete(zone.id!).subscribe({
      next: () => this.loadZones(),
      error: (err) => {
        this.errorMessage = err.error?.message || 'Greška pri brisanju zone.';
      }
    });
  }

  totalSeats(zone: ZoneDTO): number {
    return zone.numberOfRows * zone.seatsPerRow;
  }
}
