import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { forkJoin, Subscription, interval } from 'rxjs';
import { switchMap } from 'rxjs/operators';

import { GameService } from '../../services/game.service';
import { ClubService } from '../../services/club.service';
import { AppearanceService } from '../../services/appearance.service';
import { MatchEventService } from '../../services/match-event.service';

import { Game } from '../../models/game.model';
import { Club } from '../../models/club.model';
import { Appearance } from '../../models/appearance.model';
import { MatchEventRequest } from '../../models/match-event.model';

@Component({
  selector: 'app-live-tracking',
  templateUrl: './live-tracking.component.html',
  styleUrls: ['./live-tracking.component.css']
})
export class LiveTrackingComponent implements OnInit, OnDestroy {
  isLoading = true;
  gameId!: number;
  game!: Game;
  homeClub!: Club;
  awayClub!: Club;

  homeStartingXI: Appearance[] = [];
  homeBench: Appearance[] = [];
  
  awayStartingXI: Appearance[] = [];
  awayBench: Appearance[] = [];

  selectedPlayer: Appearance | null = null;
  isPassMode = false;
  isSubMode = false;

  matchSeconds = 0;
  displayTime = '00:00';
  currentMinute = 1;
  isTimerRunning = false;
  private timerSubscription?: Subscription;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private gameService: GameService,
    private clubService: ClubService,
    private appearanceService: AppearanceService,
    private matchEventService: MatchEventService
  ) {}

  ngOnInit(): void {
    this.gameId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadLiveMatchData();
  }

  ngOnDestroy(): void {
    this.stopTimer();
  }

  loadLiveMatchData(): void {
    this.isLoading = true;
    this.gameService.getGameById(this.gameId).pipe(
      switchMap(game => {
        this.game = game;
        return forkJoin({
          homeClub: this.clubService.getClubById(game.homeClubId),
          awayClub: this.clubService.getClubById(game.awayClubId),
          appearances: this.appearanceService.getAppearancesByGame(this.gameId)
        });
      })
    ).subscribe({
      next: ({ homeClub, awayClub, appearances }) => {
        this.homeClub = homeClub;
        this.awayClub = awayClub;

        const homeApps = appearances.filter(p => p.clubId === this.game.homeClubId);
        const awayApps = appearances.filter(p => p.clubId === this.game.awayClubId);

        this.homeStartingXI = homeApps.filter(p => p.matchRole === 'STARTING_XI');
        this.homeBench = homeApps.filter(p => p.matchRole === 'BENCH');
        
        this.awayStartingXI = awayApps.filter(p => p.matchRole === 'STARTING_XI');
        this.awayBench = awayApps.filter(p => p.matchRole === 'BENCH');
        
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Greška pri učitavanju live meča:', err);
        this.isLoading = false;
      }
    });
  }

  toggleTimer(): void {
    if (this.isTimerRunning) {
      this.stopTimer();
    } else {
      this.startTimer();
    }
  }

  private startTimer(): void {
    this.isTimerRunning = true;
    this.timerSubscription = interval(1000).subscribe(() => {
      this.matchSeconds++;
      this.updateDisplayTime();
    });
  }

  private stopTimer(): void {
    this.isTimerRunning = false;
    this.timerSubscription?.unsubscribe();
  }

  private updateDisplayTime(): void {
    const minutes = Math.floor(this.matchSeconds / 60);
    const seconds = this.matchSeconds % 60;
    this.displayTime = `${this.padZero(minutes)}:${this.padZero(seconds)}`;
    this.currentMinute = minutes + 1;
  }

  private padZero(num: number): string {
    return num < 10 ? '0' + num : num.toString();
  }

  onMinuteManualChange(): void {
    if (this.currentMinute > 0) {
      this.matchSeconds = (this.currentMinute - 1) * 60;
      this.updateDisplayTime();
    }
  }

  selectPlayer(player: Appearance): void {
    if (this.isPassMode && this.selectedPlayer) {
      if (this.selectedPlayer.id === player.id) {
        alert('Igrač ne može dodati pas samom sebi!');
        return;
      }
      this.handlePassExecution(this.selectedPlayer, player);
      return;
    }

    if (this.isSubMode && this.selectedPlayer) {
      if (this.selectedPlayer.clubId !== player.clubId) {
        alert('Izmena mora biti unutar istog tima!');
        return;
      }
      if (player.matchRole !== 'BENCH') {
        alert('Igrač koji ulazi mora biti na klupi!');
        return;
      }
      this.handleSubstitutionExecution(this.selectedPlayer, player);
      return;
    }

    this.selectedPlayer = player;
  }

  activateSubMode(): void {
    if (!this.selectedPlayer) {
      alert('Molimo vas da prvo izaberete igrača iz prve postave koji izlazi iz igre.');
      return;
    }
    if (this.selectedPlayer.matchRole !== 'STARTING_XI') {
      alert('Igrač koji izlazi mora biti u prvoj postavi.');
      return;
    }
    this.isSubMode = true;
    this.isPassMode = false; 
  }

  private handleSubstitutionExecution(playerOut: Appearance, playerIn: Appearance): void {
    playerOut.matchRole = 'BENCH';
    playerIn.matchRole = 'STARTING_XI';

    if (playerOut.clubId === this.game.homeClubId) {
      this.homeStartingXI = this.homeStartingXI.filter(p => p.id !== playerOut.id);
      this.homeStartingXI.push(playerIn);
      this.homeBench = this.homeBench.filter(p => p.id !== playerIn.id);
      this.homeBench.push(playerOut);
    } else {
      this.awayStartingXI = this.awayStartingXI.filter(p => p.id !== playerOut.id);
      this.awayStartingXI.push(playerIn);
      this.awayBench = this.awayBench.filter(p => p.id !== playerIn.id);
      this.awayBench.push(playerOut);
    }

    forkJoin([
      this.appearanceService.updateAppearance(playerOut.id!, playerOut),
      this.appearanceService.updateAppearance(playerIn.id!, playerIn)
    ]).subscribe({
      next: () => {
        alert('Izmena je uspešno evidentirana i upisana u bazu!');
      },
      error: (err) => {
        console.error('Greška pri čuvanju izmene:', err);
        alert('Greška na serveru prilikom čuvanja izmene. Osvežite stranicu.');
      }
    });

    this.isSubMode = false;
    this.selectedPlayer = null;
  }

  activatePassMode(): void {
    if (!this.selectedPlayer) {
      alert('Molimo vas da prvo izaberete igrača koji dodaje loptu.');
      return;
    }
    this.isPassMode = true;
  }

  private handlePassExecution(player1: Appearance, player2: Appearance): void {
    const isSuccessful = player1.clubId === player2.clubId;
    const eventType = isSuccessful ? 'PASS_SUCCESS' : 'PASS_FAIL';

    this.sendEventToBackend(player1, eventType);

    this.isPassMode = false;
    this.selectedPlayer = null;
  }

  triggerStandardEvent(eventType: string): void {
    if (!this.selectedPlayer) {
      alert('Molimo vas da selektujete igrača pre klika na aktivnost.');
      return;
    }

    this.sendEventToBackend(this.isPassMode ? this.selectedPlayer : this.selectedPlayer!, eventType);
    this.selectedPlayer = null;
  }

  private sendEventToBackend(appearance: Appearance, eventType: string): void {
    const matchEventDto: MatchEventRequest = {
      clubId: appearance.clubId!,
      playsForId: appearance.playsForId!,
      eventType: eventType,
      matchMinute: this.currentMinute
    };

    this.matchEventService.recordLiveEvent(this.gameId, matchEventDto).subscribe({
      next: () => {
      },
      error: (err) => {
        console.error('Greška prilikom slanja događaja uživo:', err);
        alert('Došlo je do greške na serveru pri upisu aktivnosti.');
      }
    });
  }

  finishMatch(): void {
    if (confirm('Da li ste sigurni da želite da završite praćenje ove utakmice?')) {
      this.stopTimer();
      this.router.navigate(['/match-details', this.gameId]);
    }
  }
}