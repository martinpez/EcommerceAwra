import { cloudinaryImage, IMAGE_TRANSFORM } from './cloudinary';
import { MEDIA_PUBLIC_IDS } from './media-urls.generated';

/**
 * Catalogo de medios del frontend.
 *
 * Antes aqui vivian las ediciones y los videos fijos. Desde la Fase 10 el
 * contenido llega de la API, asi que solo queda lo que hace falta como
 * respaldo: el logo, por si la API todavia no ha contestado.
 */

export interface MotionMedia {
  id: string;
  label: string;
  src: string;
  poster: string;
}

/**
 * Logo de respaldo.
 *
 * El logo que manda es el que sube el administrador (viene en la API). Este
 * se usa mientras la API responde y si deja de hacerlo, para que el hero
 * nunca se quede en negro.
 */
export const BRAND_ASSETS = {
  logo: cloudinaryImage(MEDIA_PUBLIC_IDS.logo, IMAGE_TRANSFORM.logo),
  logoWidth: 1600,
  logoHeight: 533,
} as const;
