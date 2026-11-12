import { Injectable, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { UsuarioSesion } from '../models/sesion.model';
import { AuthGateway } from '../ports/auth.gateway';

/** Clave del token en el almacenamiento de la sesion. */
const CLAVE_TOKEN = 'awra.admin.token';

/**
 * Sesion del administrador.
 *
 * El token vive en `sessionStorage`, no en `localStorage`: se borra al
 * cerrar la pestaña. Es la diferencia entre "la sesión dura hasta que
 * cierras el navegador" y "el token sobrevive semanas en un equipo
 * compartido".
 */
@Injectable({ providedIn: 'root' })
export class SessionService {
  private readonly auth = inject(AuthGateway);
  private readonly router = inject(Router);

  private readonly tokenInterno = signal<string | null>(sessionStorage.getItem(CLAVE_TOKEN));
  private readonly usuarioInterno = signal<UsuarioSesion | null>(null);

  readonly token = this.tokenInterno.asReadonly();
  readonly usuario = this.usuarioInterno.asReadonly();
  readonly autenticado = computed(() => !!this.tokenInterno());

  async login(username: string, password: string): Promise<UsuarioSesion> {
    const sesion = await firstValueFrom(this.auth.login(username, password));
    this.tokenInterno.set(sesion.token);
    sessionStorage.setItem(CLAVE_TOKEN, sesion.token);

    // Se pide /auth/me para confirmar el token y saber quien es. Si el
    // token no valiera, el interceptor lo detecta y cierra la sesion.
    const usuario = await firstValueFrom(this.auth.me());
    this.usuarioInterno.set(usuario);
    return usuario;
  }

  /** Cierra la sesion y borra el token. */
  logout(): void {
    this.tokenInterno.set(null);
    this.usuarioInterno.set(null);
    sessionStorage.removeItem(CLAVE_TOKEN);
  }

  /** Redirige al acceso. Se usa cuando el token caduca. */
  expirar(): void {
    this.logout();
    void this.router.navigate(['/admin/login']);
  }

  /**
   * Recupera el usuario al recargar la pagina.
   *
   * El token sobrevive en `sessionStorage`, pero el nombre y los roles no:
   * hay que volver a pedirlos.
   */
  async restaurar(): Promise<boolean> {
    if (!this.tokenInterno()) {
      return false;
    }
    try {
      const usuario = await firstValueFrom(this.auth.me());
      this.usuarioInterno.set(usuario);
      return true;
    } catch {
      this.logout();
      return false;
    }
  }
}
