export type DisponibilidadProducto = 'DISPONIBLE' | 'SIN_STOCK';

export interface VarianteProducto {
  talla: string;
  color: string;
  /** La API pública no expone la cantidad de unidades: solo si se puede pedir. */
  disponible: boolean;
}

export interface ImagenProducto {
  url: string;
  principal: boolean;
  orden: number;
}

export interface Producto {
  id: number;
  nombre: string;
  descripcion: string;
  precio: number;
  categoriaId: number;
  categoriaNombre: string;
  disponibilidad: DisponibilidadProducto;
  /** Texto de la insignia ("Agotado"). Viene vacío cuando hay stock. */
  etiqueta: string;
  variantes: VarianteProducto[];
  imagenes: ImagenProducto[];
}

export interface PaginaProductos {
  content: Producto[];
  page: {
    size: number;
    number: number;
    totalElements: number;
    totalPages: number;
  };
}

export interface ConsultaProductos {
  categoriaId?: number;
  page?: number;
  size?: number;
}
