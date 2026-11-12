# Documentación del frontend público AWRA

Frontend Angular para la tienda pública de AWRA: buzos reflectivos colombianos para
streetwear de moto y noche.

La guía de arranque, los comandos y la estructura del proyecto están en el
[README del frontend](../README.md). Este documento es la referencia de operación.

## Comandos principales

```bash
npm start
npm run build
npm test
npm run upload:media   # sube los medios a Cloudinary y regenera los public_id
```

## Configuración

La URL de la API está en `src/environments/environment.ts` (desarrollo) y
`src/environments/environment.production.ts` (producción). El build de producción
sustituye un archivo por otro, así que la URL real se cambia en un solo sitio.

## Panel de administración

El panel vive en `/admin` y usa las mismas credenciales que la API
(`ADMIN_USERNAME` y `ADMIN_PASSWORD` del backend).

| Ruta                   | Qué se hace                                   |
| ---------------------- | --------------------------------------------- |
| `/admin/login`         | Acceso                                        |
| `/admin/productos`     | Listado con filtros y acciones de publicación |
| `/admin/productos/:id` | Ficha del producto: datos, tallas y fotos     |
| `/admin/inventario`    | Movimientos de stock, historial y alertas     |
| `/admin/categorias`    | Categorías del catálogo                       |
| `/admin/contenido`     | Datos de la empresa y textos de las secciones |
| `/admin/multimedia`    | Logo, imágenes de secciones y videos          |
| `/admin/ediciones`     | Qué productos aparecen en la portada          |

Todo el panel se carga en diferido: quien visita la tienda no descarga su código.

## Assets

Todo el multimedia se sirve desde Cloudinary (cloud `uhe3fxzj`, carpeta
`ecommerce-awra/frontend`). Los originales se conservan en `media/`, fuera de
`public/`, para que el build no los copie. Esa carpeta no se versiona en Git.

- `media/brand/`: logos AWRA.
- `media/catalogo/`: copias normalizadas de las imágenes de cada edición para evitar
  problemas de URL con espacios, tildes y `:`.
- `media/Imagenes editadas/`: originales aportados por el usuario; no se modifican.
- `media/videos y imagenes vanilas/`: originales audiovisuales aportados por el usuario.
- `media/textures/`: ruido, texturas y recursos visuales.

El catálogo de assets en `src/app/core/media/asset-catalog.ts` centraliza las URLs
utilizadas por los componentes, y `src/app/core/media/cloudinary.ts` construye las
URLs de entrega con las transformaciones.
