import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { PlayerService } from '../../../match/services/player.service';
import { Player } from '../../../match/models/player.model';
import { map } from 'rxjs/internal/operators/map';
import { catchError, forkJoin, of, switchMap } from 'rxjs';
import { ReportService } from '../../services/report.service';
import { PlayerWithReport } from '../../models/report.model';

@Component({
  selector: 'app-scouting-player-list',
  templateUrl: './scouting-player-list.component.html',
  styleUrls: ['./scouting-player-list.component.css']
})
export class ScoutingPlayerListComponent implements OnInit {
  players: PlayerWithReport[] = [];
  isLoading: boolean = true;
  errorMessage: string = '';

  constructor(
    private playerService: PlayerService, 
    private reportService: ReportService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.playerService.getAllPlayers().pipe(
      switchMap((playerList) => {
        if (!playerList || playerList.length === 0) {
          return of([]);
        }
        
        const reportingRequests = playerList.map(player => {
          if (player.id === undefined) {
            return of({ ...player, latestReport: null });
          }
          return this.reportService.getLatestReportForPlayer(player.id).pipe(
            map(report => ({ ...player, latestReport: report })),
            catchError(() => of({ ...player, latestReport: null }))
          )
        });
        
        return forkJoin(reportingRequests);
      })
    ).subscribe({
      next: (combinedData) => {
        this.players = combinedData;
        this.isLoading = false;
      },
      error: (err) => {
        console.error(err);
        this.errorMessage = 'System error occurred while assembling the matrix profiles.';
        this.isLoading = false;
      }
    });
  }

  viewDetails(id: number | undefined): void {
    if (id === undefined) return;
    this.router.navigate(['/view-player', id]);
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