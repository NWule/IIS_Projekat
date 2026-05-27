import { Component, OnInit } from '@angular/core';
import { ClubService } from '../../services/club.service'; 
import { Club } from '../../models/club.model'; 
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';

@Component({
  selector: 'app-club-list',
  templateUrl: './club-list.component.html',
  styleUrls: ['./club-list.component.css']
})
export class ClubListComponent implements OnInit {
  clubs: Club[] = [];
  myClubId: number | null = null;
  searchQuery: string = '';
  private searchSubject = new Subject<string>();

  constructor(private clubService: ClubService) {}

  ngOnInit(): void {
    this.loadMyClubAndAllClubs();
    this.setupSearch();
  }

  loadMyClubAndAllClubs(): void {
    this.clubService.getMyClub().subscribe({
      next: (myClub) => {
        this.myClubId = myClub.id;
        this.loadAllClubs();
      },
      error: (err) => {
        this.loadAllClubs();
      }
    });
  }

  loadAllClubs(): void {
    this.clubService.getAllClubs().subscribe({
      next: (data) => {
        this.clubs = this.sortClubsWithMyClubFirst(data);
      },
      error: (err) => console.error('Greška pri učitavanju klubova:', err)
    });
  }

  private setupSearch(): void {
    this.searchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(query => {
        if (!query.trim()) {
          return this.clubService.getAllClubs();
        }
        return this.clubService.searchClubsByName(query);
      })
    ).subscribe({
      next: (data) => {
        this.clubs = this.sortClubsWithMyClubFirst(data);
      },
      error: (err) => console.error('Greška tokom pretrage:', err)
    });
  }

  onSearchChange(value: string): void {
    this.searchSubject.next(value);
  }

  private sortClubsWithMyClubFirst(clubList: Club[]): Club[] {
    if (!this.myClubId) return clubList;
    
    return [...clubList].sort((a, b) => {
      if (a.id === this.myClubId) return -1;
      if (b.id === this.myClubId) return 1;
      return 0;
    });
  }
}