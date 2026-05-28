import { Component, OnInit } from '@angular/core';
import { AuthService } from './infrastructure/auth/auth.service';
import { User } from './infrastructure/auth/model/user.model';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  currentUser: User | null = null;

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.authService.checkIfUserExists();
    this.authService.user$.subscribe(user => {
      this.currentUser = user;
    });
  }

  logout(): void {
    this.authService.logout();
  }
}
