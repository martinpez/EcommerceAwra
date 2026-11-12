import {
  AfterViewInit,
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  ElementRef,
  computed,
  inject,
  input,
} from '@angular/core';
import gsap from 'gsap';
import { ScrollTrigger } from 'gsap/ScrollTrigger';
import { prefersReducedMotion } from '../../../core/browser/media-queries';
import { corregirPosicionesDeSeccion } from '../../../core/scroll/posiciones-trigger';

/**
 * Titulo partido en letras, con animacion atada al scroll.
 *
 * Hay dos variantes:
 *
 * - **loader**: las letras suben desde debajo, tapadas por su propia mascara.
 *   Es la misma animacion del arranque de la web (las letras de AWRA).
 * - **escalera**: cada letra aparece desde mas abajo que la anterior, de modo
 *   que durante la animacion forman una escalera descendente que se nivela.
 *
 * Las letras se agrupan por palabra: cada letra es una caja independiente y
 * el navegador podria cortar la linea entre dos de ellas, partiendo
 * palabras a la mitad. Al meter cada palabra en su propia caja, el corte
 * solo puede ocurrir entre palabras.
 */
@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-letter-title',
  templateUrl: './letter-title.html',
  styleUrl: './letter-title.scss',
})
export class LetterTitle implements AfterViewInit {
  readonly text = input.required<string>();
  readonly variante = input<'loader' | 'escalera'>('loader');

  /** El texto partido en palabras y cada palabra en letras. */
  protected readonly palabras = computed(() =>
    this.text()
      .split(' ')
      .filter((palabra) => palabra.length > 0)
      .map((palabra) => Array.from(palabra)),
  );

  private readonly element = inject<ElementRef<HTMLElement>>(ElementRef);
  private readonly destroyRef = inject(DestroyRef);

  /**
   * Se prepara en `ngAfterViewInit` y no en `ngOnInit`: en `ngOnInit` la
   * plantilla del componente todavia no existe, asi que no habria ninguna
   * letra que animar.
   */
  ngAfterViewInit(): void {
    if (prefersReducedMotion()) {
      return;
    }

    const contenedor = this.element.nativeElement;
    const letras = contenedor.querySelectorAll<HTMLElement>('.letra');

    if (!letras.length) {
      return;
    }

    // El escalonado reparte el ritmo: con muchas letras el retardo entre
    // ellas tiene que ser pequeno para que la ultima llegue a tiempo.
    const escalonado = Math.min(0.12, 0.8 / letras.length);

    const contexto = gsap.context(() => {
      if (this.variante() === 'loader') {
        // Desde debajo y tapadas: solo se ve subir.
        gsap.set(letras, { yPercent: 110, opacity: 1 });
        ScrollTrigger.create({
          trigger: contenedor,
          start: 'top 88%',
          end: 'top 45%',
          scrub: true,
          animation: gsap.to(letras, {
            yPercent: 0,
            duration: 1,
            stagger: escalonado,
            ease: 'none',
          }),
          onRefresh: (self) => corregirPosicionesDeSeccion(self, contenedor),
        });
        return;
      }

      // Escalera: cada letra arranca mas abajo que la anterior.
      //
      // El desnivel esta acotado: con un titulo largo (38 letras) una
      // escalera sin limite haria que la ultima letra cayera 300 px mas
      // abajo y pasara por encima del texto que viene despues.
      const total = Math.max(1, letras.length - 1);
      gsap.set(letras, { y: (indice) => 16 + (indice / total) * 44, opacity: 0 });
      ScrollTrigger.create({
        trigger: contenedor,
        start: 'top 88%',
        end: 'top 45%',
        scrub: true,
        animation: gsap.to(letras, {
          y: 0,
          opacity: 1,
          duration: 1,
          stagger: escalonado,
          ease: 'none',
        }),
        onRefresh: (self) => corregirPosicionesDeSeccion(self, contenedor),
      });
    });

    this.destroyRef.onDestroy(() => contexto.revert());
  }
}
