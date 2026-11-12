# Frontend — ecomerceAwraFrontend

App web Angular 22 (standalone + signals), TypeScript 6, Vitest, Prettier.

## Comandos

```bash
cd ecomerceAwraFrontend
npm start                  # ng serve
npm run build              # build de producción (verificación mínima antes de entregar)
npm test                   # tests (vitest)
npx prettier --write <archivo>   # formatear
npm run upload:media       # script de subida de medios
```

## Estructura

`src/app/`

| Carpeta | Contenido |
| --- | --- |
| `core/` | Servicios y singletons: `brand`, `productos`, `media`, `browser`, `scroll`, `config`, `sitio` |
| `features/` | Pantallas por ruta: `home`, `catalogo`, `producto` |
| `shared/` | Componentes UI y directivas reutilizables |
| `styles/` | Estilos globales |
| `environments/` | Configuración por entorno (URLs de la API) |

## Convenciones

- Componentes **standalone**: nada de `NgModule` nuevo.
- Estado con **signals**, no con RxJS subjects sueltos.
- Rutas registradas en `app.routes.ts`; toda pantalla nueva se agrega ahí.
- Llama a la API a través de los servicios de `core/`, no con `fetch` directo desde el componente.
- Si necesitas un endpoint o campo nuevo del backend, entrega el contrato (método,
  ruta, request, response) en la respuesta final; no toques `ecomerceAwra/`.
