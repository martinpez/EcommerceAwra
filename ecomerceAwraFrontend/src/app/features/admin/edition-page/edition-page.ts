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
  selector: 'app-edition-page',
  templateUrl: './edition-page.html',
  styleUrl: './edition-page.scss',
})
export class EditionPage {
  private readonly admin = inject(AdminGateway);

  protected readonly error = signal<string | null>(null);
  protected readonly aviso = signal<string | null>(null);
  protected readonly editandoId = signal<number | null>(null);

  protected readonly formulario = signal({
    productoId: 0,
    eyebrow: '',
    titulo: '',
    descripcion: '',
    orden: 1,
    activa: true,
  });

  protected readonly ediciones = rxResource({
    stream: () => this.admin.listarEdiciones(),
  });

  /** Productos publicados y también los que no, para preparar la edición. */
  protected readonly productos = rxResource({
    stream: () => this.admin.listarProductos({ size: 100 }),
  });

  protected nueva(): void {
    this.editandoId.set(0);
    this.formulario.set({
      productoId: 0,
      eyebrow: 'Edición',
      titulo: '',
      descripcion: '',
      orden: (this.ediciones.value()?.length ?? 0) + 1,
      activa: true,
    });
  }

  protected editar(id: number): void {
    const edicion = (this.ediciones.value() ?? []).find((item) => item.id === id);
    if (!edicion) {
      return;
    }
    this.editandoId.set(id);
    this.formulario.set({
      productoId: edicion.productoId,
      eyebrow: edicion.eyebrow ?? '',
      titulo: edicion.titulo ?? '',
      descripcion: edicion.descripcion ?? '',
      orden: edicion.orden,
      activa: edicion.activa,
    });
  }

  protected cancelar(): void {
    this.editandoId.set(null);
  }

  protected campo(nombre: string, valor: string | number | boolean): void {
    this.formulario.update((formulario) => ({ ...formulario, [nombre]: valor }));
  }

  protected async guardar(): Promise<void> {
    this.error.set(null);
    this.aviso.set(null);
    const datos = this.formulario();

    if (!datos.productoId) {
      this.error.set('Elige el producto que aparece en la edición.');
      return;
    }

    const cuerpo = {
      productoId: Number(datos.productoId),
      eyebrow: datos.eyebrow,
      titulo: datos.titulo,
      descripcion: datos.descripcion,
      orden: Number(datos.orden) || 0,
      activa: datos.activa,
    };

    try {
      if (this.editandoId() === 0) {
        await firstValueFrom(this.admin.crearEdicion(cuerpo));
        this.aviso.set('Edición creada.');
      } else {
        await firstValueFrom(this.admin.actualizarEdicion(this.editandoId()!, cuerpo));
        this.aviso.set('Edición guardada.');
      }
      this.editandoId.set(null);
      this.ediciones.reload();
    } catch (error) {
      this.error.set(mensajeDeError(error));
    }
  }
}
