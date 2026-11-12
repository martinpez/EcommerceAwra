import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  ElementRef,
  Injector,
  NgZone,
  afterNextRender,
  computed,
  effect,
  inject,
  signal,
  viewChild,
  viewChildren,
} from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import gsap from 'gsap';
import { ScrollTrigger } from 'gsap/ScrollTrigger';
import { BRAND } from '../../../core/brand/brand.config';
import { prefersReducedMotion } from '../../../core/browser/media-queries';
import { MotionMedia } from '../../../core/media/asset-catalog';
import { SmoothScrollService } from '../../../core/scroll/smooth-scroll.service';
import {
  EdicionHome,
  Empresa,
  MediaSitio,
  SeccionSitio,
  Sitio,
} from '../../../core/sitio/models/sitio.model';
import { SitioGateway } from '../../../core/sitio/ports/sitio.gateway';
import { AboutSection } from '../sections/about-section/about-section';
import { ContactSection } from '../sections/contact-section/contact-section';
import { SectionIndicator } from '../sections/section-indicator/section-indicator';
import { EditionShowcase } from '../sections/edition-showcase/edition-showcase';
import { HeroSection } from '../sections/hero-section/hero-section';
import { MotionArchive } from '../sections/motion-archive/motion-archive';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    AboutSection,
    ContactSection,
    SectionIndicator,
    EditionShowcase,
    HeroSection,
    MotionArchive,
  ],
  selector: 'app-home-page',
  styleUrl: './home-page.scss',
  templateUrl: './home-page.html',
})
export class HomePage {
  private readonly zone = inject(NgZone);
  private readonly destroyRef = inject(DestroyRef);
  private readonly injector = inject(Injector);
  private readonly smoothScroll = inject(SmoothScrollService);
  private readonly gateway = inject(SitioGateway);

  protected readonly brand = BRAND;

  /**
   * El contenido del sitio y las ediciones se piden a la API.
   *
   * `rxResource` da cuatro cosas de un golpe: el valor, si esta cargando, el
   * error y si ya tiene valor. Con eso la Home puede pintar el esqueleto
   * mientras carga y avisar si la API no responde.
   */
  protected readonly sitioResource = rxResource({
    stream: () => this.gateway.obtenerSitio(),
    debugName: 'sitio',
  });

  protected readonly edicionesResource = rxResource({
    stream: () => this.gateway.obtenerEdiciones(),
    debugName: 'ediciones',
  });

  protected readonly cargando = computed(
    () => this.sitioResource.isLoading() || this.edicionesResource.isLoading(),
  );

  protected readonly error = computed(
    () => this.sitioResource.error() ?? this.edicionesResource.error(),
  );

  private readonly sitioActual = computed<Sitio | null>(() => {
    const recurso = this.sitioResource;
    return recurso.hasValue() ? recurso.value() : null;
  });

  private readonly edicionesActual = computed(() => {
    const recurso = this.edicionesResource;
    return recurso.hasValue() ? (recurso.value() ?? []) : [];
  });

  protected readonly empresa = computed<Empresa | null>(() => this.sitioActual()?.empresa ?? null);

  /** El logo que el administrador subió. */
  protected readonly logo = computed<MediaSitio | null>(() => {
    const media = this.sitioActual()?.media ?? [];
    return media.find((item) => item.tipo === 'LOGO') ?? null;
  });

  protected readonly about = computed<SeccionSitio | null>(() => this.seccionDe('QUIENES_SOMOS'));

  protected readonly movimiento = computed<SeccionSitio | null>(() => this.seccionDe('MOVIMIENTO'));

  protected readonly contacto = computed<SeccionSitio | null>(() => this.seccionDe('CONTACTO'));

  /** Clave de la sección que se está viendo: ilumina su marca del indicador. */
  protected readonly seccionActiva = signal<string | null>(null);

  /**
   * Secciones de la página, leidas del DOM.
   *
   * Se piden con un atributo `data-seccion` en vez de una lista fija porque
   * algunas secciones aparecen y desaparecen: si el administrador desactiva
   * "En movimiento", esa sección (y su marca) desaparecen con ella.
   */
  private readonly seccionesDetectadas = signal<{ clave: string; elemento: HTMLElement }[]>([]);

  /** Secciones que existen ahora mismo, en orden: las del indicador lateral. */
  protected readonly clavesSecciones = computed(() =>
    this.seccionesDetectadas().map((seccion) => seccion.clave),
  );

  private pendienteDeEspia = false;

  /**
   * Marca la seccion por la que se esta bajando.
   *
   * Se busca la seccion cuyo centro esta mas cerca del centro de la ventana.
   * Con las secciones fijadas (el hero y las ediciones) el rectangulo se queda
   * quieto mientras dura el pin, asi que la seccion activa no cambia hasta
   * que pasas a la siguiente.
   */
  private readonly alDesplazar = (): void => {
    if (this.pendienteDeEspia) {
      return;
    }
    this.pendienteDeEspia = true;
    requestAnimationFrame(() => {
      this.pendienteDeEspia = false;
      this.calcularSeccionActiva();
    });
  };

  private calcularSeccionActiva(): void {
    const secciones = this.seccionesDetectadas();
    if (!secciones.length) {
      return;
    }
    const centro = window.innerHeight / 2;
    let activa = secciones[0];
    let mejorDistancia = Infinity;

    for (const seccion of secciones) {
      const rect = seccion.elemento.getBoundingClientRect();
      const distancia = Math.abs(rect.top + rect.height / 2 - centro);
      if (distancia < mejorDistancia) {
        mejorDistancia = distancia;
        activa = seccion;
      }
    }

    if (activa.clave !== this.seccionActiva()) {
      this.zone.run(() => this.seccionActiva.set(activa.clave));
    }
  }

