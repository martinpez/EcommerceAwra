import {
  AfterViewInit,
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  ElementRef,
  NgZone,
  computed,
  inject,
  input,
  viewChild,
} from '@angular/core';
import gsap from 'gsap';
import { BRAND_ASSETS } from '../../../core/media/asset-catalog';
import { prefersReducedMotion } from '../../../core/browser/media-queries';
import { MediaSitio } from '../../../core/sitio/models/sitio.model';

const POINTER_RADIUS = 200;
const SWEEP_RADIUS = 260;
const SWEEP_DURATION = 1.6;
const SWEEP_FADE = 0.4;
const SWEEP_REPEAT_DELAY = 2;
const IDLE_DELAY_MS = 1500;
const RESUME_DELAY_MS = 600;
const SCRUB_RADIUS = 240;

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-awra-logo',
  templateUrl: './awra-logo.html',
  styleUrl: './awra-logo.scss',
  host: {
    '[class.is-static]': 'reducedMotion',
  },
})
export class AwraLogo implements AfterViewInit {
  private readonly zone = inject(NgZone);
  private readonly destroyRef = inject(DestroyRef);
  private readonly chromeRef = viewChild.required<ElementRef<HTMLImageElement>>('chrome');

  /**
   * El logo que el administrador subió.
   *
   * Si todavia no llego de la API se usa el de respaldo, que esta en el
   * catalogo de medios: asi el hero se pinta de inmediato y, si la API no
   * responde, la web no se queda con el hueco.
   */
  readonly logo = input<MediaSitio | null>(null);

  protected readonly reducedMotion = prefersReducedMotion();

  protected readonly logoSrc = computed(() => this.logo()?.url ?? BRAND_ASSETS.logo);
  protected readonly logoWidth = computed(() => this.logo()?.ancho ?? BRAND_ASSETS.logoWidth);
  protected readonly logoHeight = computed(() => this.logo()?.alto ?? BRAND_ASSETS.logoHeight);

  private chrome: HTMLImageElement | null = null;
  private readonly light = { x: 0, y: 0, radius: 0 };
  private sweep: gsap.core.Timeline | null = null;
  private idleTimer: number | undefined;
  private scrubbing = false;
  private lastProgress = 0;

  ngAfterViewInit(): void {
    if (this.reducedMotion) {
      return;
    }

    this.chrome = this.chromeRef().nativeElement;

    const onPointerMove = (event: PointerEvent) => {
      if (this.scrubbing || !this.chrome) {
        return;
      }

      const rect = this.chrome.getBoundingClientRect();

      this.stopSweep();
      this.scheduleSweep(IDLE_DELAY_MS);

      gsap.to(this.light, {
        x: event.clientX - rect.left,
        y: event.clientY - rect.top,
        duration: 0.6,
        ease: 'power3.out',
        overwrite: 'auto',
        onUpdate: this.paint,
      });

      if (Math.abs(this.light.radius - POINTER_RADIUS) > 0.5) {
        gsap.to(this.light, {
          radius: POINTER_RADIUS,
          duration: 0.4,
          ease: 'power3.out',
          overwrite: 'auto',
          onUpdate: this.paint,
        });
      }
    };

    const onLeaveWindow = (event: MouseEvent) => {
      if (event.relatedTarget !== null || this.scrubbing) {
        return;
      }

      this.stopSweep();
      gsap.to(this.light, {
        radius: 0,
        duration: 0.5,
        ease: 'power3.out',
        overwrite: 'auto',
        onUpdate: this.paint,
      });
      this.scheduleSweep(RESUME_DELAY_MS);
    };

    this.zone.runOutsideAngular(() => {
      window.addEventListener('pointermove', onPointerMove, { passive: true });
      document.addEventListener('mouseout', onLeaveWindow);
      this.scheduleSweep(0);
    });

    this.destroyRef.onDestroy(() => {
      window.removeEventListener('pointermove', onPointerMove);
      document.removeEventListener('mouseout', onLeaveWindow);
      window.clearTimeout(this.idleTimer);
      this.stopSweep();
      gsap.killTweensOf(this.light);
    });
  }

  sweepTo(progress: number): void {
    if (!this.chrome || this.reducedMotion) {
      return;
    }

    const p = progress < 0.001 ? 0 : Math.min(progress, 1);
    if (p === this.lastProgress) {
      return;
    }
    this.lastProgress = p;

    if (p === 0) {
      this.scrubbing = false;
      this.chrome.classList.remove('is-lit');
      this.light.radius = 0;
      this.paint();
      this.scheduleSweep(RESUME_DELAY_MS);
      return;
    }

    this.scrubbing = true;
    this.stopSweep();
    gsap.killTweensOf(this.light);

    const width = this.chrome.clientWidth;
    const height = this.chrome.clientHeight;

    this.light.x = -SCRUB_RADIUS + p * (width + 2 * SCRUB_RADIUS);
    this.light.y = height / 2;
    this.light.radius = SCRUB_RADIUS;
    this.chrome.classList.toggle('is-lit', p >= 1);
    this.paint();
  }

  private readonly paint = (): void => {
    if (!this.chrome) {
      return;
    }

    this.chrome.style.setProperty('--light-x', `${this.light.x}px`);
    this.chrome.style.setProperty('--light-y', `${this.light.y}px`);
    this.chrome.style.setProperty('--light-r', `${this.light.radius}px`);
  };

  private startSweep(): void {
    if (this.scrubbing || !this.chrome) {
      return;
    }

    this.stopSweep();

    const width = this.chrome.clientWidth;
    const height = this.chrome.clientHeight;

    this.sweep = gsap
      .timeline({ repeat: -1, repeatDelay: SWEEP_REPEAT_DELAY, onUpdate: this.paint })
      .fromTo(
        this.light,
        { x: 0, y: 0, radius: 0 },
        {
          x: width,
          y: height,
          radius: SWEEP_RADIUS,
          duration: SWEEP_DURATION,
          ease: 'sine.inOut',
        },
      )
      .to(this.light, { radius: 0, duration: SWEEP_FADE, ease: 'sine.in' });
  }

  private stopSweep(): void {
    this.sweep?.kill();
    this.sweep = null;
  }

  private scheduleSweep(delay: number): void {
    window.clearTimeout(this.idleTimer);
    this.idleTimer = window.setTimeout(() => this.startSweep(), delay);
  }
}
