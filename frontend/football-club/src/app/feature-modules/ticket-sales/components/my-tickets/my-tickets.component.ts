import { Component, OnInit } from '@angular/core';
import { TicketService } from '../../services/ticket.service';
import { TicketDTO } from '../../models/purchase.model';

@Component({
  selector: 'app-my-tickets',
  templateUrl: './my-tickets.component.html',
  styleUrls: ['./my-tickets.component.css']
})
export class MyTicketsComponent implements OnInit {
  tickets: TicketDTO[] = [];
  loading = true;
  errorMessage = '';
  searchCode = '';
  foundTicket: TicketDTO | null = null;
  codeError = '';
  codeLoading = false;

  constructor(private ticketService: TicketService) {}

  ngOnInit(): void {
    this.ticketService.getMyTickets().subscribe({
      next: (data) => {
        this.tickets = data;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Greška pri učitavanju karata.';
        this.loading = false;
      }
    });
  }

  searchByCode(): void {
    if (!this.searchCode.trim()) return;
    this.codeLoading = true;
    this.codeError = '';
    this.foundTicket = null;

    this.ticketService.getTicketByCode(this.searchCode.trim()).subscribe({
      next: (ticket) => {
        this.foundTicket = ticket;
        this.codeLoading = false;
      },
      error: () => {
        this.codeError = 'Karta sa tim kodom nije pronađena.';
        this.codeLoading = false;
      }
    });
  }
}