  /** Secciones que el administrador dejó desactivadas: no se muestran ni en el menú. */
  protected readonly seccionesVisibles = computed(() => {
    const claves = new Set((this.sitioActual()?.secciones ?? []).map((seccion) => seccion.clave));
    return {
      about: !claves.size || claves.has('QUIENES_SOMOS'),
      motion: !claves.size || claves.has('MOVIMIENTO'),
      contact: !claves.size || claves.has('CONTACTO'),
    };
  });

  /**
   * Las ediciones de la Home, ya con la forma que pinta el componente.
   *
   * El enlace "Ver más detalles" apunta al producto, no a la edición: por eso
   * el id que se usa es el del producto.
   */
  protected readonly editions = computed<EdicionHome[]>(() =>
    this.edicionesActual().map((edicion) => ({
      id: edicion.productoId,
      eyebrow: edicion.eyebrow ?? '',
      title: edicion.titulo ?? '',
      description: edicion.descripcion ?? '',
      front: edicion.imagenFrente ?? '',
      back: edicion.imagenEspalda ?? '',
      etiqueta: edicion.etiqueta,
    })),
  );

  protected readonly motionMedia = computed<MotionMedia[]>(() =>
    (this.sitioActual()?.videos ?? []).map((video) => ({
      id: `video-${video.id}`,
      label: video.etiqueta ?? video.titulo,
      src: video.url,
      poster: video.posterUrl ?? '',
    })),
  );

  private readonly editionsStage = viewChild.required<ElementRef<HTMLElement>>('editionsStage');
  private readonly editionItems = viewChildren(EditionShowcase);

  private timeline: gsap.core.Timeline | null = null;

  constructor() {
    /**
     * La línea de tiempo se construye cuando ya hay ediciones en pantalla.
     *
     * Antes los datos venían del código y existían desde el primer momento.
     * Ahora llegan por HTTP: hay que esperar a que el `@for` del template
     * haya pintado los bloques para poder medirlos y fijar la sección.
     */
    effect(() => {
      const items = this.editionItems();
      const lista = this.editions();

      if (items.length === 0 || items.length !== lista.length) {
        return;
      }

      if (prefersReducedMotion()) {
        return;
      }

      // Se espera al siguiente render: crear el pin mientras Angular está
      // pintando el DOM puede provocar el error de "cambió después de
      // comprobarlo".
      afterNextRender(
        () => {
          this.destruirTimeline();
          this.timeline = this.zone.runOutsideAngular(() => this.construirTimeline(items));
          // El contenido es dinámico: hay que recalcular las alturas fijadas.
          ScrollTrigger.refresh();
        },
        { injector: this.injector },
      );
    });

    /**
     * Las secciones se detectan cuando el DOM cambia: algunas solo existen
     * si hay datos (las ediciones) o si el administrador las deja activas.
     */
    effect(() => {
      // `hasValue()` lee la señal interna del recurso: al resolverse, el
      // efecto vuelve a correr y las secciones se vuelven a leer.
      this.sitioResource.hasValue();
      this.edicionesResource.hasValue();
      afterNextRender(() => this.detectarSecciones(), { injector: this.injector });
    });

    this.zone.runOutsideAngular(() => {
      window.addEventListener('scroll', this.alDesplazar, { passive: true });
    });

    this.destroyRef.onDestroy(() => {
      window.removeEventListener('scroll', this.alDesplazar);
      this.destruirTimeline();
    });
  }

  /** Vuelve a leer las secciones del DOM y actualiza el indicador. */
  private detectarSecciones(): void {
    const elementos = Array.from(document.querySelectorAll<HTMLElement>('[data-seccion]'));
    this.seccionesDetectadas.set(
      elementos.map((elemento) => ({
        clave: elemento.getAttribute('data-seccion') ?? '',
        elemento,
      })),
    );
    this.calcularSeccionActiva();
  }

  /**
   * La sección de ediciones se maneja como un único bloque fijo con una sola
   * línea de tiempo. Así el usuario nunca ve un salto entre ediciones ni un
   * momento vacío: entra con la vista trasera y el crossfade ocurre mientras
   * la sección está completamente en pantalla.
   */
  private construirTimeline(editions: readonly EditionShowcase[]): gsap.core.Timeline {
    const stage = this.editionsStage().nativeElement;

    gsap.registerPlugin(ScrollTrigger);

    const timeline = gsap.timeline({
      defaults: { ease: 'none' },
      scrollTrigger: {
        trigger: stage,
        start: 'top top',
        end: () => `+=${window.innerHeight * editions.length}`,
        pin: true,
        scrub: true,
        invalidateOnRefresh: true,
        anticipatePin: 1,
      },
    });

    editions.forEach((edition, index) => {
      edition.addToTimeline(timeline, index);
    });

    // Transición vertical entre ediciones: una sale hacia arriba y la
    // siguiente entra desde abajo, sin huecos ni solapes.
    const distance = () => stage.clientHeight;

    for (let index = 0; index < editions.length - 1; index += 1) {
      const at = index + 0.85;
      editions[index].slideOut(timeline, at, distance);
      editions[index + 1].slideIn(timeline, at, distance);
    }

    return timeline;
  }

  private destruirTimeline(): void {
    this.timeline?.scrollTrigger?.kill();
    this.timeline?.kill();
    this.timeline = null;
  }

  private seccionDe(clave: string): SeccionSitio | null {
    const secciones = this.sitioActual()?.secciones ?? [];
    return secciones.find((seccion) => seccion.clave === clave) ?? null;
  }
}
