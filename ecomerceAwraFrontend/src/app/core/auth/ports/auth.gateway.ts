import { Observable } from 'rxjs';
import { Sesion, UsuarioSesion } from '../models/sesion.model';

export abstract class AuthGateway {
  abstract login(username: string, password: string): Observable<Sesion>;
  abstract me(): Observable<UsuarioSesion>;
}
