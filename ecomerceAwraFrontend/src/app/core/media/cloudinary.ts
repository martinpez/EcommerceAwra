import { CLOUDINARY_CLOUD_NAME, CLOUDINARY_FOLDER } from './media-urls.generated';

const BASE = `https://res.cloudinary.com/${CLOUDINARY_CLOUD_NAME}`;

/**
 * Transformaciones de entrega.
 *
 * - `f_auto`: Cloudinary sirve WebP o AVIF según lo que acepte el navegador y
 *   cae al formato original si no soporta ninguno.
 * - `q_auto`: la calidad se ajusta al contenido de la imagen (una sudadera
 *   negra sobre fondo oscuro necesita mucho menos bitrate que una foto de
 *   estudio).
 * - `w_`: se entrega el ancho máximo que la web usa en pantalla. Pedir más
 *   solo agranda el archivo sin ganancia visible.
 */
export const IMAGE_TRANSFORM = {
  /** Fotos de producto: se ven a media pantalla, ~500 px. */
  catalog: 'f_auto,q_auto,w_960',
  /** Galería: se abre a tamaño grande, ~1200 px. */
  gallery: 'f_auto,q_auto,w_1200',
  /** Logo: se muestra enorme en el hero, hasta 1024 px. */
  logo: 'f_auto,q_auto,w_1600',
} as const;

/**
 * Transformación de los videos.
 *
 * `q_45` baja el bitrate de vídeo sin diferencia visible en material de noche
 * con grano, `vc_h264` garantiza compatibilidad universal, `w_720` es la
 * resolución nativa (la web los muestra a ~400 px de ancho) y `fps_24` quita
 * los fotogramas que no se ven. La pista de audio con su música se conserva.
 */
export const VIDEO_TRANSFORM = 'q_45,vc_h264,w_720,fps_24';

const withFolder = (publicId: string): string => `${CLOUDINARY_FOLDER}/${publicId}`;

/** URL de entrega de una imagen optimizada. */
export const cloudinaryImage = (
  publicId: string,
  transform: string = IMAGE_TRANSFORM.catalog,
): string => `${BASE}/image/upload/${transform}/${withFolder(publicId)}`;

/** URL de entrega de un video optimizado. */
export const cloudinaryVideo = (publicId: string, transform: string = VIDEO_TRANSFORM): string =>
  `${BASE}/video/upload/${transform}/${withFolder(publicId)}.mp4`;

/** Poster del primer frame: se muestra mientras el video carga. */
export const cloudinaryVideoPoster = (publicId: string): string =>
  `${BASE}/video/upload/so_0,f_jpg,q_auto/${withFolder(publicId)}.jpg`;
