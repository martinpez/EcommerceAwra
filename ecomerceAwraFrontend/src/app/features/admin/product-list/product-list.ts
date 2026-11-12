import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { mensajeDeError } from '../../../core/admin/api-error';
import { AdminGateway } from '../../../core/admin/ports/admin.gateway';
import { PanelMensaje } from '../panel-mensaje/panel-mensaje';

/** Estados por los que se puede filtrar. `ELIMINADO` no aparece: no existe. */
type EstadoFiltro = 'BORRADOR' | 'PUBLICADO' | 'OCULTO' | '';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink, PanelMensaje],
  selector: 'app-product-list',
  templateUrl: './product-list.html',
  styleUrl: './product-list.scss',
})
export class ProductList {
  private readonly admin = inject(AdminGateway);

  protected readonly estado = signal<EstadoFiltro>('');
  protected readonly busqueda = signal('');
  protected readonly categoriaId = signal<number | null>(null);
  protected readonly pagina = signal(0);

  protected readonly error = signal<string | null>(null);
  protected readonly aviso = signal<string | null>(null);

  protected readonly categorias = rxResource({
    stream: () => this.admin.listarCategorias(),
  });

  protected readonly productos = rxResource({
    params: () => ({
      estado: this.estado(),
      categoriaId: this.categoriaId(),
      q: this.busqueda().trim(),
      page: this.pagina(),
      size: 20,
    }),
    stream: ({ params }) => this.admin.listarProductos(params),
  });

  protected readonly totalPaginas = computed(() => this.productos.value()?.page.totalPages ?? 0);

  private readonly copFormatter = new Intl.NumberFormat('es-CO', {
    style: 'currency',
    currency: 'COP',
    maximumFractionDigits: 0,
  });

  protected precio(valor: number): string {
    return this.copFormatter.format(valor);
  }

  protected filtrarPorEstado(estado: EstadoFiltro): void {
    this.estado.set(estado);
    this.pagina.set(0);
  }

  protected filtrarPorCategoria(categoriaId: string): void {
    const valor = Number(categoriaId);
    this.categoriaId.set(categoriaId ? valor : null);
    this.pagina.set(0);
  }

  protected buscar(texto: string): void {
    this.busqueda.set(texto);
    this.pagina.set(0);
  }

  protected irAPagina(pagina: number): void {
    this.pagina.set(Math.max(0, pagina));
  }

  /**
   * Cambia el estado de publicación.
   *
   * Publicar y ocultar son el mismo botón con dos estados: el administrador
   * elige si la prenda se ve o no. Borrador es el estado inicial de todo
   * producto nuevo.
   */
  protected async cambiarEstado(
    id: number,
    estado: 'BORRADOR' | 'PUBLICADO' | 'OCULTO',
  ): Promise<void> {
    this.error.set(null);
    this.aviso.set(null);
    try {
      await firstValueFrom(this.admin.cambiarPublicacion(id, estado));
      this.aviso.set(
        estado === 'PUBLICADO'
          ? 'Producto publicado: ya aparece en la tienda.'
          : estado === 'OCULTO'
            ? 'Producto oculto: sigue guardado, pero no se ve.'
            : 'Producto vuelve a borrador.',
      );
      this.productos.reload();
    } catch (error) {
      this.error.set(mensajeDeError(error));
    }
  }

  protected async eliminar(id: number, nombre: string): Promise<void> {
    if (
      !confirm(`¿Eliminar "${nombre}"? Es un borrado lógico: no se puede recuperar desde el panel.`)
    ) {
      return;
    }
    this.error.set(null);
    this.aviso.set(null);
    try {
      await firstValueFrom(this.admin.eliminarProducto(id));
      this.aviso.set('Producto eliminado.');
      this.productos.reload();
    } catch (error) {
      this.error.set(mensajeDeError(error));
    }
  }
}
