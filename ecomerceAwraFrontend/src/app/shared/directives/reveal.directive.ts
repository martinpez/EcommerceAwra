import { DestroyRef, Directive, ElementRef, OnInit, inject } from '@angular/core';
import gsap from 'gsap';
import { ScrollTrigger } from 'gsap/ScrollTrigger';
import { prefersReducedMotion } from '../../core/browser/media-queries';
import { corregirPosicionesDeSeccion } from '../../core/scroll/posiciones-trigger';

/**
 * Revela el texto de una sección mientras el visitante baja.
 *
 * Los elementos que se animan son los que llevan `data-reveal`. La animación
 * va atada al scroll (no a un temporizador): al ritmo al que bajas, el texto
 * aparece. Si sales de la sección y vuelves, se revierte.
 *
 * Las posiciones del trigger se corrigen a mano: ver `corregirPosicionesDeSeccion`.
 */
@Directive({
  selector: '[appReveal]',
})
export class Reveal implements OnInit {
  private readonly element = inject<ElementRef<HTMLElement>>(ElementRef);
  private readonly destroyRef = inject(DestroyRef);

  ngOnInit(): void {
    if (prefersReducedMotion()) {
      return;
    }

    const contenedor = this.element.nativeElement;
    const hijos = contenedor.querySelectorAll<HTMLElement>('[data-reveal]');

    if (!hijos.length) {
      return;
    }

    const contexto = gsap.context(() => {
      gsap.set(hijos, { opacity: 0, y: 28 });

      ScrollTrigger.create({
        trigger: contenedor,
        start: 'top 88%',
        end: 'top 45%',
        scrub: true,
        animation: gsap.to(hijos, {
          opacity: 1,
          y: 0,
          duration: 1,
          // El escalonado reparte el ritmo sin que la ultima letra se quede
          // sin animacion.
          stagger: Math.min(0.15, 0.8 / hijos.length),
          ease: 'none',
        }),
        onRefresh: (self) => corregirPosicionesDeSeccion(self, contenedor),
      });
    });

    this.destroyRef.onDestroy(() => contexto.revert());
  }
}
