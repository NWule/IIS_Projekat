import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../auth.service';
import { ClubService } from '../../../../feature-modules/match/services/club.service';
import { RoleEnum } from '../../model/user.model';
import { Club } from '../../../../feature-modules/match/models/club.model';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css']
})
export class RegistrationComponent implements OnInit {
  registerForm: FormGroup;
  errorMessage: string = '';
  availableClubs: Club[] = [];
  roles = Object.values(RoleEnum)
    .filter(role => role !== RoleEnum.ROLE_ADMIN);

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private clubService: ClubService,
    private router: Router
  ) {
    this.registerForm = this.fb.group({
      name: ['', Validators.required],
      surname: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      username: ['', Validators.required],
      password: ['', [Validators.required, Validators.minLength(6)]],
      role: ['', Validators.required],
      clubId: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.clubService.getAllClubs().subscribe({
      next: (clubs) => {
        this.availableClubs = clubs;
      },
      error: (err) => {
        console.error('Failed to fetch clubs', err);
      }
    });
  }

  onSubmit(): void {
    if (this.registerForm.valid) {
      this.authService.register(this.registerForm.value).subscribe({
        next: () => {
          console.log('Registration successful');
          this.router.navigate(['/']); // Redirect to home or dashboard on success
        },
        error: (err) => {
          console.error('Registration failed', err);
          this.errorMessage = 'Registration failed. Please check your data and try again.';
        }
      });
    }
  }
}
