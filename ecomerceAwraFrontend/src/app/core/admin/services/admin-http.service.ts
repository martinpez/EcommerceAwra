import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_CONFIG } from '../../config/api.config';
import {
  AlertaStock,
  Categoria,
  Edicion,
  Empresa,
  ImagenProducto,
  InventarioProducto,
  MediaSitio,
  Movimiento,
  Pagina,
  Producto,
  ProductoResumen,
  SeccionSitio,
  Variante,
  VideoSitio,
} from '../models/admin.model';
import {
  AdminGateway,
  EdicionEditable,
  FiltroProductos,
  MedioEditable,
  MovimientoNuevo,
  ProductoEditable,
  ProductoNuevo,
  SeccionEditable,
  VarianteNueva,
  VideoEditable,
} from '../ports/admin.gateway';

@Injectable()
export class AdminHttpService extends AdminGateway {
  private readonly http = inject(HttpClient);
  private readonly config = inject(API_CONFIG);

  private url(path: string): string {
    return `${this.config.baseUrl}/admin${path}`;
  }

  // ---------------------------------------------------------------- productos

  listarProductos(filtros: FiltroProductos): Observable<Pagina<ProductoResumen>> {
    let params = new HttpParams()
      .set('page', String(filtros.page ?? 0))
      .set('size', String(filtros.size ?? 20));
    if (filtros.estado) {
      params = params.set('estado', filtros.estado);
    }
    if (filtros.categoriaId !== undefined && filtros.categoriaId !== null) {
      params = params.set('categoriaId', String(filtros.categoriaId));
    }
    if (filtros.q) {
      params = params.set('q', filtros.q);
    }
    return this.http.get<Pagina<ProductoResumen>>(this.url('/productos'), { params });
  }

  obtenerProducto(id: number): Observable<Producto> {
    return this.http.get<Producto>(this.url(`/productos/${id}`));
  }

  crearProducto(datos: ProductoNuevo): Observable<Producto> {
    return this.http.post<Producto>(this.url('/productos'), datos);
  }

  actualizarProducto(id: number, datos: ProductoEditable): Observable<Producto> {
    return this.http.put<Producto>(this.url(`/productos/${id}`), datos);
  }

  cambiarPublicacion(
    id: number,
    estado: 'BORRADOR' | 'PUBLICADO' | 'OCULTO',
  ): Observable<Producto> {
    return this.http.patch<Producto>(this.url(`/productos/${id}/publicacion`), { estado });
  }

  eliminarProducto(id: number): Observable<void> {
    return this.http.delete<void>(this.url(`/productos/${id}`));
  }

  // --------------------------------------------------------------- variantes

  crearVariante(productoId: number, datos: VarianteNueva): Observable<Variante> {
    return this.http.post<Variante>(this.url(`/productos/${productoId}/variantes`), datos);
  }

  actualizarVariante(id: number, datos: VarianteNueva): Observable<Variante> {
    return this.http.put<Variante>(this.url(`/variantes/${id}`), datos);
  }

  eliminarVariante(id: number): Observable<void> {
    return this.http.delete<void>(this.url(`/variantes/${id}`));
  }

  // ---------------------------------------------------------------- imágenes

  listarImagenes(productoId: number): Observable<ImagenProducto[]> {
    return this.http.get<ImagenProducto[]>(this.url(`/productos/${productoId}/imagenes`));
  }

  subirImagen(productoId: number, archivo: File): Observable<ImagenProducto> {
    const formulario = new FormData();
    formulario.append('archivo', archivo, archivo.name);
    return this.http.post<ImagenProducto>(
      this.url(`/productos/${productoId}/imagenes`),
      formulario,
    );
  }

  marcarImagenPrincipal(productoId: number, imagenId: number): Observable<ImagenProducto> {
    return this.http.patch<ImagenProducto>(
      this.url(`/productos/${productoId}/imagenes/${imagenId}/principal`),
      {},
    );
  }

  cambiarOrdenImagen(
    productoId: number,
    imagenId: number,
    orden: number,
  ): Observable<ImagenProducto> {
    return this.http.patch<ImagenProducto>(
      this.url(`/productos/${productoId}/imagenes/${imagenId}/orden`),
      { orden },
    );
  }

  eliminarImagen(productoId: number, imagenId: number): Observable<void> {
    return this.http.delete<void>(this.url(`/productos/${productoId}/imagenes/${imagenId}`));
  }

  // -------------------------------------------------------------- inventario

  listarInventario(pagina: number, size: number): Observable<Pagina<InventarioProducto>> {
    const params = new HttpParams().set('page', String(pagina)).set('size', String(size));
    return this.http.get<Pagina<InventarioProducto>>(this.url('/inventario'), { params });
  }

