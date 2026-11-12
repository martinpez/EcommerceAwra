/**
 * Generado por `npm run upload:media`. No editar a mano.
 *
 * Solo contiene identificadores públicos: las credenciales nunca llegan al
 * bundle del navegador.
 */

export const CLOUDINARY_CLOUD_NAME = 'uhe3fxzj';

export const CLOUDINARY_FOLDER = 'ecommerce-awra/frontend';

export const MEDIA_PUBLIC_IDS = {
  logo: 'brand/awra-logo',
  editions: [
    {
      front: 'catalogo/edicion-i/frente',
      back: 'catalogo/edicion-i/espalda',
      gallery: [
        'catalogo/edicion-i/galeria-01',
        'catalogo/edicion-i/galeria-02',
        'catalogo/edicion-i/galeria-03',
      ],
    },
    {
      front: 'catalogo/edicion-ii/frente',
      back: 'catalogo/edicion-ii/espalda',
      gallery: ['catalogo/edicion-ii/galeria-01', 'catalogo/edicion-ii/galeria-02'],
    },
  ],
  motion: [
    'motion/night-study-01',
    'motion/night-study-02',
    'motion/night-study-03',
    'motion/night-study-04',
  ],
} as const;
