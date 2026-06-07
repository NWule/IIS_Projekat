import { Component, OnInit } from '@angular/core';
import { ScoutRequestService } from '../../services/scout-request.service';
import { ScoutRequest } from '../../models/scout-request.model';
import { AuthService } from 'src/app/infrastructure/auth/auth.service';

@Component({
  selector: 'app-scouting-requests',
  templateUrl: './scouting-requests.component.html',
  styleUrls: ['./scouting-requests.component.css']
})
export class ScoutingRequestsComponent implements OnInit {
  isScout = false;
  isDirector = false;

  unclaimedRequests: ScoutRequest[] = [];
  myScoutRequests: ScoutRequest[] = [];

  myPendingRequests: ScoutRequest[] = [];
  myProcessedRequests: ScoutRequest[] = [];

  showModal = false;
  requestToEdit: ScoutRequest | null = null;

  constructor(
    private scoutRequestService: ScoutRequestService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.authService.user$.subscribe(user => {
      if (user) {
        const role = user.role.toString();
        if (role === 'ROLE_SCOUT') {
          this.isScout = true;
          this.loadScoutData();
        } else if (role === 'ROLE_SPORTS_DIRECTOR' || role === 'ROLE_ADMIN') {
          this.isDirector = true;
          this.loadDirectorData();
        }
      }
    });
  }

  loadScoutData(): void {
    this.scoutRequestService.getUnclaimedRequests().subscribe(data => {
      this.unclaimedRequests = data;
    });
    this.scoutRequestService.getRequestsByScout().subscribe(data => {
      this.myScoutRequests = data.filter(req => req.status !== 'CANCELLED');
    });
  }

  loadDirectorData(): void {
    this.scoutRequestService.getRequestsByDirector().subscribe(data => {
      this.myPendingRequests = data.filter(req => req.status === 'PENDING');
      this.myProcessedRequests = data.filter(req => req.status !== 'PENDING');
    });
  }

  isExpired(deadlineStr: string): boolean {
    return new Date(deadlineStr).getTime() < new Date().getTime();
  }

  // --- AKCIJE ZA SKAUTA ---
  claimRequest(id: number): void {
    this.scoutRequestService.claimRequest(id).subscribe(() => this.loadScoutData());
  }

  cancelByScout(id: number): void {
    if (confirm('Da li ste sigurni da želite da otkažete ovaj zadatak?')) {
      this.scoutRequestService.cancelRequest(id).subscribe(() => this.loadScoutData());
    }
  }

  // --- AKCIJE ZA DIREKTORA ---
  cancelByDirector(id: number): void {
    if (confirm('Da li ste sigurni da želite da otkažete ovaj skauting zahtev?')) {
      this.scoutRequestService.directorCancelRequest(id).subscribe(() => this.loadDirectorData());
    }
  }

  deleteRequest(id: number): void {
    if (confirm('Trajno brisanje zahteva. Da li ste sigurni?')) {
      this.scoutRequestService.deleteRequest(id).subscribe(() => this.loadDirectorData());
    }
  }

  openEditModal(request: ScoutRequest): void {
    this.requestToEdit = request;
    this.showModal = true;
  }

  onModalClosed(hasChanges: boolean): void {
    this.showModal = false;
    this.requestToEdit = null;
    if (hasChanges) {
      this.loadDirectorData();
    }
  }
}