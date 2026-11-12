import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';

/**
 * Indicador de secciones de la Home.
 *
 * Una marca por sección, sin texto: solo dice por dónde vas. La marca de la
 * sección activa se ilumina; las demás quedan apagadas.
 *
 * Las marcas son rectángulos pequeños, no puntos: pegadas al borde derecho
 * funcionan como una guia vertical y no distraen como una fila de puntos.
 */
@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-section-indicator',
  templateUrl: './section-indicator.html',
  styleUrl: './section-indicator.scss',
})
export class SectionIndicator {
  /** Claves de las secciones, en el orden en que aparecen. */
  readonly secciones = input.required<readonly string[]>();

  /** Clave de la sección que se está viendo. */
  readonly activa = input<string | null>(null);

  protected readonly indices = computed(() =>
    this.secciones().map((clave, indice) => ({ clave, indice })),
  );
}
