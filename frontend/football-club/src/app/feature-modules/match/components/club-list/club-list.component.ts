import { Component, OnInit } from '@angular/core';
import { ClubService } from '../../services/club.service'; 
import { Club } from '../../models/club.model'; 
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';
import { Router } from '@angular/router';
import { AuthService } from '../../../../infrastructure/auth/auth.service';
import { environment } from 'src/env/environment';

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

  isAssistantCoach: boolean = false;
  isHeadCoach: boolean = false;
  isCoach: boolean = false; 

  constructor(private clubService: ClubService, private router: Router, private authService: AuthService) {}

  ngOnInit(): void {
    this.checkUserRole(); 
    this.loadMyClubAndAllClubs();
    this.setupSearch();
  }

  private checkUserRole(): void {
    this.authService.user$.subscribe(user => {
      if (user) {
        this.isAssistantCoach = user.role === 'ROLE_ASSISTANT_COACH';
        this.isHeadCoach = user.role === 'ROLE_HEAD_COACH';
        this.isCoach = this.isAssistantCoach || this.isHeadCoach;
      } else {
        this.isAssistantCoach = false;
        this.isHeadCoach = false;
        this.isCoach = false;
      }
    });
  }

  goToClubDetails(clubId: number | undefined): void {
    if (clubId && this.isCoach) {
      this.router.navigate(['/club-details', clubId]);
    }
  }

  goToAddClub(): void {
    if (!this.isAssistantCoach) {
      alert('Samo pomoćni trener ima prava za dodavanje novog kluba!');
      return;
    }
    this.router.navigate(['/add-club']);
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

  getClubImageUrl(imagePath?: string): string | null {
    if (!imagePath) return null;
    
    let baseUrl = environment.apiHost.replace('/api/', '/').replace('api/', '');
    if (!baseUrl.endsWith('/')) {
      baseUrl += '/';
    }
    
    return baseUrl + 'images' + imagePath;
  }
}