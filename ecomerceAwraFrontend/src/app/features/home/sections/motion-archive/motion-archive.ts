import {
  AfterViewInit,
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  OnDestroy,
  effect,
  input,
  signal,
  viewChild,
} from '@angular/core';
import { MotionMedia } from '../../../../core/media/asset-catalog';
import { Reveal } from '../../../../shared/directives/reveal.directive';
import { LetterTitle } from '../../../../shared/ui/letter-title/letter-title';

/** Fracción del video visible a partir de la cual empieza a sonar. */
const VISIBLE_THRESHOLD = 0.35;

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [LetterTitle, Reveal],
  selector: 'app-motion-archive',
  templateUrl: './motion-archive.html',
  styleUrl: './motion-archive.scss',
})
export class MotionArchive implements AfterViewInit, OnDestroy {
  readonly media = input.required<readonly MotionMedia[]>();

  /** Textos de la sección. Si no llegan, se usan los de respaldo del código. */
  readonly eyebrow = input('');
  readonly title = input('');
  readonly description = input('');

  protected readonly activeIndex = signal(0);

  /**
   * Los videos arrancan silenciados: los navegadores bloquean el audio
   * automático. El usuario lo activa con un click.
   */
  protected readonly muted = signal(true);

  private readonly video = viewChild<ElementRef<HTMLVideoElement>>('video');
  private observer: IntersectionObserver | null = null;

  /**
   * Campo plano (no señal) a propósito: si fuera una señal, cualquier efecto
   * que lo leyera se dispararía en cada cambio de visibilidad y reiniciaría
   * el video.
   */
  private visible = false;

  constructor() {
    effect(() => {
      // Al cambiar de video hay que volver a arrancar la reproducción. Este
      // `play()` llega antes de que Angular actualice el atributo `src` (el
      // navegador aborta la petición en curso), así que el arranque real lo
      // hace `onReady()`, cuando ya hay metadatos del video nuevo.
      this.activeIndex();
      this.syncPlayback();
    });
  }

  ngAfterViewInit(): void {
    const element = this.video()?.nativeElement;
    if (!element) {
      return;
    }

    this.observer = new IntersectionObserver(
      (entries) => {
        this.visible = entries[0]?.isIntersecting ?? false;
        this.syncPlayback();
      },
      { threshold: VISIBLE_THRESHOLD },
    );
    this.observer.observe(element);
  }

  ngOnDestroy(): void {
    this.observer?.disconnect();
    this.observer = null;
  }

  protected next(): void {
    this.activeIndex.update((index) => (index + 1) % this.media().length);
  }

  protected previous(): void {
    this.activeIndex.update((index) => (index - 1 + this.media().length) % this.media().length);
  }

  protected select(index: number): void {
    this.activeIndex.set(index);
  }

  /** Un click en el video alterna entre silencio y sonido. */
  protected toggleMute(): void {
    this.muted.update((value) => !value);
    this.syncPlayback();
  }

  /**
   * El video ya tiene datos: se puede arrancar la reproducción. Al cambiar de
   * fuente el navegador reinicia la carga y este evento llega cuando ya hay
   * algo que reproducir.
   */
  protected onReady(): void {
    this.syncPlayback();
  }

  /**
   * El video solo reproduce mientras la sección está en pantalla. Fuera de ella
   * se pausa para no gastar batería ni ancho de banda.
   */
  private syncPlayback(): void {
    const video = this.video()?.nativeElement;
    if (!video) {
      return;
    }

    if (this.visible) {
      void video.play().catch(() => undefined);
    } else {
      video.pause();
    }
  }
}
