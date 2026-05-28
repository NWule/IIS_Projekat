import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { GameSearchService } from '../../services/game-search.service';
import { GameSearchResult } from '../../models/game-search.model';

@Component({
  selector: 'app-match-search',
  templateUrl: './match-search.component.html',
  styleUrls: ['./match-search.component.css']
})
export class MatchSearchComponent implements OnInit {
  form: FormGroup;
  results: GameSearchResult[] = [];
  loading = false;
  searched = false;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private gameSearchService: GameSearchService,
    private router: Router
  ) {
    this.form = this.fb.group({
      opponent: [''],
      startDate: [''],
      endDate: [''],
      minPrice: [null],
      maxPrice: [null]
    });
  }

  ngOnInit(): void {
    this.search();
  }

  search(): void {
    this.loading = true;
    this.searched = true;
    this.errorMessage = '';

    const v = this.form.value;
    const params: any = {};
    if (v.opponent) params.opponent = v.opponent;
    if (v.startDate) params.startDate = v.startDate;
    if (v.endDate) params.endDate = v.endDate;
    if (v.minPrice !== null && v.minPrice !== '') params.minPrice = v.minPrice;
    if (v.maxPrice !== null && v.maxPrice !== '') params.maxPrice = v.maxPrice;

    this.gameSearchService.searchGames(params).subscribe({
      next: (data) => {
        this.results = data;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Greška pri učitavanju utakmica.';
        this.loading = false;
      }
    });
  }

  reset(): void {
    this.form.reset();
    this.search();
  }

  goToStadiumMap(gameId: number): void {
    this.router.navigate(['/stadium-map', gameId]);
  }
}
