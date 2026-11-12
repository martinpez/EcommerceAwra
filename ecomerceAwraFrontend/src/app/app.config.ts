import {
  ApplicationConfig,
  provideBrowserGlobalErrorListeners,
  provideZoneChangeDetection,
} from '@angular/core';
import { provideHttpClient, withFetch, withInterceptors } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import { authInterceptor } from './core/auth/auth.interceptor';
import { AuthGateway } from './core/auth/ports/auth.gateway';
import { AuthHttpService } from './core/auth/services/auth-http.service';
import { API_CONFIG } from './core/config/api.config';
import { ProductosGateway } from './core/productos/ports/productos.gateway';
import { ProductosHttpService } from './core/productos/services/productos-http.service';
import { SitioGateway } from './core/sitio/ports/sitio.gateway';
import { SitioHttpService } from './core/sitio/services/sitio-http.service';
import { environment } from '../environments/environment';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    // `withFetch` usa la API de fetch del navegador en vez de XHR: es lo que
    // entiende el servicio worker y evita la advertencia de XHR síncrono.
    provideHttpClient(withFetch(), withInterceptors([authInterceptor])),
    { provide: API_CONFIG, useValue: { baseUrl: environment.apiUrl } },
    { provide: ProductosGateway, useClass: ProductosHttpService },
    { provide: SitioGateway, useClass: SitioHttpService },
    { provide: AuthGateway, useClass: AuthHttpService },
  ],
};