  registrarMovimiento(varianteId: number, datos: MovimientoNuevo): Observable<Movimiento> {
    return this.http.post<Movimiento>(this.url(`/variantes/${varianteId}/movimientos`), datos);
  }

  listarMovimientos(varianteId: number): Observable<Movimiento[]> {
    return this.http.get<Movimiento[]>(this.url(`/variantes/${varianteId}/movimientos`));
  }

  listarAlertas(soloNoLeidas: boolean): Observable<AlertaStock[]> {
    // Sin el parámetro el backend devuelve todas. Con `leida=false`
    // devuelve solo las que están sin leer.
    const params = soloNoLeidas ? new HttpParams().set('leida', 'false') : new HttpParams();
    return this.http.get<AlertaStock[]>(this.url('/alertas'), { params });
  }

  marcarAlertaLeida(id: number): Observable<AlertaStock> {
    return this.http.patch<AlertaStock>(this.url(`/alertas/${id}/leida`), {});
  }

  // -------------------------------------------------------------- categorías

  listarCategorias(): Observable<Categoria[]> {
    return this.http.get<Categoria[]>(this.url('/categorias'));
  }

  crearCategoria(nombre: string): Observable<Categoria> {
    return this.http.post<Categoria>(this.url('/categorias'), { nombre });
  }

  actualizarCategoria(id: number, nombre: string): Observable<Categoria> {
    return this.http.put<Categoria>(this.url(`/categorias/${id}`), { nombre });
  }

  eliminarCategoria(id: number): Observable<void> {
    return this.http.delete<void>(this.url(`/categorias/${id}`));
  }

  // -------------------------------------------------------- contenido del sitio

  obtenerEmpresa(): Observable<Empresa> {
    return this.http.get<Empresa>(this.url('/sitio/empresa'));
  }

  actualizarEmpresa(datos: Partial<Empresa>): Observable<Empresa> {
    return this.http.put<Empresa>(this.url('/sitio/empresa'), datos);
  }

  listarSecciones(): Observable<SeccionSitio[]> {
    return this.http.get<SeccionSitio[]>(this.url('/sitio/secciones'));
  }

  crearSeccion(datos: SeccionEditable): Observable<SeccionSitio> {
    return this.http.post<SeccionSitio>(this.url('/sitio/secciones'), datos);
  }

  actualizarSeccion(id: number, datos: SeccionEditable): Observable<SeccionSitio> {
    return this.http.put<SeccionSitio>(this.url(`/sitio/secciones/${id}`), datos);
  }

  listarMedios(): Observable<MediaSitio[]> {
    return this.http.get<MediaSitio[]>(this.url('/sitio/media'));
  }

  subirMedio(archivo: File, datos: MedioEditable): Observable<MediaSitio> {
    return this.http.post<MediaSitio>(this.url('/sitio/media'), this.multipart(archivo, datos));
  }

  actualizarMedio(id: number, datos: MedioEditable): Observable<MediaSitio> {
    return this.http.put<MediaSitio>(this.url(`/sitio/media/${id}`), datos);
  }

  eliminarMedio(id: number): Observable<void> {
    return this.http.delete<void>(this.url(`/sitio/media/${id}`));
  }

  listarVideos(): Observable<VideoSitio[]> {
    return this.http.get<VideoSitio[]>(this.url('/sitio/videos'));
  }

  subirVideo(archivo: File, datos: VideoEditable): Observable<VideoSitio> {
    return this.http.post<VideoSitio>(this.url('/sitio/videos'), this.multipart(archivo, datos));
  }

  actualizarVideo(id: number, datos: VideoEditable): Observable<VideoSitio> {
    return this.http.put<VideoSitio>(this.url(`/sitio/videos/${id}`), datos);
  }

  eliminarVideo(id: number): Observable<void> {
    return this.http.delete<void>(this.url(`/sitio/videos/${id}`));
  }

  listarEdiciones(): Observable<Edicion[]> {
    return this.http.get<Edicion[]>(this.url('/sitio/ediciones'));
  }

  crearEdicion(datos: EdicionEditable): Observable<Edicion> {
    return this.http.post<Edicion>(this.url('/sitio/ediciones'), datos);
  }

  actualizarEdicion(id: number, datos: EdicionEditable): Observable<Edicion> {
    return this.http.put<Edicion>(this.url(`/sitio/ediciones/${id}`), datos);
  }

  /**
   * Las subidas de contenido del sitio mandan dos partes: el archivo y un
   * JSON con los datos. El JSON va como `Blob` con su tipo MIME para que
   * Spring lo entienda.
   */
  private multipart(archivo: File, datos: unknown): FormData {
    const formulario = new FormData();
    formulario.append('archivo', archivo, archivo.name);
    formulario.append('datos', new Blob([JSON.stringify(datos)], { type: 'application/json' }));
    return formulario;
  }
}
