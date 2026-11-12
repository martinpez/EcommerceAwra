import { ChangeDetectionStrategy, Component, input } from '@angular/core';

/**
 * Mensajes de resultado del panel (error y confirmacion).
 *
 * Va en un componente propio porque necesita unaregion viva: la region
 * tiene que existir ANTES de que aparezca el texto, si no un lector de
 * pantalla no se entera de lo que acaba de pasar.
 */
@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-panel-mensaje',
  templateUrl: './panel-mensaje.html',
  styleUrl: './panel-mensaje.scss',
})
export class PanelMensaje {
  readonly error = input<string | null>(null);
  readonly aviso = input<string | null>(null);
}
