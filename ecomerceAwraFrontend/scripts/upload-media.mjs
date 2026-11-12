/**
 * Sube los medios del frontend a Cloudinary y regenera
 * `src/app/core/media/media-urls.generated.ts` con los `public_id`.
 *
 * Las credenciales se leen del entorno (nunca se versionan):
 *   CLOUDINARY_CLOUD_NAME, CLOUDINARY_API_KEY, CLOUDINARY_API_SECRET
 *
 * Uso:
 *   set -a && . ../ecomerceAwra/.env && set +a && npm run upload:media
 */
import { readFile, writeFile } from 'node:fs/promises';
import { existsSync } from 'node:fs';
import { dirname, join, resolve } from 'node:path';
import { fileURLToPath } from 'node:url';

const ROOT = resolve(dirname(fileURLToPath(import.meta.url)), '..');
const FOLDER = 'ecommerce-awra/frontend';

const CLOUD_NAME = process.env.CLOUDINARY_CLOUD_NAME;
const API_KEY = process.env.CLOUDINARY_API_KEY;
const API_SECRET = process.env.CLOUDINARY_API_SECRET;

if (!CLOUD_NAME || !API_KEY || !API_SECRET) {
  console.error(
    'Faltan las credenciales. Exporta CLOUDINARY_CLOUD_NAME, CLOUDINARY_API_KEY y CLOUDINARY_API_SECRET.',
  );
  process.exit(1);
}

const AUTH = `Basic ${Buffer.from(`${API_KEY}:${API_SECRET}`).toString('base64')}`;
const API = `https://api.cloudinary.com/v1_1/${CLOUD_NAME}`;

/**
 * Archivos de imagen que la web usa hoy, con el `public_id` que queremos
 * en Cloudinary. Se sube el archivo tal cual: Cloudinary genera las variantes
 * optimizadas (f_auto, q_auto, anchos) bajo demanda y las cachea.
 */
const IMAGES = [
  { file: 'media/brand/awra-logo.webp', id: 'brand/awra-logo' },
  { file: 'media/catalogo/edicion-i/frente.webp', id: 'catalogo/edicion-i/frente' },
  { file: 'media/catalogo/edicion-i/espalda.webp', id: 'catalogo/edicion-i/espalda' },
  { file: 'media/catalogo/edicion-i/galeria-01.webp', id: 'catalogo/edicion-i/galeria-01' },
  { file: 'media/catalogo/edicion-i/galeria-02.webp', id: 'catalogo/edicion-i/galeria-02' },
  { file: 'media/catalogo/edicion-i/galeria-03.webp', id: 'catalogo/edicion-i/galeria-03' },
  { file: 'media/catalogo/edicion-ii/frente.webp', id: 'catalogo/edicion-ii/frente' },
  { file: 'media/catalogo/edicion-ii/espalda.webp', id: 'catalogo/edicion-ii/espalda' },
  { file: 'media/catalogo/edicion-ii/galeria-01.webp', id: 'catalogo/edicion-ii/galeria-01' },
  { file: 'media/catalogo/edicion-ii/galeria-02.webp', id: 'catalogo/edicion-ii/galeria-02' },
];

/**
 * Videos verticales con pista de audio. Se suben como `video` para que
 * Cloudinary transcodifique y sirva MP4/WebM, y se pide por adelantado (de
 * forma asíncrona) la variante optimizada y el poster del primer frame.
 *
 * La variante es la misma que se usa en la URL de entrega:
 *   q_45,vc_h264,w_720,fps_24
 * Reduce el archivo a menos de la mitad sin diferencia visible (el material es
 * de noche y con grano) y conserva la pista de audio con su música.
 */
const VIDEOS = [
  { file: 'media/videos y imagenes vanilas/igexport-DecjvUny1B0.mp4', id: 'motion/night-study-01' },
  { file: 'media/videos y imagenes vanilas/igexport-DdimJsXTjRY.mp4', id: 'motion/night-study-02' },
  { file: 'media/videos y imagenes vanilas/igexport-DeChch6RB4K.mp4', id: 'motion/night-study-03' },
  { file: 'media/videos y imagenes vanilas/igexport-Dd10SGUyuE5.mp4', id: 'motion/night-study-04' },
];

export const VIDEO_TRANSFORM = 'q_45,vc_h264,w_720,fps_24';

async function upload({ file, id, resourceType = 'image', extra = {} }) {
  const path = join(ROOT, file);

  if (!existsSync(path)) {
    throw new Error(`No existe el archivo: ${path}`);
  }

  const bytes = await readFile(path);
  const form = new FormData();

  form.append('file', new Blob([bytes]), file.split('/').pop());
  form.append('public_id', id);
  form.append('folder', FOLDER);
  form.append('overwrite', 'true');
  form.append('invalidate', 'true');
  form.append('use_filename', 'false');

  for (const [key, value] of Object.entries(extra)) {
    form.append(key, String(value));
  }

  const response = await fetch(`${API}/${resourceType}/upload`, {
    method: 'POST',
    headers: { Authorization: AUTH },
    body: form,
  });

  const payload = await response.json();

  if (!response.ok) {
    throw new Error(
      `Fallo al subir ${file}: ${payload?.error?.message ?? response.status}`,
    );
  }

  return payload;
}

async function main() {
  console.log(`Subiendo a Cloudinary (${CLOUD_NAME}) en ${FOLDER}…\n`);

  console.log('Imágenes');
  for (const image of IMAGES) {
    const payload = await upload(image);
    const kb = Math.round((payload.bytes ?? 0) / 1024);
    console.log(`  ✓ ${image.id} (${kb} kB)`);
  }

  console.log('\nVideos');
  for (const video of VIDEOS) {
    // `eager_async` encola la transcodificación sin bloquear la subida.
    const payload = await upload({
      ...video,
      resourceType: 'video',
      extra: {
        format: 'mp4',
        eager_async: 'true',
        eager: `${VIDEO_TRANSFORM}|so_0,f_jpg,q_auto`,
      },
    });
    const kb = Math.round((payload.bytes ?? 0) / 1024);
    console.log(`  ✓ ${video.id} (${kb} kB)`);
  }

  const generated = `/**
 * Generado por \`npm run upload:media\`. No editar a mano.
 *
 * Solo contiene identificadores públicos: las credenciales nunca llegan al
 * bundle del navegador.
 */

export const CLOUDINARY_CLOUD_NAME = '${CLOUD_NAME}';

export const CLOUDINARY_FOLDER = '${FOLDER}';

export const MEDIA_PUBLIC_IDS = {
  logo: '${IMAGES[0].id}',
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
      gallery: [
        'catalogo/edicion-ii/galeria-01',
        'catalogo/edicion-ii/galeria-02',
      ],
    },
  ],
  motion: ['motion/night-study-01', 'motion/night-study-02', 'motion/night-study-03', 'motion/night-study-04'],
} as const;
`;

  const target = join(ROOT, 'src/app/core/media/media-urls.generated.ts');
  await writeFile(target, generated, 'utf8');

  console.log(`\nEscrito ${target}`);
}

main().catch((error) => {
  console.error(`\n${error.message}`);
  process.exit(1);
});
