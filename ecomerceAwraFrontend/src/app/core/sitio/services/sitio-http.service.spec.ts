import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { firstValueFrom } from 'rxjs';
import { API_CONFIG } from '../../config/api.config';
import { SitioGateway } from '../ports/sitio.gateway';
import { SitioHttpService } from './sitio-http.service';

describe('SitioHttpService', () => {
  let gateway: SitioGateway;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: API_CONFIG, useValue: { baseUrl: 'http://api.test/api' } },
        { provide: SitioGateway, useClass: SitioHttpService },
      ],
    });

    gateway = TestBed.inject(SitioGateway);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    http.verify();
    TestBed.resetTestingModule();
  });

  it('pide el contenido del sitio', async () => {
    const promesa = firstValueFrom(gateway.obtenerSitio());

    http.expectOne('http://api.test/api/publico/sitio').flush({
      empresa: { nombre: 'AWRA', whatsapp: '573123461277' },
      secciones: [{ clave: 'HERO', activa: true }],
      media: [],
      videos: [],
    });

    const sitio = await promesa;
    expect(sitio.empresa.whatsapp).toBe('573123461277');
    expect(sitio.secciones).toHaveLength(1);
  });

  it('pide las ediciones de la Home', async () => {
    const promesa = firstValueFrom(gateway.obtenerEdiciones());

    http.expectOne('http://api.test/api/publico/ediciones').flush([
      {
        id: 1,
        eyebrow: 'Edicion I',
        titulo: 'AWRA',
        descripcion: 'Descripcion',
        orden: 1,
        productoId: 12,
        productoNombre: 'Buzo',
        precio: 189900,
        imagenFrente: 'https://x/1.webp',
        imagenEspalda: 'https://x/2.webp',
        disponibilidad: 'DISPONIBLE',
        etiqueta: null,
      },
    ]);

    const ediciones = await promesa;
    expect(ediciones).toHaveLength(1);
    expect(ediciones[0].productoId).toBe(12);
  });
});
