/**
 * Modelos de la API de administracion.
 *
 * Reflejan los DTOs del backend. Los nombres de los campos son los del
 * contrato (ingles en el backend, español en los mensajes de error).
 */

export type EstadoPublicacion = 'BORRADOR' | 'PUBLICADO' | 'OCULTO' | 'ELIMINADO';

export type Disponibilidad = 'DISPONIBLE' | 'SIN_STOCK';

export type TipoMovimiento = 'ENTRADA' | 'VENTA' | 'AJUSTE';

export type TipoAlerta = 'STOCK_BAJO' | 'AGOTADO';

export type TipoMedia = 'LOGO' | 'IMAGEN';

export interface Variante {
  id: number;
  productoId: number;
  talla: string;
  color: string;
  sku: string;
  stock: number;
  stockMinimo: number;
}

export interface Producto {
  id: number;
  nombre: string;
  descripcion: string | null;
  precio: number;
  categoriaId: number;
  categoriaNombre: string;
  estadoPublicacion: EstadoPublicacion;
  fechaCreacion: string;
  fechaActualizacion: string;
  variantes: Variante[];
}

export interface ImagenProducto {
  id: number;
  url: string;
  principal: boolean;
  orden: number;
}

export interface ProductoResumen {
  id: number;
  nombre: string;
  precio: number;
  categoriaId: number;
  categoriaNombre: string;
  estadoPublicacion: EstadoPublicacion;
  stockTotal: number;
  disponibilidad: Disponibilidad;
  fechaCreacion: string;
}

export interface Pagina<T> {
  content: T[];
  page: {
    size: number;
    number: number;
    totalElements: number;
    totalPages: number;
  };
}

export interface Movimiento {
  id: number;
  varianteId: number;
  tipo: TipoMovimiento;
  cantidad: number;
  stockResultante: number;
  motivo: string | null;
  fechaMovimiento: string;
}

export interface AlertaStock {
  id: number;
  varianteId: number;
  productoNombre: string;
  talla: string;
  color: string;
  sku: string;
  tipo: TipoAlerta;
  stockAlMomento: number;
  leida: boolean;
  fechaCreacion: string;
  fechaResolucion: string | null;
}

export interface InventarioProducto {
  productoId: number;
  nombre: string;
  categoriaId: number;
  categoriaNombre: string;
  stockTotal: number;
  disponibilidad: Disponibilidad;
}

export interface Categoria {
  id: number;
  nombre: string;
}

export interface Empresa {
  id: number;
  nombre: string;
  slogan: string | null;
  descripcion: string | null;
  whatsapp: string;
  telefono: string | null;
  email: string | null;
  direccion: string | null;
  ciudad: string | null;
  pais: string | null;
  instagram: string | null;
  tiktok: string | null;
  facebook: string | null;
  youtube: string | null;
  horario: string | null;
  fechaActualizacion: string;
}

export interface SeccionSitio {
  id: number;
  clave: string;
  eyebrow: string | null;
  titulo: string | null;
  cuerpo: string | null;
  orden: number;
  activa: boolean;
}

export interface MediaSitio {
  id: number;
  seccionId: number | null;
  seccionClave: string | null;
  tipo: TipoMedia;
  url: string;
  publicId: string;
  alt: string | null;
  ancho: number | null;
  alto: number | null;
  orden: number;
  activa: boolean;
}

export interface VideoSitio {
  id: number;
  seccionId: number | null;
  seccionClave: string | null;
  titulo: string;
  etiqueta: string | null;
  url: string;
  publicId: string;
  posterUrl: string | null;
  ancho: number | null;
  alto: number | null;
  duracionSeg: number | null;
  orden: number;
  activa: boolean;
}

export interface Edicion {
  id: number;
  productoId: number;
  productoNombre: string;
  productoEstado: EstadoPublicacion;
  eyebrow: string | null;
  titulo: string | null;
  descripcion: string | null;
  orden: number;
  activa: boolean;
  /** true solo si esta activa y su producto esta publicado. */
  visible: boolean;
}

/** Cuerpo de las peticiones que envian las capturas de pantalla. */
export interface ErrorApi {
  status: number;
  mensaje: string;
  errores?: { campo: string; mensaje: string }[];
}
