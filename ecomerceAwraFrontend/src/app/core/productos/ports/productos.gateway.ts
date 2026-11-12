import { Observable } from 'rxjs';
import { ConsultaProductos, PaginaProductos, Producto } from '../models/producto.model';

export abstract class ProductosGateway {
  abstract listar(consulta?: ConsultaProductos): Observable<PaginaProductos>;
  abstract obtenerPorId(id: number): Observable<Producto | null>;
}
