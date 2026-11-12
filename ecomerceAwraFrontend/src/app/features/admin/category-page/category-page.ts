import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { firstValueFrom } from 'rxjs';
import { mensajeDeError } from '../../../core/admin/api-error';
import { AdminGateway } from '../../../core/admin/ports/admin.gateway';
import { PanelMensaje } from '../panel-mensaje/panel-mensaje';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [FormsModule, PanelMensaje],
  selector: 'app-category-page',
  templateUrl: './category-page.html',
  styleUrl: './category-page.scss',
})
export class CategoryPage {
  private readonly admin = inject(AdminGateway);

  protected readonly error = signal<string | null>(null);
  protected readonly aviso = signal<string | null>(null);
  protected readonly nuevaNombre = signal('');
  protected readonly editandoId = signal<number | null>(null);
  protected readonly editandoNombre = signal('');

  protected readonly categorias = rxResource({
    stream: () => this.admin.listarCategorias(),
  });

  protected async crear(): Promise<void> {
    const nombre = this.nuevaNombre().trim();
    if (!nombre) {
      return;
    }
    this.error.set(null);
    try {
      await firstValueFrom(this.admin.crearCategoria(nombre));
      this.nuevaNombre.set('');
      this.aviso.set('Categoría creada.');
      this.categorias.reload();
    } catch (error) {
      this.error.set(mensajeDeError(error));
    }
  }

  protected editar(id: number, nombre: string): void {
    this.editandoId.set(id);
    this.editandoNombre.set(nombre);
  }

  protected cancelarEdicion(): void {
    this.editandoId.set(null);
  }

  protected async guardarEdicion(id: number): Promise<void> {
    const nombre = this.editandoNombre().trim();
    if (!nombre) {
      return;
    }
    this.error.set(null);
    try {
      await firstValueFrom(this.admin.actualizarCategoria(id, nombre));
      this.editandoId.set(null);
      this.aviso.set('Categoría actualizada.');
      this.categorias.reload();
    } catch (error) {
      this.error.set(mensajeDeError(error));
    }
  }

  protected async eliminar(id: number, nombre: string): Promise<void> {
    if (!confirm(`¿Eliminar la categoría "${nombre}"?`)) {
      return;
    }
    this.error.set(null);
    try {
      await firstValueFrom(this.admin.eliminarCategoria(id));
      this.aviso.set('Categoría eliminada.');
      this.categorias.reload();
    } catch (error) {
      this.error.set(mensajeDeError(error));
    }
  }
}
