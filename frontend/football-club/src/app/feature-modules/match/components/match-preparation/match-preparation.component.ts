import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import { switchMap } from 'rxjs/operators';

import { ContractService } from '../../services/playsFor.service';
import { GameService } from '../../services/game.service';
import { ClubService } from '../../services/club.service';
import { AppearanceService } from '../../services/appearance.service';

import { PlaysFor } from '../../models/player.model';
import { Game } from '../../models/game.model';
import { Club } from '../../models/club.model';

@Component({
  selector: 'app-match-preparation',
  templateUrl: './match-preparation.component.html',
  styleUrls: ['./match-preparation.component.css']
})
export class MatchPreparationComponent implements OnInit {
  isLoading = true;
  currentStep: 'my-club' | 'opponent-club' = 'my-club';

  gameId!: number;
  game!: Game;
  myClub!: Club;
  opponentClub!: Club;

  formation = '4-3-3';
  
  roster: PlaysFor[] = [];
  startingLineup: PlaysFor[] = [];
  bench: PlaysFor[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private contractService: ContractService,
    private gameService: GameService,
    private clubService: ClubService,
    private appearanceService: AppearanceService
  ) {}

  ngOnInit(): void {
    this.gameId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadInitialData();
  }

  loadInitialData(): void {
    this.isLoading = true;
    
    this.gameService.getGameById(this.gameId).pipe(
      switchMap(game => {
        this.game = game;
        return forkJoin({
          myClub: this.clubService.getMyClub(),
          homeClub: this.clubService.getClubById(game.homeClubId),
          awayClub: this.clubService.getClubById(game.awayClubId)
        });
      }),
      switchMap(({ myClub, homeClub, awayClub }) => {
        this.myClub = myClub;
        this.opponentClub = this.game.homeClubId === myClub.id ? awayClub : homeClub;
        return this.contractService.getCurrentRoster(this.myClub.id!);
      })
    ).subscribe({
      next: (players) => {
        this.roster = players;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Greška pri učitavanju podataka:', err);
        this.isLoading = false;
      }
    });
  }

  addToStartingLineup(player: PlaysFor): void {
    if (this.startingLineup.length < 11) {
      this.roster = this.roster.filter(p => p.id !== player.id);
      this.startingLineup.push(player);
    } else {
      alert('Prva postava već ima 11 igrača!');
    }
  }

  addToBench(player: PlaysFor): void {
    this.roster = this.roster.filter(p => p.id !== player.id);
    this.bench.push(player);
  }

  returnToRoster(player: PlaysFor, fromGroup: 'lineup' | 'bench'): void {
    if (fromGroup === 'lineup') {
      this.startingLineup = this.startingLineup.filter(p => p.id !== player.id);
    } else {
      this.bench = this.bench.filter(p => p.id !== player.id);
    }
    this.roster.push(player);
  }

  saveMatchSquad(): void {
    if (this.currentStep === 'my-club') {
        this.submitMyClubLineup();
    } else {
        this.submitOpponentClubLineup();
    }
  }

  private submitMyClubLineup(): void {
    if (this.startingLineup.length !== 11) {
      alert('Prva postava mora imati tačno 11 igrača!');
      return;
    }

    this.isLoading = true;
    this.saveAppearancesForCurrentStep().subscribe({
      next: () => {
        alert('Sastav vašeg tima je sačuvan!');
        this.currentStep = 'opponent-club'; 
        this.loadOpponentRoster();
      },
      error: (err) => {
        console.error('Greška pri čuvanju:', err);
        this.isLoading = false;
      }
    });
  }

  private loadOpponentRoster(): void {
    this.startingLineup = [];
    this.bench = [];
    this.roster = [];
    
    this.contractService.getCurrentRoster(this.opponentClub.id!).subscribe({
      next: (players) => {
        this.roster = players;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Greška pri učitavanju protivnika:', err);
        this.isLoading = false;
      }
    });
  }

  private submitOpponentClubLineup(): void {
    if (this.startingLineup.length !== 11) {
      alert('Prva postava mora imati tačno 11 igrača!');
      return;
    }

    this.isLoading = true;
    this.saveAppearancesForCurrentStep().subscribe({
      next: () => {
        alert('Sastav protivnika je sačuvan! Priprema je završena.');
        this.router.navigate(['/match-details', this.gameId]);
      },
      error: (err) => {
        console.error('Greška pri čuvanju:', err);
        this.isLoading = false;
      }
    });
  }

  private saveAppearancesForCurrentStep() {
    const allPlayers = [
      ...this.startingLineup.map(p => ({ player: p, matchRole: 'STARTER' })),
      ...this.bench.map(p => ({ player: p, matchRole: 'BENCH' }))
    ];

    const requests = allPlayers.map(item => {
      const payload: any = { 
        gameId: this.gameId,
        playsForId: item.player.id,
        minutesPlayed: 0,
        goals: 0,
        assists: 0,
        passingAccuracy: 0,
        fouls: 0,
        yellowCards: 0,
        redCard: false,
        rating: 0.0,
        matchRole: item.matchRole 
      };
      return this.appearanceService.createAppearance(payload);
    });

    return forkJoin(requests);
  }
}