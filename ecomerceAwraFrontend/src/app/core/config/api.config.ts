import { InjectionToken } from '@angular/core';

export interface ApiConfig {
  /** Base de la API, sin barra final: "http://localhost:8080/api". */
  baseUrl: string;
}

/**
 * Token con la configuracion de la API.
 *
 * Se inyecta en vez de leer el archivo de entorno directamente en los
 * servicios: asi las pruebas pueden apuntar a una URL falsa sin tocar el
 * codigo de la aplicacion.
 */
export const API_CONFIG = new InjectionToken<ApiConfig>('API_CONFIG');
