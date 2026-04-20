import { Injectable, signal, inject } from '@angular/core';
import { AuthService as GeneratedAuthService, LoginRequest, UserProfile, LoginResponse } from '@app/task-client';
import { tap } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private generatedAuth = inject(GeneratedAuthService);
  
  currentUser = signal<UserProfile | null>(null);
  token = signal<string | null>(null);

  login(credentials: LoginRequest) {
    return this.generatedAuth.authLogin(credentials).pipe(
      tap((response: LoginResponse) => {
        this.currentUser.set(response.user);
        this.token.set(response.token);
      })
    );
  }

  logout() {
    this.currentUser.set(null);
    this.token.set(null);
  }

  hasPermission(permission: string): boolean {
    const user = this.currentUser();
    return user ? user.permissions.includes(permission) : false;
  }
}
