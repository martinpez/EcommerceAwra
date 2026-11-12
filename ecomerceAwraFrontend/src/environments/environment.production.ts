/**
 * Configuracion del entorno de produccion.
 *
 * La URL real de la API se define aqui (o en el servidor que aloja la web).
 * El dominio debe estar tambien en `app.cors.origenes` del backend.
 */
export const environment = {
  apiUrl: 'https://api.awra.co/api',
  production: true,
};
