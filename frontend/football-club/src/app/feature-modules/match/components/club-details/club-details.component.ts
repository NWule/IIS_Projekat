import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ClubService } from '../../services/club.service';
import { ContractService } from '../../services/playsFor.service';
import { Club } from '../../models/club.model';
import { PlaysFor } from '../../models/player.model';

@Component({
  selector: 'app-club-details',
  templateUrl: './club-details.component.html',
  styleUrls: ['./club-details.component.css']
})
export class ClubDetailsComponent implements OnInit {
  club: Club | null = null;
  roster: PlaysFor[] = []; 
  isLoading: boolean = true;

  constructor(
    private route: ActivatedRoute,
    private clubService: ClubService,
    private contractService: ContractService
  ) {}

  ngOnInit(): void {
    const clubId = Number(this.route.snapshot.paramMap.get('id'));
    
    if (clubId) {
      this.loadClubData(clubId);
    } else {
      this.isLoading = false;
      console.error('ID kluba nije pronađen u URL ruti.');
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