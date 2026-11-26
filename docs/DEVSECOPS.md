# DevSecOps — Decisiones del pipeline

Documento vivo. Si cambia una decisión, se actualiza aquí y se justifica en el PR.

## Alcance

- Repositorio: `ecomerceAwra/` (raíz del repo Git).
- Apps: backend Spring Boot (`ecomerceAwra/`) y frontend Angular
  (`ecomerceAwra/ecomerceAwraFrontend/`).
- Orquestador: GitHub Actions.
- Destino de despliegue: servidor con Docker Compose (se concreta en la Fase 14D).

## 1. Cuándo se dispara el pipeline

| Evento | Qué se ejecuta | Objetivo |
|---|---|---|
| `pull_request` hacia `main` | CI + seguridad | Validar antes de integrar |
| `push` a `main` | CI + seguridad + publicación de imágenes | Dejar artefacto versionado |
| `workflow_dispatch` | Despliegue manual de un tag concreto | Continuous Delivery (primera etapa) |
| `push` a `main` (más adelante) | Despliegue automático con aprobación del environment | Continuous Deployment |

Regla: un PR nunca publica imágenes ni despliega.

## 2. Qué bloquea un pull request

**Bloqueante desde el inicio:**

- Compilación del backend.
- Pruebas unitarias del backend.
- Pruebas del frontend.
- Build de producción del frontend.
- Detección de secretos (`gitleaks`).

**Informativo al inicio (no bloquea):**

- CodeQL / SAST.
- Análisis de dependencias (Maven y npm).
- Escaneo de imágenes (Trivy).
- Escaneo de configuración (`trivy config`).

**Criterio para endurecer:** tras dos iteraciones revisando reportes, se vuelven
bloqueantes los hallazgos de severidad alta o crítica que tengan señal clara. El resto
queda como informativo para no bloquear por ruido.

## 3. Dónde viven los secretos

| Secreto | Dónde vive | Quién lo usa |
|---|---|---|
| `.env`, `.env.jwt` (JWT, Oracle, Cloudinary) | Solo en el servidor de despliegue | Docker Compose |
| `DEPLOY_HOST`, `DEPLOY_USER`, `DEPLOY_SSH_KEY` | GitHub Environment `produccion` | Job de despliegue |
| `GITHUB_TOKEN` | Lo genera GitHub en cada ejecución | Publicación en `ghcr.io` |

Reglas:

- Ningún secreto en el repositorio, en el YAML del workflow ni en variables públicas.
- `.env.example` solo contiene valores ficticios.
- El pipeline de despliegue nunca copia `.env` ni `.env.jwt` al servidor: esos archivos ya
  están allí y no se sobrescriben.
- El environment `produccion` puede exigir aprobación manual antes de ejecutar el job.

## 4. Estrategia de artefactos y tags

- Las imágenes se publican en GitHub Container Registry:
  - `ghcr.io/<owner>/<repo>-backend:<sha>`
  - `ghcr.io/<owner>/<repo>-frontend:<sha>`
- Cada imagen lleva además el tag `latest`, solo por comodidad.
- El despliegue y el rollback usan siempre el tag `<sha>`, nunca `latest`.
- Rollback = volver a desplegar el `<sha>` anterior.

## Flujo resumido

```
PR        → CI + seguridad (informativa)        → checks en el PR
push main → CI + seguridad + build/push ghcr.io → imágenes <sha> y latest
deploy    → environment produccion              → compose pull + up -d + smoke test
```

## Definition of Done de la Fase 14A

- [x] Pruebas locales en verde (backend, frontend y build).
- [x] `docker-compose.yml` validado.
- [x] Repositorio de GitHub revisado: público, sin ruleset en `main`, escaneo desactivado.
- [ ] Servidor revisado: Docker Compose validado en la EC2; pendiente `.env`, `.env.jwt`,
      `docker compose ps` y memoria (14D).
- [x] Este documento creado.
- [ ] Confirmación explícita del usuario sobre las cuatro decisiones.

## Cómo reproducir el CI en local

```bash
# Backend (desde la raíz del repo)
./mvnw -B test

# Frontend
cd ecomerceAwraFrontend
npm ci
npm test -- --watch=false
npm run build
```
