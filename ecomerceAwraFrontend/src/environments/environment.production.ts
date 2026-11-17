/**
 * Configuracion del entorno de produccion.
 *
 * La web se sirve detras de nginx, que hace de proxy de `/api` hacia el
 * backend (ver `nginx.conf` y el `docker-compose.yml` del backend). Al usar
 * una ruta relativa, el navegador ve un solo origen y no hace falta CORS.
 *
 * Si la API viviera en otro dominio, aqui iria la URL absoluta y ese origen
 * tendria que estar en `app.cors.origenes` del backend.
 */
export const environment = {
  /** Ruta relativa: nginx la reenvia al backend. */
  apiUrl: '/api',
  production: true,
};
