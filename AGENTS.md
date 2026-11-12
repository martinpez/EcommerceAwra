# Backend — ecomerceAwra

API REST de EcommerAwra. Spring Boot 4.1.2 / Java 21 / Spring Data JPA / Flyway / Oracle.

## Comandos

```bash
cd ecomerceAwra
./mvnw -q compile          # compilar (verificación mínima antes de entregar)
./mvnw -q test             # tests
./mvnw spring-boot:run     # levantar la API
docker compose up -d       # base de datos Oracle (solo si la API no levanta por datasource)
```

Los secretos están en `.env`, `.env.example` y `.env.jwt`. No los edites a mano ni los
subas a git; si hace falta una variable nueva, agrégala a `.env.example`.

## Estructura

`src/main/java/com/awra/co/ecommerceawra/`

| Paquete | Contenido |
| --- | --- |
| `entity` | Entidades JPA mapeadas a Oracle |
| `repository` | Repositorios Spring Data |
| `service` | Lógica de negocio |
| `controller` | Endpoints REST |
| `dto` | Request/response (usa DTOs, no expongas entidades) |
| `exception` | Excepciones y manejo global |
| `security` | Autenticación / autorización |
| `config` | Beans y configuración |
| `media` | Archivos y medios |
| `content` | Contenido del sitio |

Migraciones Flyway: `src/main/resources/db/migration/V*.sql`. **Nunca** edites una
migración ya aplicada; crea la siguiente (`V5__...`).

## Convenciones

- Los endpoints no devuelven entidades JPA: siempre DTOs.
- Validación de entrada con `jakarta.validation` (`@Valid`).
- Los cambios de esquema van por Flyway, nunca por `ddl-auto`.
- Si el frontend necesita algo nuevo, entrega el contrato (método, ruta, request,
  response, status codes) en la respuesta final; no toques `ecomerceAwraFrontend/`.
