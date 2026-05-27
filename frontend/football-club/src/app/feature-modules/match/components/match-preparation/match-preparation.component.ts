import { Component, OnInit } from '@angular/core';
import { ContractService } from '../../services/playsFor.service';
import { PlaysFor } from '../../models/player.model';

@Component({
  selector: 'app-match-preparation',
  templateUrl: './match-preparation.component.html',
  styleUrls: ['./match-preparation.component.css']
})
export class MatchPreparationComponent implements OnInit {
  isLoading = true;
  formation = '4-3-3';
  
  roster: PlaysFor[] = [];
  startingLineup: PlaysFor[] = [];
  bench: PlaysFor[] = [];

  currentClubId: number = 1;

  constructor(private contractService: ContractService) {}

  ngOnInit(): void {
    this.loadRoster();
  }

  loadRoster(): void {
    this.contractService.getClubHistory(this.currentClubId).subscribe({
      next: (players) => {
        this.roster = players;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading roster:', err);
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
    if (this.startingLineup.length !== 11) {
      alert('Prva postava mora imati tačno 11 igrača pre čuvanja!');
      return;
    }
    
    const squadData = {
      startingLineup: this.startingLineup,
      bench: this.bench,
      formation: this.formation
    };
    
    console.log('Squad saved:', squadData);
    alert('Sastav je uspešno sačuvan!');
  }
}