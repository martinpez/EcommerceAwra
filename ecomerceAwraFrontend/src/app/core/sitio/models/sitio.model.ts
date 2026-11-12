/**
 * Modelo del contenido del sitio que devuelve la API publica.
 *
 * Refleja los DTOs del backend (`SitioPublicoResponse`, `EdicionPublicaResponse`
 * y los que contienen). Los nombres estan en ingles porque son los del
 * contrato de la API; la presentacion en español se hace en los componentes.
 */

export type Disponibilidad = 'DISPONIBLE' | 'SIN_STOCK';

export type TipoMedia = 'LOGO' | 'IMAGEN';

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

/** Todo el contenido del sitio en una sola respuesta. */
export interface Sitio {
  empresa: Empresa;
  secciones: SeccionSitio[];
  media: MediaSitio[];
  videos: VideoSitio[];
}

/** Una edicion de la Home, tal y como la devuelve la API. */
export interface EdicionPublica {
  id: number;
  eyebrow: string | null;
  titulo: string | null;
  descripcion: string | null;
  orden: number;
  productoId: number;
  productoNombre: string;
  precio: number;
  imagenFrente: string | null;
  imagenEspalda: string | null;
  disponibilidad: Disponibilidad;
  etiqueta: string | null;
}

/**
 * La edicion como la pinta la Home.
 *
 * Se separa del DTO de la API porque aqui ya estan decididos los textos por
 * defecto y a que producto apunta el enlace.
 */
export interface EdicionHome {
  /** Id del producto: es a donde lleva el enlace "Ver más detalles". */
  id: number;
  eyebrow: string;
  title: string;
  description: string;
  front: string;
  back: string;
  /** Texto de la insignia cuando el producto esta agotado. */
  etiqueta: string | null;
}
