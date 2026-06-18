import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { GameService } from '../../services/game.service';
import { ClubService } from '../../services/club.service';
import { Club } from '../../models/club.model';

@Component({
  selector: 'app-live-match-finder',
  templateUrl: './live-match-finder.component.html',
  styleUrls: ['./live-match-finder.component.css']
})
export class LiveMatchFinderComponent implements OnInit {
  myClub!: Club;
  isLoadingClub = true;
  isSearchingMatch = false;
  noMatchFound = false;

  constructor(
    private gameService: GameService,
    private clubService: ClubService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.clubService.getMyClub().subscribe({
      next: (club) => {
        this.myClub = club;
        this.isLoadingClub = false;
      },
      error: (err) => {
        console.error('Greška pri učitavanju kluba:', err);
        this.isLoadingClub = false;
      }
    });
  }

  findLiveMatch(): void {
    this.isSearchingMatch = true;
    this.noMatchFound = false;

    this.gameService.getLiveGames().subscribe({
      next: (liveGames) => {
        const myLiveGame = liveGames.find(
          game => game.homeClubId === this.myClub.id || game.awayClubId === this.myClub.id
        );

        this.isSearchingMatch = false;

        if (myLiveGame) {
          this.router.navigate(['/live-match-coach', myLiveGame.id]);
        } else {
          this.noMatchFound = true;
        }
      },
      error: (err) => {
        console.error('Greška pri traženju live utakmice:', err);
        this.isSearchingMatch = false;
        alert('Došlo je do greške prilikom pretrage utakmica.');
      }
    });
  }
}