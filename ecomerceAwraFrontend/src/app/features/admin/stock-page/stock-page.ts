import { DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { Observable, firstValueFrom, of } from 'rxjs';
import { mensajeDeError } from '../../../core/admin/api-error';
import { AdminGateway } from '../../../core/admin/ports/admin.gateway';
import { PanelMensaje } from '../panel-mensaje/panel-mensaje';
import { MovimientoNuevo } from '../../../core/admin/ports/admin.gateway';
import { Movimiento, Producto } from '../../../core/admin/models/admin.model';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [DatePipe, FormsModule, PanelMensaje],
  selector: 'app-stock-page',
  templateUrl: './stock-page.html',
  styleUrl: './stock-page.scss',
})
export class StockPage {
  private readonly admin = inject(AdminGateway);

  protected readonly productoId = signal<number | null>(null);
  protected readonly error = signal<string | null>(null);
  protected readonly aviso = signal<string | null>(null);
  protected readonly varianteAbierta = signal<number | null>(null);

  protected readonly tipoMovimiento = signal<MovimientoNuevo['tipo']>('ENTRADA');
  protected readonly cantidad = signal(1);
  protected readonly motivo = signal('');

  protected readonly alertas = rxResource({
    stream: () => this.admin.listarAlertas(false),
  });

  protected readonly inventario = rxResource({
    stream: () => this.admin.listarInventario(0, 100),
  });

  /** Producto elegido: sus variantes son las que tienen stock propio. */
  protected readonly producto = rxResource<Producto | null, number | null>({
    params: () => this.productoId(),
    stream: ({ params }): Observable<Producto | null> =>
      params === null ? of(null) : this.admin.obtenerProducto(params),
  });

  /** Historial de la variante abierta. */
  protected readonly movimientos = rxResource<Movimiento[], number | null>({
    params: () => this.varianteAbierta(),
    stream: ({ params }): Observable<Movimiento[]> =>
      params === null ? of([]) : this.admin.listarMovimientos(params),
  });

  protected readonly alertasSinLeer = computed(
    () => (this.alertas.value() ?? []).filter((alerta) => !alerta.leida).length,
  );

  protected elegirProducto(id: string): void {
    this.productoId.set(id ? Number(id) : null);
  }

  protected abrirVariante(varianteId: number): void {
    this.varianteAbierta.set(varianteId);
    this.movimientos.reload();
  }

  protected cerrarVariante(): void {
    this.varianteAbierta.set(null);
  }

  protected async registrarMovimiento(varianteId: number): Promise<void> {
    this.error.set(null);
    this.aviso.set(null);
    try {
      await firstValueFrom(
        this.admin.registrarMovimiento(varianteId, {
          tipo: this.tipoMovimiento(),
          cantidad: this.cantidad(),
          motivo: this.motivo().trim(),
        }),
      );
      this.aviso.set('Movimiento registrado.');
      this.motivo.set('');
      this.producto.reload();
      this.inventario.reload();
      this.alertas.reload();
      this.movimientos.reload();
    } catch (error) {
      this.error.set(mensajeDeError(error));
    }
  }

  protected async marcarLeida(alertaId: number): Promise<void> {
    this.error.set(null);
    try {
      await firstValueFrom(this.admin.marcarAlertaLeida(alertaId));
      this.alertas.reload();
    } catch (error) {
      this.error.set(mensajeDeError(error));
    }
  }
}
