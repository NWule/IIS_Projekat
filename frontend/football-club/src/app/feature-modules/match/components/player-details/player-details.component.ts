import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { PlayerService } from '../../services/player.service';
import { ContractService } from '../../services/playsFor.service';
import { Player, PlaysFor } from '../../models/player.model';

@Component({
  selector: 'app-player-details',
  templateUrl: './player-details.component.html',
  styleUrls: ['./player-details.component.css']
})
export class PlayerDetailsComponent implements OnInit {
  player: Player | null = null;
  currentContract: PlaysFor | null = null;
  isLoading: boolean = true;

  constructor(
    private route: ActivatedRoute,
    private playerService: PlayerService,
    private contractService: ContractService
  ) {}

  ngOnInit(): void {
    const playerId = Number(this.route.snapshot.paramMap.get('id'));
    
    if (playerId) {
      this.loadPlayerData(playerId);
    } else {
      this.isLoading = false;
      console.error('ID igrača nije pronađen u URL ruti.');
    }
  }

  private loadPlayerData(playerId: number): void {
    this.playerService.getPlayerById(playerId).subscribe({
      next: (playerData) => {
        this.player = playerData;
        
        this.contractService.getCurrentContract(playerId).subscribe({
          next: (contractData) => {
            this.currentContract = contractData;
            this.isLoading = false;
          },
          error: (err) => {
            console.log('Igrač trenutno nema aktivan ugovor:', err);
            this.currentContract = null;
            this.isLoading = false;
          }
        });
      },
      error: (err) => {
        console.error('Greška pri učitavanju podataka o igraču:', err);
        this.isLoading = false;
      }
    });
  }
}