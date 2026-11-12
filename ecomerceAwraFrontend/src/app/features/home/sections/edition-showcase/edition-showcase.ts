import {
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  input,
  viewChild,
  viewChildren,
} from '@angular/core';
import { RouterLink } from '@angular/router';
import gsap from 'gsap';
import { EdicionHome } from '../../../../core/sitio/models/sitio.model';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink],
  selector: 'app-edition-showcase',
  templateUrl: './edition-showcase.html',
  styleUrl: './edition-showcase.scss',
})
export class EditionShowcase {
  readonly edition = input.required<EdicionHome>();
  readonly reverse = input(false);

  private readonly article = viewChild.required<ElementRef<HTMLElement>>('article');
  private readonly front = viewChild.required<ElementRef<HTMLImageElement>>('front');
  private readonly copyItems = viewChildren<ElementRef<HTMLElement>>('copyItem');

  /**
   * Registra las animaciones de esta edición dentro de la línea de tiempo global.
   *
   * - `at`: posición (en unidades de la línea de tiempo) donde empieza el bloque.
   * - El texto entra desplazándose de izquierda a derecha.
   * - La imagen frontal hace crossfade sobre la trasera.
   */
  addToTimeline(timeline: gsap.core.Timeline, at: number): void {
    const front = this.front().nativeElement;
    const copy = this.copyItems().map((item) => item.nativeElement);

    gsap.set(front, { opacity: 0 });
    gsap.set(copy, { opacity: 0, x: -56, y: 18 });

    // Entrada de los textos: se revelan mientras bajas, no de golpe.
    //
    // La línea de tiempo va con `scrub`, así que la duración se traduce en
    // scroll: con 0.5 el texto tarda media pantalla en aparecer, y el escalonado
    // reparte el ritmo entre el eyebrow, el título, la descripción y el enlace.
    timeline.fromTo(
      copy,
      { opacity: 0, x: -56, y: 18 },
      {
        opacity: 1,
        x: 0,
        y: 0,
        duration: 0.5,
        ease: 'none',
        stagger: 0.08,
      },
      at,
    );

    // Crossfade de la vista trasera a la frontal.
    timeline.fromTo(front, { opacity: 0 }, { opacity: 1, duration: 0.79, ease: 'none' }, at + 0.06);
  }

  /** Desplaza la edición hacia arriba para dar paso a la siguiente. */
  slideOut(timeline: gsap.core.Timeline, at: number, distance: () => number): void {
    timeline.to(
      this.article().nativeElement,
      {
        y: () => -distance(),
        duration: 0.15,
        ease: 'power2.inOut',
      },
      at,
    );
  }

  /** Entra la edición desde abajo del escenario. */
  slideIn(timeline: gsap.core.Timeline, at: number, distance: () => number): void {
    const article = this.article().nativeElement;

    gsap.set(article, { y: () => distance() });
    timeline.to(article, { y: 0, duration: 0.15, ease: 'power2.inOut' }, at);
  }
}
