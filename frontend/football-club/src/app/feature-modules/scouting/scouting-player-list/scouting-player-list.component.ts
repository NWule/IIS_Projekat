import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { PlayerService } from '../../match/services/player.service';
import { Player } from '../../match/models/player.model';

@Component({
  selector: 'app-scouting-player-list',
  templateUrl: './scouting-player-list.component.html',
  styleUrls: ['./scouting-player-list.component.css']
})
export class ScoutingPlayerListComponent implements OnInit {
  players: Player[] = [];
  errorMessage: string = '';

  constructor(
    private playerService: PlayerService, 
    private router: Router
  ) {}

  ngOnInit(): void {
    this.playerService.getAllPlayers().subscribe({
      next: (data) => this.players = data,
      error: (err) => {
        console.error(err);
        this.errorMessage = 'Unable to load player rosters at this moment.';
      }
    });
  }

  viewDetails(id: number | undefined): void {
    if (id === undefined) return;
    this.router.navigate(['/players', id]);
  }

  formatPosition(position: string | undefined): string {
    if (!position) return '';
    return position
      .toLowerCase()
      .split('_')
      .map(word => word.charAt(0).toUpperCase() + word.slice(1))
      .join(' ');
  }
}