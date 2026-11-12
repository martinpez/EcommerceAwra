import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, shareReplay } from 'rxjs';
import { API_CONFIG } from '../../config/api.config';
import { EdicionPublica, Sitio } from '../models/sitio.model';
import { SitioGateway } from '../ports/sitio.gateway';

@Injectable()
export class SitioHttpService extends SitioGateway {
  private readonly http = inject(HttpClient);
  private readonly config = inject(API_CONFIG);

  /**
   * `shareReplay` guarda la ultima respuesta en memoria: la Home y la ficha
   * piden el mismo contenido y no tiene por que viajar dos veces.
   */
  private readonly sitio$ = this.http
    .get<Sitio>(`${this.config.baseUrl}/publico/sitio`)
    .pipe(shareReplay({ bufferSize: 1, refCount: false }));

  private readonly ediciones$ = this.http
    .get<EdicionPublica[]>(`${this.config.baseUrl}/publico/ediciones`)
    .pipe(shareReplay({ bufferSize: 1, refCount: false }));

  obtenerSitio(): Observable<Sitio> {
    return this.sitio$;
  }

  obtenerEdiciones(): Observable<EdicionPublica[]> {
    return this.ediciones$;
  }
}
