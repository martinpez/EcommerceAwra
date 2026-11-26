# EcommerAwra — Backend

[![CI](https://github.com/martinpez/EcommerceAwra/actions/workflows/ci.yml/badge.svg)](https://github.com/martinpez/EcommerceAwra/actions/workflows/ci.yml)

API REST de la tienda AWRA. Spring Boot 4.1.2 · Java 21 · Spring Data JPA · Flyway · Oracle.

La tienda pública y el panel de administración (Angular) viven en
[`ecomerceAwraFrontend/`](ecomerceAwraFrontend/).

## Stack

| Pieza | Tecnología |
| --- | --- |
| Runtime | Java 21 |
| Framework | Spring Boot 4.1.2 (`spring-boot-starter-webmvc`) |
| Persistencia | Spring Data JPA + Hibernate |
| Esquema | Flyway (`src/main/resources/db/migration/V*.sql`) |
| Base de datos | Oracle 23 (producción) · H2 en memoria (desarrollo) |
| Seguridad | Spring Security + OAuth2 Resource Server (JWT HS256, stateless) |
| Multimedia | Cloudinary HTTP 5 |
| Documentación | springdoc-openapi (solo en el perfil `dev`) |
| Build | Maven Wrapper 3.3.4 (no hace falta Maven instalado) |

## Requisitos

- **Java 21** en el `PATH`.
- **Docker** solo si vas a levantar Oracle (opcional en desarrollo).
- El **Maven Wrapper** (`./mvnw`) ya viene en el repositorio.

## Puesta en marcha

### 1. Preparar las variables de entorno

```bash
cp .env.example .env
```

Edita `.env` y pon los valores reales. `.env` **nunca** se sube a Git: es el archivo
donde viven las credenciales (Oracle, JWT, Cloudinary).

El backend no lee `.env` solo: las variables tienen que estar en el entorno del
proceso. Carga el archivo en la terminal antes de arrancar:

```bash
set -a && . ./.env && set +a
```

### 2. Elegir perfil

**Desarrollo (`dev`, por defecto).** Base de datos H2 en memoria, sin Flyway, con
Swagger y la consola H2 activadas. No requiere Docker.

```bash
./mvnw spring-boot:run
```

**Oracle (`oracle`).** Esquema gestionado por Flyway, `ddl-auto=validate`.

```bash
docker compose up -d                      # Oracle 23 en localhost:1521
./mvnw spring-boot:run -Dspring-boot.run.profiles=oracle
```

La API queda en `http://localhost:8080`.

### 3. Entrar al panel

El usuario administrador se crea solo la primera vez que arranca la aplicación,
tomando `ADMIN_USERNAME` y `ADMIN_PASSWORD` del entorno.

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"tu-clave"}'
```

La respuesta trae el token JWT. Envíalo como `Authorization: Bearer <token>` en las
peticiones a `/api/admin/**`.

## Variables de entorno

| Variable | Para qué | Valor por defecto |
| --- | --- | --- |
| `JWT_SECRET` | Firma de los tokens. Mínimo 32 caracteres (`openssl rand -hex 32`). | — (obligatoria) |
| `ADMIN_USERNAME` | Administrador inicial. | vacío (no se crea) |
| `ADMIN_PASSWORD` | Contraseña del administrador inicial. | vacío (obligatoria si hay usuario) |
| `ORACLE_PASSWORD` | Clave del usuario SYS del contenedor de Oracle. | — |
| `ORACLE_APP_USER` | Usuario de aplicación (Flyway y JPA). | — |
| `ORACLE_APP_PASSWORD` | Clave del usuario de aplicación. | — |
| `ORACLE_URL` | JDBC de Oracle. | `jdbc:oracle:thin:@//localhost:1521/FREEPDB1` |
| `CLOUDINARY_CLOUD_NAME` | Cloud de Cloudinary. | — |
| `CLOUDINARY_API_KEY` | Credencial de Cloudinary. | — |
| `CLOUDINARY_API_SECRET` | Credencial de Cloudinary. | — |
| `CLOUDINARY_CARPETA_SITIO` | Carpeta de los medios del sitio. | `ecommerce-awra/sitio` |

Al agregar una variable nueva, inclúyela en `.env.example` con un valor de ejemplo.

## Endpoints

### Público (sin autenticación)

| Método | Ruta | Qué hace |
| --- | --- | --- |
| `GET` | `/api/publico/sitio` | Contenido del sitio: empresa, secciones, medios y ediciones |
| `GET` | `/api/publico/ediciones` | Ediciones publicadas |
| `GET` | `/api/publico/categorias` | Categorías del catálogo |
| `GET` | `/api/publico/productos` | Catálogo paginado |
| `GET` | `/api/publico/productos/{id}` | Ficha de producto con variantes e imágenes |

### Autenticación

| Método | Ruta | Qué hace |
| --- | --- | --- |
| `POST` | `/api/auth/login` | Inicia sesión y devuelve el token JWT |
| `GET` | `/api/auth/me` | Usuario de la sesión actual |

### Administración (JWT + rol `ADMIN`)

| Método | Ruta | Qué hace |
| --- | --- | --- |
| `GET` `POST` | `/api/admin/productos` | Listado con filtros / crear producto |
| `GET` `PUT` `DELETE` | `/api/admin/productos/{id}` | Detalle / actualizar / borrar (borrado lógico) |
| `PATCH` | `/api/admin/productos/{id}/publicacion` | Publicar o despublicar |
| `POST` | `/api/admin/productos/{productoId}/variantes` | Crear variante (talla y stock) |
| `PUT` `DELETE` | `/api/admin/variantes/{id}` | Actualizar / borrar variante |
| `GET` `POST` | `/api/admin/productos/{productoId}/imagenes` | Listar / subir imagen |
| `PATCH` `DELETE` | `/api/admin/productos/{productoId}/imagenes/{imagenId}` | Marcar principal, ordenar, borrar |
| `GET` `POST` | `/api/admin/categorias` | Listar / crear categoría |
| `GET` `PUT` `DELETE` | `/api/admin/categorias/{id}` | Detalle / actualizar / borrar |
| `GET` | `/api/admin/inventario` | Inventario consolidado por producto |
| `GET` `POST` | `/api/admin/variantes/{varianteId}/movimientos` | Historial / registrar movimiento |
| `GET` | `/api/admin/alertas` | Alertas de stock bajo |
| `PATCH` | `/api/admin/alertas/{id}/leida` | Marcar alerta como leída |
| `GET` `PUT` | `/api/admin/sitio/empresa` | Datos de la empresa |
| `GET` `POST` | `/api/admin/sitio/secciones` | Listar / crear sección del sitio |
| `GET` `PUT` | `/api/admin/sitio/secciones/{id}` | Detalle / actualizar sección |
| `GET` `POST` | `/api/admin/sitio/media` | Listar / subir imagen de sección |
| `GET` `PUT` `DELETE` | `/api/admin/sitio/media/{id}` | Detalle / actualizar / borrar |
| `GET` `POST` | `/api/admin/sitio/videos` | Listar / subir video de sección |
| `GET` `PUT` `DELETE` | `/api/admin/sitio/videos/{id}` | Detalle / actualizar / borrar |
| `GET` `POST` | `/api/admin/sitio/ediciones` | Listar / crear edición |
| `PUT` | `/api/admin/sitio/ediciones/{id}` | Actualizar edición |

### Documentación de la API

Solo en el perfil `dev`:

- OpenAPI: `http://localhost:8080/v3/api-docs`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

## Migraciones

Flyway corre al arrancar con el perfil `oracle`. Los scripts viven en
`src/main/resources/db/migration/`:

| Script | Qué crea |
| --- | --- |
| `V1__esquema_inicial.sql` | Productos, variantes, categorías e inventario |
| `V2__seguridad.sql` | Usuarios y roles |
| `V3__imagenes.sql` | Imágenes de producto |
| `V4__empresa_y_contenido.sql` | Empresa, secciones, medios, videos y ediciones |

**Nunca edites una migración ya aplicada.** Crea la siguiente (`V5__...`).

## Comandos

```bash
./mvnw -q compile    # compilar
./mvnw -q test       # tests
./mvnw spring-boot:run
docker compose up -d # Oracle
```

## Docker

El `docker-compose.yml` levanta el stack completo: Oracle, la API y la web
(nginx). Solo se publican la web y la base de datos; la API queda en la red
interna de Docker.

```bash
docker-compose up -d --build     # primera vez (descarga Oracle, ~2.8 GB)
docker-compose ps                # estado y salud de los tres servicios
docker-compose logs -f backend
docker-compose down              # baja los contenedores (conserva Oracle)
docker-compose down -v           # baja y borra los datos de Oracle
```

> Si tienes el plugin de Compose v2, los mismos comandos funcionan como
> `docker compose` (con espacio en vez de guion).

| Servicio | Imagen | Puerto en el host | Tamaño |
| --- | --- | --- | --- |
| `frontend` | nginx + build Angular | `${PUERTO_WEB:-8088}` → 80 | ~95 MB |
| `backend` | Spring Boot (perfil `oracle`) | interno, 8080 | ~603 MB |
| `oracle` | `gvenzl/oracle-free:23-slim` | `127.0.0.1:1521` | ~2.8 GB |

La web queda en `http://localhost:8088`. La primera vez Oracle tarda ~90 s en
estar sano; el backend espera a ese healthcheck y nginx espera al backend. El
arranque del backend, ya con Oracle sano, tarda ~11 s (Flyway valida las 4
migraciones y el esquema queda en `AWRA_APP` versión 4). Con todo arriba, el
consumo es de unos 2.5 GB de RAM (Oracle ~2 GB, backend ~440 MB, nginx ~12 MB).

### Comprobar que quedó bien

```bash
curl -s -o /dev/null -w "SPA %{http_code}\n"  http://localhost:8088/
curl -s -o /dev/null -w "API %{http_code}\n"  http://localhost:8088/api/publico/sitio
curl -s -o /dev/null -w "401 %{http_code}\n"  http://localhost:8088/api/admin/productos
```

- `SPA 200`: nginx sirve Angular.
- `API 200`: el proxy `/api` llega al backend y el backend habla con Oracle.
- `admin 401`: la API está protegida y no se publica directamente.

### Cómo se comunican la web y la API

nginx sirve la SPA y hace de proxy de `/api` hacia `backend:8080`, así que el
navegador ve un solo origen. Aun así el backend sigue validando el origen de lo
que llega por el proxy (el puerto interno no coincide con el publicado), así
que **`CORS_ORIGENES` debe incluir el origen real de la web**. Verificado: un
origen que no está en la lista recibe `403`.

Si cambias `PUERTO_WEB`, cambia también `CORS_ORIGENES`:

```bash
# .env
PUERTO_WEB=80
CORS_ORIGENES=https://awra.co,https://www.awra.co
```

Variables que afectan al stack (en `.env`):

| Variable | Para qué | Valor por defecto |
| --- | --- | --- |
| `PUERTO_WEB` | Puerto del host donde se publica la web. | `8088` |
| `CORS_ORIGENES` | Orígenes permitidos por la API. | `http://localhost:8088,http://127.0.0.1:8088` |

El contenedor lee `.env` y `.env.jwt` (el segundo pisa al primero), así que
necesita `JWT_SECRET`, `ADMIN_*` y `CLOUDINARY_*` reales para arrancar.

## Estructura

```
src/main/java/com/awra/co/ecommerceawra/
├── config/      Beans y configuración (Cloudinary, OpenAPI)
├── content/     Runners de contenido inicial y datos de demostración
├── controller/  Endpoints REST
├── dto/         Request y response (la API nunca devuelve entidades)
├── entity/      Entidades JPA
├── exception/   Excepciones y manejador global
├── media/       Validación de archivos
├── repository/  Repositorios Spring Data
├── security/    JWT, seguridad y usuario administrador inicial
└── service/     Lógica de negocio
```

## Convenciones

- Los endpoints devuelven **DTOs**, nunca entidades JPA.
- La entrada se valida con `jakarta.validation` (`@Valid`).
- Los cambios de esquema van por **Flyway**, nunca por `ddl-auto`.
- Los secretos se leen del entorno; nunca se escriben en el código.
- Cuando el frontend necesite algo nuevo, el contrato (método, ruta, request,
  response) se entrega como respuesta, no se edita el frontend desde aquí.
# EcommerceAwra
# EcommerceAwra
# EcommerceAwra
