import { HttpErrorResponse } from '@angular/common/http';
import { ErrorApi } from './models/admin.model';

/**
 * Convierte un error de la API en un mensaje que se pueda mostrar.
 *
 * El backend responde siempre con la misma forma:
 * `{ status, mensaje, path, errores: [{ campo, mensaje }] }`.
 */
export function mensajeDeError(error: unknown): string {
  if (error instanceof HttpErrorResponse) {
    const cuerpo = error.error as ErrorApi | undefined;

    if (cuerpo?.errores?.length) {
      return cuerpo.errores.map((item) => item.mensaje).join(' ');
    }
    if (cuerpo?.mensaje) {
      return cuerpo.mensaje;
    }
    if (error.status === 0) {
      return 'No hay conexión con el servidor';
    }
    if (error.status === 401) {
      return 'Tu sesión expiró. Vuelve a entrar.';
    }
    if (error.status === 403) {
      return 'No tienes permisos para esta acción.';
    }
    if (error.status === 413) {
      return 'El archivo es demasiado grande.';
    }
  }
  return 'Algo salió mal. Inténtalo de nuevo.';
}

/** Mensajes de validación de un campo concreto. */
export function erroresDeCampo(error: unknown, campo: string): string[] {
  if (error instanceof HttpErrorResponse) {
    const cuerpo = error.error as ErrorApi | undefined;
    return (cuerpo?.errores ?? [])
      .filter((item) => item.campo === campo)
      .map((item) => item.mensaje);
  }
  return [];
}
