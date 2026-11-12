import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, catchError, map, of, shareReplay, throwError } from 'rxjs';
import { API_CONFIG } from '../../config/api.config';
import {
  ConsultaProductos,
  ImagenProducto,
  PaginaProductos,
  Producto,
} from '../models/producto.model';
import { ProductosGateway } from '../ports/productos.gateway';

/** Respuesta del listado publico: el resumen con la foto principal. */
interface ProductoResumenApi {
  id: number;
  nombre: string;
  precio: number;
  categoriaId: number;
  categoriaNombre: string;
  disponibilidad: Producto['disponibilidad'];
  etiqueta: string | null;
  imagenPrincipal: ImagenProducto | null;
}

interface PaginaProductosApi {
  content: ProductoResumenApi[];
  page: {
    size: number;
    number: number;
    totalElements: number;
    totalPages: number;
  };
}

@Injectable()
export class ProductosHttpService extends ProductosGateway {
  private readonly http = inject(HttpClient);
  private readonly config = inject(API_CONFIG);

  listar(consulta?: ConsultaProductos): Observable<PaginaProductos> {
    // Los parametros van con HttpParams y no pegados a la URL: es la forma
    // idiomatica y el motor se encarga de codificarlos.
    let params = new HttpParams()
      .set('page', String(consulta?.page ?? 0))
      .set('size', String(consulta?.size ?? 20));
    if (consulta?.categoriaId !== undefined) {
      params = params.set('categoriaId', String(consulta.categoriaId));
    }

    return this.http
      .get<PaginaProductosApi>(`${this.config.baseUrl}/publico/productos`, { params })
      .pipe(
        // El listado llega con la foto principal suelta, no dentro de un
        // arreglo. Se mueve al arreglo de imagenes, que es como lo pintan
        // los componentes.
        map((pagina) => ({
          content: pagina.content.map((item) => this.aProducto(item)),
          page: pagina.page,
        })),
        shareReplay({ bufferSize: 1, refCount: false }),
      );
  }

  obtenerPorId(id: number): Observable<Producto | null> {
    return this.http.get<Producto>(`${this.config.baseUrl}/publico/productos/${id}`).pipe(
      // Un 404 significa "no existe o no esta publicado". El componente lo
      // pinta como "Producto no encontrado", no como un error.
      catchError((error: HttpErrorResponse) =>
        error.status === 404 ? of(null) : throwError(() => error),
      ),
    );
  }

  private aProducto(resumen: ProductoResumenApi): Producto {
    return {
      id: resumen.id,
      nombre: resumen.nombre,
      descripcion: '',
      precio: resumen.precio,
      categoriaId: resumen.categoriaId,
      categoriaNombre: resumen.categoriaNombre,
      disponibilidad: resumen.disponibilidad,
      etiqueta: resumen.etiqueta ?? '',
      variantes: [],
      imagenes: resumen.imagenPrincipal ? [resumen.imagenPrincipal] : [],
    };
  }
}
