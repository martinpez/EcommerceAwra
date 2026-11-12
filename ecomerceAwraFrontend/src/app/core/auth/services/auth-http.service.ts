import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_CONFIG } from '../../config/api.config';
import { Sesion, UsuarioSesion } from '../models/sesion.model';
import { AuthGateway } from '../ports/auth.gateway';

@Injectable()
export class AuthHttpService extends AuthGateway {
  private readonly http = inject(HttpClient);
  private readonly config = inject(API_CONFIG);

  login(username: string, password: string): Observable<Sesion> {
    return this.http.post<Sesion>(`${this.config.baseUrl}/auth/login`, {
      username,
      password,
    });
  }

  me(): Observable<UsuarioSesion> {
    return this.http.get<UsuarioSesion>(`${this.config.baseUrl}/auth/me`);
  }
}
