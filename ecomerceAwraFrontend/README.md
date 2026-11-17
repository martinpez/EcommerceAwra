# AwraPublic — Frontend

Tienda pública y panel de administración de AWRA. Angular 22 (standalone + signals),
TypeScript 6, Vitest, Prettier.

La API que consume vive en [`../`](../) (Spring Boot). El frontend no guarda secretos:
habla con la API por HTTP y el token JWT viaja en el `Authorization`.

## Stack

| Pieza | Tecnología |
| --- | --- |
| Framework | Angular 22.2.6 (standalone, sin `NgModule`) |
| Lenguaje | TypeScript 6 |
| Estado | Signals (sin RxJS subjects sueltos) |
| Animación | GSAP 3.15 + ScrollTrigger, Lenis 1.3 |
| Tests | Vitest 5 |
| Formato | Prettier 3.8 |
| Build | `@angular/build` (application builder) |
| Multimedia | Cloudinary (servida por URL) |

## Requisitos

- **Node.js** 20.19+ o 22.12+ (probado con Node 26).
- **npm** 12 o superior.

## Puesta en marcha

```bash
npm ci        # instala lo que está en package-lock.json
npm start     # ng serve -> http://localhost:4200
```

La tienda pública necesita la API levantada (por defecto `http://localhost:8080`).
Si el backend corre en otro origen, ese origen tiene que estar permitido en
`app.cors.origenes` del backend.

## Configuración

La URL de la API está en los archivos de entorno:

| Archivo | Uso |
| --- | --- |
| `src/environments/environment.ts` | Desarrollo (`http://localhost:8080/api`) |
| `src/environments/environment.production.ts` | Producción (`/api`, proxy de nginx) |

El build de producción sustituye un archivo por otro (`fileReplacements` en
`angular.json`), así que la URL real se cambia en un solo sitio.

Los servicios no leen el archivo de entorno directamente: reciben la URL por
inyección mediante el token `API_CONFIG` (`src/app/core/config/api.config.ts`),
para que las pruebas puedan apuntar a una URL falsa sin tocar el código.

## Scripts

| Comando | Qué hace |
| --- | --- |
| `npm start` | Servidor de desarrollo con recarga en caliente |
| `npm run build` | Build de producción en `dist/` |
| `npm run watch` | Build de desarrollo en modo watch |
| `npm test` | Tests unitarios (Vitest) |
| `npm run upload:media` | Sube los medios a Cloudinary y regenera los `public_id` |

### Formato

```bash
npx prettier --write <archivo>
```

### Subida de medios

El script `scripts/upload-media.mjs` necesita credenciales de Cloudinary. Carga las
variables en el entorno antes de ejecutarlo:

```bash
cp scripts/.env.example scripts/.env   # rellena los valores
set -a && . ./scripts/.env && set +a
npm run upload:media
```

`scripts/.env` está en `.gitignore`: las credenciales nunca llegan al repositorio
ni al bundle del navegador.

## Docker

El `Dockerfile` es multi-stage: compila la app con Node y la sirve con nginx.
nginx atiende la SPA y hace de proxy de `/api` hacia el backend, así que el
navegador ve un solo origen y no hay CORS.

```bash
docker build -t awra-frontend .
docker run --rm -p 8088:80 awra-frontend
```

Normalmente se levanta junto con el backend y Oracle desde el compose del
backend:

```bash
cd ..          # a ecomerceAwra/, donde está el docker-compose.yml
docker-compose up -d --build
```

La web queda en `http://localhost:8088`. La URL de la API en producción es
relativa (`/api`), por eso funciona igual en local y en el servidor.

La imagen final pesa ~95 MB. El `application builder` de Angular emite la SPA
en `dist/awra-public/browser/`, y eso es lo que se copia a nginx (no
`dist/awra-public/`, que solo contiene la licencia de terceros).

Detalles de `nginx.conf` que conviene conocer:

- `client_max_body_size 55m`: el panel sube imágenes y videos de hasta 50 MB.
- `try_files ... /index.html`: las rutas de Angular (`/admin/...`,
  `/producto/...`) funcionan al recargar o entrar directo.
- Los assets con hash se cachean un año (`Cache-Control: public, immutable`);
  el `index.html` no se cachea.
- `gzip` activo para JS, CSS, JSON y SVG.
- El proxy pasa `$request_uri` y resuelve `backend` con el DNS de Docker en
  cada petición, así que sigue funcionando si el contenedor del backend se
  recrea con otra IP.

El `HEALTHCHECK` usa `http://127.0.0.1/` y no `localhost`: dentro del
contenedor, `localhost` resuelve primero a `::1` y nginx escucha solo en IPv4,
así que con `localhost` el contenedor se quedaba en `starting`.

## Estructura

```
src/app/
├── core/            Servicios y singletons
│   ├── admin/       Gateway HTTP del panel
│   ├── auth/        Sesión, guard e interceptor de JWT
│   ├── brand/       Identidad visual de la marca
│   ├── browser/     Media queries
│   ├── config/      Configuración de la API
│   ├── media/       Catálogo de assets y URLs de Cloudinary
│   ├── productos/   Gateway y modelos de producto
│   ├── scroll/      Scroll suave y posiciones de trigger
│   └── sitio/       Gateway y modelos del contenido del sitio
├── features/        Pantallas por ruta
│   ├── admin/       Panel de administración
│   ├── home/        Portada y sus secciones
│   └── producto/    Ficha de producto
├── shared/          Componentes UI y directivas reutilizables
└── styles/          Estilos globales
```

## Rutas

| Ruta | Pantalla |
| --- | --- |
| `/` | Portada: hero, ediciones, catálogo y contacto |
| `/producto/:id` | Ficha de producto: tallas, galería y contacto |
| `/admin` | Panel de administración |

Todo se carga en diferido (`loadComponent` / `loadChildren`): quien visita la tienda
no descarga el código del panel.

## Panel de administración

| Ruta | Qué se hace |
| --- | --- |
| `/admin/login` | Acceso |
| `/admin/productos` | Listado con filtros y acciones de publicación |
| `/admin/productos/:id` | Ficha del producto: datos, tallas y fotos |
| `/admin/inventario` | Movimientos de stock, historial y alertas |
| `/admin/categorias` | Categorías del catálogo |
| `/admin/contenido` | Datos de la empresa y textos de las secciones |
| `/admin/multimedia` | Logo, imágenes de secciones y videos |
| `/admin/ediciones` | Qué productos aparecen en la portada |

El panel usa las mismas credenciales que la API (`ADMIN_USERNAME` y
`ADMIN_PASSWORD` del backend).

## Assets y multimedia

Todo el multimedia se sirve desde Cloudinary. Las URLs se construyen en
`src/app/core/media/cloudinary.ts` a partir del catálogo de assets
(`src/app/core/media/asset-catalog.ts`) y de los `public_id` generados
(`src/app/core/media/media-urls.generated.ts`).

La carpeta `media/` guarda los originales (logos, fotos, videos). Está en
`.gitignore` y no se versiona: los originales se conservan en local y en Cloudinary,
no en el repositorio.

## Convenciones

- Componentes **standalone**: nada de `NgModule` nuevo.
- Estado con **signals**, no con RxJS subjects sueltos.
- Las rutas se registran en `src/app/app.routes.ts`; toda pantalla nueva se
  agrega ahí.
- La API se llama a través de los gateways de `core/`, nunca con `fetch` directo
  desde un componente.
- Los componentes se nombran sin sufijo `.component` (`home-page.ts`), y los
  archivos usan guiones en kebab-case.
