import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TicketTypeCrudService } from '../../services/ticket-type-crud.service';
import { TicketTypeDTO } from '../../models/stadium-map.model';

@Component({
  selector: 'app-ticket-type-list',
  templateUrl: './ticket-type-list.component.html',
  styleUrls: ['./ticket-type-list.component.css']
})
export class TicketTypeListComponent implements OnInit {
  ticketTypes: TicketTypeDTO[] = [];
  loading = true;
  errorMessage = '';

  constructor(private service: TicketTypeCrudService, private router: Router) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.service.getAll().subscribe({
      next: (data) => { this.ticketTypes = data; this.loading = false; },
      error: () => { this.errorMessage = 'Greška pri učitavanju tipova karata.'; this.loading = false; }
    });
  }

  add(): void { this.router.navigate(['/add-ticket-type']); }
  edit(id: number): void { this.router.navigate(['/edit-ticket-type', id]); }

  delete(tt: TicketTypeDTO): void {
    if (!confirm(`Brisanje tipa karte "${tt.name}"?`)) return;
    this.service.delete(tt.id!).subscribe({
      next: () => this.load(),
      error: (err) => { this.errorMessage = err.error?.message || 'Greška pri brisanju.'; }
    });
  }
}
