import { Component, OnInit } from '@angular/core';
import { PriceChangeLogService } from '../../services/price-change-log.service';
import { PriceChangeLogDTO } from '../../models/price-change-log.model';

@Component({
  selector: 'app-price-change-log',
  templateUrl: './price-change-log.component.html',
  styleUrls: ['./price-change-log.component.css']
})
export class PriceChangeLogComponent implements OnInit {
  logs: PriceChangeLogDTO[] = [];
  loading = true;
  errorMessage = '';

  constructor(private priceChangeLogService: PriceChangeLogService) {}

  ngOnInit(): void {
    this.priceChangeLogService.getAll().subscribe({
      next: (data) => { this.logs = data; this.loading = false; },
      error: () => { this.errorMessage = 'Greška pri učitavanju istorije.'; this.loading = false; }
    });
  }

  formatDate(dateStr: string): string {
    return new Date(dateStr).toLocaleString('sr-RS');
  }
}
