import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ClubService } from '../../services/club.service';
import { ContractService } from '../../services/playsFor.service';
import { Club } from '../../models/club.model';
import { PlaysFor } from '../../models/player.model';
import { TeamStatisticService } from '../../services/team-statistic.service';
import { AuthService } from '../../../../infrastructure/auth/auth.service';
import { ChartConfiguration, ChartType } from 'chart.js';


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

  searchQuery: string = '';
  filteredRoster: PlaysFor[] = [];

  public showChart: boolean = false;
  public lineChartData: ChartConfiguration['data'] = {
    datasets: [
      {
        data: [], 
        label: 'Golovi',
        backgroundColor: 'rgba(31, 58, 86, 0.8)', 
        borderColor: 'rgba(31, 58, 86, 1)',
        yAxisID: 'y-axis-1',
        type: 'bar',
        borderRadius: 4
      },
      {
        data: [], 
        label: 'Pasovi (%)',
        backgroundColor: 'rgba(220, 53, 69, 0.2)',
        borderColor: 'rgba(220, 53, 69, 1)',
        yAxisID: 'y-axis-2',
        type: 'line',
        tension: 0.4,
        borderWidth: 3,
        pointBackgroundColor: 'rgba(220, 53, 69, 1)'
      }
    ],
    labels: [] 
  };

  public lineChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    scales: {
      'y-axis-1': {
        position: 'left',
        beginAtZero: true,
        title: { display: true, text: 'Broj golova' },
        ticks: { stepSize: 1 }
      },
      'y-axis-2': {
        position: 'right',
        beginAtZero: true,
        max: 100,
        title: { display: true, text: 'Preciznost pasova (%)' },
        grid: { drawOnChartArea: false } 
      }
    },
    plugins: {
      legend: { position: 'top' }
    }
  };
  
  public lineChartType: ChartType = 'line';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private clubService: ClubService,
    private contractService: ContractService,
    private authService: AuthService,
    private teamStatService: TeamStatisticService
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
            this.filteredRoster = [...this.roster];
            this.isLoading = false;
          },
          error: (err) => {
            console.error('Greška pri dobavljanju igrača:', err);
            this.isLoading = false;
          }
        });

        this.loadChartData(clubId);
      },
      error: (err) => {
        console.error('Greška pri dobavljanju podataka o klubu:', err);
        this.isLoading = false;
      }
    });
  }

  private loadChartData(clubId: number): void {
    this.teamStatService.getClubChartData(clubId).subscribe({
      next: (data: any[]) => {
        if (data && data.length > 0) {
          this.lineChartData.labels = data.map(item => new Date(item.matchDate).toLocaleDateString('sr-RS'));
          this.lineChartData.datasets[0].data = data.map(item => item.goals);
          this.lineChartData.datasets[1].data = data.map(item => item.passSuccessRate);
          
          this.lineChartData = { ...this.lineChartData };
          this.showChart = true;
        }
      },
      error: (err) => console.error('Greška pri učitavanju analitike tima:', err)
    });
  }


  onSearchChange(query: string): void {
    const lowerQuery = query.toLowerCase().trim();
    
    if (!lowerQuery) {
      this.filteredRoster = [...this.roster];
      return;
    }
    this.filteredRoster = this.roster.filter(item => 
      (item.playerName || '').toLowerCase().includes(lowerQuery) || 
      (item.playerSurname || '').toLowerCase().includes(lowerQuery)
    );
  }
}