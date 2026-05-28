import {Component, OnInit} from '@angular/core';
import {AuthService} from "../../../infrastructure/auth/auth.service";
import {Router} from "@angular/router";
import {RoleEnum} from "../../../infrastructure/auth/model/user.model";

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {
  isLoggedIn: boolean = false;
  userRole: RoleEnum | undefined = undefined;

  constructor(
    private authService: AuthService,
    public router: Router,
  ) {}

  ngOnInit(): void {
    this.authService.checkIfUserExists();
    this.authService.user$.subscribe(user => {
      this.isLoggedIn = !!user;
      this.userRole = user?.role;
    });
  }

  onReportsClick() {
    this.router.navigate(['/reports']);
  }

  onLogoutClick() {
    this.authService.logout();
    this.router.navigate(['/']);
  }

  onLoginClick() {
    this.router.navigate(['/login']);
  }
}
