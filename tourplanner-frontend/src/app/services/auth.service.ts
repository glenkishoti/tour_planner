import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { tap } from 'rxjs/operators';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private baseUrl = '/api/auth';

  constructor(private http: HttpClient, private router: Router) {}

  register(data: { username: string; email: string; password: string }): Observable<any> {
    return this.http.post(`${this.baseUrl}/register`, data);
  }

  login(data: { usernameOrEmail: string; password: string }): Observable<any> {
    return this.http.post<{ token: string; username: string; email: string }>(
      `${this.baseUrl}/login`, data
    ).pipe(
      tap(response => {
        localStorage.setItem('token', response.token);
        localStorage.setItem('username', response.username);
      })
    );
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    this.router.navigate(['/login']);
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  getUsername(): string | null {
    return localStorage.getItem('username');
  }
}
