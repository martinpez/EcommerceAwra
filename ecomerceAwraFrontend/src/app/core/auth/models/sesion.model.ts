/** Sesion iniciada: el token y lo que dura. */
export interface Sesion {
  token: string;
  tipo: string;
  expiraEnSegundos: number;
}

/** Quien esta autenticado. Lo devuelve `GET /api/auth/me`. */
export interface UsuarioSesion {
  username: string;
  roles: string[];
}
