import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { API_CONFIG } from '../config/api.config';
import { SessionService } from './services/session.service';

/**
 * Añade el token a las peticiones de la API y cierra la sesion si el
 * servidor responde 401.
 *
 * Solo se toca lo que va a la API: las peticiones del propio Angular (por
 * ejemplo, los archivos de traduccion) no llevan el token.
 */
export const authInterceptor: HttpInterceptorFn = (request, next) => {
  const session = inject(SessionService);
  const config = inject(API_CONFIG);
  const token = session.token();

  if (!token || !request.url.startsWith(config.baseUrl)) {
    return next(request);
  }

  return next(request.clone({ setHeaders: { Authorization: `Bearer ${token}` } })).pipe(
    catchError((error: { status?: number }) => {
      // El 401 del propio acceso es normal: usuario o contraseña mal.
      if (error.status === 401 && !request.url.includes('/auth/login')) {
        session.expirar();
      }
      return throwError(() => error);
    }),
  );
};
