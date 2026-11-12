import { Observable } from 'rxjs';
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

/** Datos para crear un producto. */
export interface ProductoNuevo {
  nombre: string;
  descripcion: string;
  precio: number;
  categoriaId: number;
  variantes: VarianteNueva[];
}

/** Datos para modificar un producto (las variantes van aparte). */
export interface ProductoEditable {
  nombre: string;
  descripcion: string;
  precio: number;
  categoriaId: number;
}

export interface VarianteNueva {
  talla: string;
  color: string;
  sku: string;
  stockMinimo: number;
}

export interface MovimientoNuevo {
  tipo: 'ENTRADA' | 'VENTA' | 'AJUSTE';
  cantidad: number;
  motivo: string;
}

/**
 * Contrato de la API de administracion.
 *
 * Todos los metodos cuelgan de `/api/admin`, que el backend solo deja pasar
 * a quien tenga el rol `ADMIN`.
 */
export abstract class AdminGateway {
  // Productos
  abstract listarProductos(filtros: FiltroProductos): Observable<Pagina<ProductoResumen>>;
  abstract obtenerProducto(id: number): Observable<Producto>;
  abstract crearProducto(datos: ProductoNuevo): Observable<Producto>;
  abstract actualizarProducto(id: number, datos: ProductoEditable): Observable<Producto>;
  abstract cambiarPublicacion(
    id: number,
    estado: 'BORRADOR' | 'PUBLICADO' | 'OCULTO',
  ): Observable<Producto>;
  abstract eliminarProducto(id: number): Observable<void>;

  // Variantes
  abstract crearVariante(productoId: number, datos: VarianteNueva): Observable<Variante>;
  abstract actualizarVariante(id: number, datos: VarianteNueva): Observable<Variante>;
  abstract eliminarVariante(id: number): Observable<void>;

  // Imágenes de producto
  abstract listarImagenes(productoId: number): Observable<ImagenProducto[]>;
  abstract subirImagen(productoId: number, archivo: File): Observable<ImagenProducto>;
  abstract marcarImagenPrincipal(productoId: number, imagenId: number): Observable<ImagenProducto>;
  abstract cambiarOrdenImagen(
    productoId: number,
    imagenId: number,
    orden: number,
  ): Observable<ImagenProducto>;
  abstract eliminarImagen(productoId: number, imagenId: number): Observable<void>;

  // Inventario
  abstract listarInventario(pagina: number, size: number): Observable<Pagina<InventarioProducto>>;
  abstract registrarMovimiento(varianteId: number, datos: MovimientoNuevo): Observable<Movimiento>;
  abstract listarMovimientos(varianteId: number): Observable<Movimiento[]>;
  abstract listarAlertas(soloNoLeidas: boolean): Observable<AlertaStock[]>;
  abstract marcarAlertaLeida(id: number): Observable<AlertaStock>;

  // Categorías
  abstract listarCategorias(): Observable<Categoria[]>;
  abstract crearCategoria(nombre: string): Observable<Categoria>;
  abstract actualizarCategoria(id: number, nombre: string): Observable<Categoria>;
  abstract eliminarCategoria(id: number): Observable<void>;

  // Contenido del sitio
  abstract obtenerEmpresa(): Observable<Empresa>;
  abstract actualizarEmpresa(datos: Partial<Empresa>): Observable<Empresa>;
  abstract listarSecciones(): Observable<SeccionSitio[]>;
  abstract crearSeccion(datos: SeccionEditable): Observable<SeccionSitio>;
  abstract actualizarSeccion(id: number, datos: SeccionEditable): Observable<SeccionSitio>;
  abstract listarMedios(): Observable<MediaSitio[]>;
  abstract subirMedio(archivo: File, datos: MedioEditable): Observable<MediaSitio>;
  abstract actualizarMedio(id: number, datos: MedioEditable): Observable<MediaSitio>;
  abstract eliminarMedio(id: number): Observable<void>;
  abstract listarVideos(): Observable<VideoSitio[]>;
  abstract subirVideo(archivo: File, datos: VideoEditable): Observable<VideoSitio>;
  abstract actualizarVideo(id: number, datos: VideoEditable): Observable<VideoSitio>;
  abstract eliminarVideo(id: number): Observable<void>;
  abstract listarEdiciones(): Observable<Edicion[]>;
  abstract crearEdicion(datos: EdicionEditable): Observable<Edicion>;
  abstract actualizarEdicion(id: number, datos: EdicionEditable): Observable<Edicion>;
}

export interface FiltroProductos {
  estado?: 'BORRADOR' | 'PUBLICADO' | 'OCULTO' | '';
  categoriaId?: number | null;
  q?: string;
  page?: number;
  size?: number;
}

export interface SeccionEditable {
  clave: string;
  eyebrow: string;
  titulo: string;
  cuerpo: string;
  orden: number;
  activa: boolean;
}

export interface MedioEditable {
  seccionClave: string;
  tipo: 'LOGO' | 'IMAGEN';
  alt: string;
  orden: number;
  activa: boolean;
}

export interface VideoEditable {
  seccionClave: string;
  titulo: string;
  etiqueta: string;
  orden: number;
  activa: boolean;
}

export interface EdicionEditable {
  productoId: number;
  eyebrow: string;
  titulo: string;
  descripcion: string;
  orden: number;
  activa: boolean;
}
