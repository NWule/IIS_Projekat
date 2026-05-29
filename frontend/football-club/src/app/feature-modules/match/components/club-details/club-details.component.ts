import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ClubService } from '../../services/club.service';
import { ContractService } from '../../services/playsFor.service';
import { Club } from '../../models/club.model';
import { PlaysFor } from '../../models/player.model';
import { AuthService } from '../../../../infrastructure/auth/auth.service';

@Component({
  selector: 'app-club-details',
  templateUrl: './club-details.component.html',
  styleUrls: ['./club-details.component.css']
})
export class ClubDetailsComponent implements OnInit {
  club: Club | null = null;
  roster: PlaysFor[] = []; 
  isLoading: boolean = true;
  
  isAssistantCoach: boolean = false;
  isHeadCoach: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private clubService: ClubService,
    private contractService: ContractService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.checkUserRole(); 
    const clubId = Number(this.route.snapshot.paramMap.get('id'));
    
    if (clubId) {
      this.loadClubData(clubId);
    } else {
      this.isLoading = false;
      console.error('ID kluba nije pronađen u URL ruti.');
    }
  }

  private checkUserRole(): void {
    this.authService.user$.subscribe(user => {
      if (user) {
        this.isAssistantCoach = user.role === 'ROLE_ASSISTANT_COACH';
        this.isHeadCoach = user.role === 'ROLE_HEAD_COACH';
      } else {
        this.isAssistantCoach = false;
        this.isHeadCoach = false;
      }
    });
  }

  goToEditClub(): void {
    if (!this.isAssistantCoach) {
      alert('Samo pomoćni trener ima prava za izmenu podataka o klubu!');
      return;
    }

    if (this.club && this.club.id) {
      this.router.navigate(['/edit-club', this.club.id]);
    }
  }

  goToPlayerDetails(playerId: number | undefined): void {
  if (playerId && (this.isAssistantCoach || this.isHeadCoach)) {
    this.router.navigate(['/player-details', playerId]);
  }
}

  private loadClubData(clubId: number): void {
    this.clubService.getClubById(clubId).subscribe({
      next: (clubData) => {
        this.club = clubData;
        
        this.contractService.getCurrentRoster(clubId).subscribe({
          next: (rosterData) => {
            this.roster = rosterData;
            this.isLoading = false;
          },
          error: (err) => {
            console.error('Greška pri dobavljanju igrača:', err);
            this.isLoading = false;
          }
        });
      },
      error: (err) => {
        console.error('Greška pri dobavljanju podataka o klubu:', err);
        this.isLoading = false;
      }
    });
  }
}