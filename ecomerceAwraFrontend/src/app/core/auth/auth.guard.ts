import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { SessionService } from './services/session.service';

/**
 * Protege las pantallas del panel.
 *
 * Sin token, redirige al acceso. Ojo: esto es experiencia de usuario, no
 * seguridad. La seguridad real la aplica el backend, que rechaza con 401
 * cualquier peticion sin `ROLE_ADMIN`.
 */
export const adminGuard: CanActivateFn = async () => {
  const session = inject(SessionService);
  const router = inject(Router);

  if (!session.autenticado()) {
    return router.createUrlTree(['/admin/login']);
  }

  // Puede que el token exista pero ya haya caducado: se comprueba con la API.
  if (!session.usuario()) {
    const ok = await session.restaurar();
    if (!ok) {
      return router.createUrlTree(['/admin/login']);
    }
  }

  return true;
};

/** Al revés: si ya hay sesion, no tiene sentido mostrar el acceso. */
export const guestGuard: CanActivateFn = () => {
  const session = inject(SessionService);
  const router = inject(Router);
  return session.autenticado() ? router.createUrlTree(['/admin/productos']) : true;
};
