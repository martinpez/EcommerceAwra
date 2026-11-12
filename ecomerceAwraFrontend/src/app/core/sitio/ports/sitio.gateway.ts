import { Observable } from 'rxjs';
import { EdicionPublica, Sitio } from '../models/sitio.model';

/**
 * Contrato del contenido del sitio.
 *
 * Igual que `ProductosGateway`, la interfaz permite que los componentes no
 * sepan de donde vienen los datos: hoy es la API, mañana podria ser un
 * archivo local o una cache.
 */
export abstract class SitioGateway {
  abstract obtenerSitio(): Observable<Sitio>;
  abstract obtenerEdiciones(): Observable<EdicionPublica[]>;
}
