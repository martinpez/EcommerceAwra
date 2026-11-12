import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { firstValueFrom } from 'rxjs';
import { API_CONFIG } from '../../config/api.config';
import { ProductosGateway } from '../ports/productos.gateway';
import { ProductosHttpService } from './productos-http.service';

describe('ProductosHttpService', () => {
  let gateway: ProductosGateway;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: API_CONFIG, useValue: { baseUrl: 'http://api.test/api' } },
        { provide: ProductosGateway, useClass: ProductosHttpService },
      ],
    });

    gateway = TestBed.inject(ProductosGateway);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    http.verify();
    TestBed.resetTestingModule();
  });

  it('pide el catalogo publico y mueve la foto principal al arreglo de imagenes', async () => {
    const promesa = firstValueFrom(gateway.listar());

    const peticion = http.expectOne((req) => req.url === 'http://api.test/api/publico/productos');
    peticion.flush({
      content: [
        {
          id: 7,
          nombre: 'Buzo Señal Nocturna',
          precio: 189900,
          categoriaId: 1,
          categoriaNombre: 'Buzos',
          disponibilidad: 'DISPONIBLE',
          etiqueta: null,
          imagenPrincipal: {
            url: 'https://res.cloudinary.com/x.webp',
            principal: true,
            orden: 1,
          },
        },
      ],
      page: { size: 20, number: 0, totalElements: 1, totalPages: 1 },
    });

    const pagina = await promesa;
    expect(pagina.content[0].imagenes).toHaveLength(1);
    expect(pagina.content[0].imagenes[0].url).toBe('https://res.cloudinary.com/x.webp');
  });

  it('manda el filtro de categoria cuando se pide', () => {
    gateway.listar({ categoriaId: 3, page: 2, size: 10 }).subscribe();

    const peticion = http.expectOne((req) => req.url === 'http://api.test/api/publico/productos');
    expect(peticion.request.params.get('categoriaId')).toBe('3');
    expect(peticion.request.params.get('page')).toBe('2');
    expect(peticion.request.params.get('size')).toBe('10');
    peticion.flush({
      content: [],
      page: { size: 10, number: 2, totalElements: 0, totalPages: 0 },
    });
  });

  it('devuelve null cuando el producto no existe', async () => {
    const promesa = firstValueFrom(gateway.obtenerPorId(99));

    http
      .expectOne('http://api.test/api/publico/productos/99')
      .flush({ mensaje: 'Producto no encontrado' }, { status: 404, statusText: 'Not Found' });

    await expect(promesa).resolves.toBeNull();
  });

  it('propaga el error si la API falla con otra causa', async () => {
    const promesa = firstValueFrom(gateway.obtenerPorId(1));

    http
      .expectOne('http://api.test/api/publico/productos/1')
      .flush({ mensaje: 'Error' }, { status: 500, statusText: 'Server Error' });

    await expect(promesa).rejects.toMatchObject({ status: 500 });
  });

  it('devuelve el producto con sus imagenes y variantes', async () => {
    const promesa = firstValueFrom(gateway.obtenerPorId(1));

    http.expectOne('http://api.test/api/publico/productos/1').flush({
      id: 1,
      nombre: 'Buzo Señal Nocturna',
      descripcion: 'Descripcion',
      precio: 189900,
      categoriaId: 1,
      categoriaNombre: 'Buzos',
      disponibilidad: 'DISPONIBLE',
      etiqueta: null,
      variantes: [
        { talla: 'S', color: 'Negro', disponible: true },
        { talla: 'L', color: 'Negro', disponible: false },
      ],
      imagenes: [
        { url: 'https://x/1.webp', principal: true, orden: 1 },
        { url: 'https://x/2.webp', principal: false, orden: 2 },
      ],
    });

    const producto = await promesa;
    expect(producto?.nombre).toBe('Buzo Señal Nocturna');
    expect(producto?.variantes).toHaveLength(2);
    expect(producto?.imagenes).toHaveLength(2);
  });
});
