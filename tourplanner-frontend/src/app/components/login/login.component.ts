import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterLink, CommonModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  usernameOrEmail = '';
  password = '';
  errorMessage = '';

  constructor(private authService: AuthService, private router: Router) {}

  onLogin(): void {
    if (!this.usernameOrEmail || !this.password) {
      this.errorMessage = 'Please fill in all fields.';
      return;
    }
    this.authService.login({ usernameOrEmail: this.usernameOrEmail, password: this.password })
      .subscribe({
        next: () => this.router.navigate(['/dashboard']),
        error: () => this.errorMessage = 'Invalid credentials. Please try again.'
      });
  }
}
