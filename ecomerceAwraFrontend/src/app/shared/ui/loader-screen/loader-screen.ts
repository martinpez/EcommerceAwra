import {
  AfterViewInit,
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  NgZone,
  inject,
  signal,
  viewChild,
} from '@angular/core';
import gsap from 'gsap';
import { BRAND } from '../../../core/brand/brand.config';
import { prefersReducedMotion } from '../../../core/browser/media-queries';
import { SmoothScrollService } from '../../../core/scroll/smooth-scroll.service';
import { StarMark } from '../star-mark/star-mark';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [StarMark],
  selector: 'app-loader-screen',
  templateUrl: './loader-screen.html',
  styleUrl: './loader-screen.scss',
})
export class LoaderScreen implements AfterViewInit {
  private readonly zone = inject(NgZone);
  private readonly smoothScroll = inject(SmoothScrollService);
  private readonly overlay = viewChild.required<ElementRef<HTMLElement>>('overlay');
  private readonly star = viewChild.required<ElementRef<HTMLElement>>('star');

  protected readonly brand = BRAND;
  protected readonly letters = BRAND.name.split('');
  protected readonly visible = signal(true);

  ngAfterViewInit(): void {
    // En el panel no hay intro: es una herramienta de trabajo y tres
    // segundos de animacion solo estorban.
    //
    // Se mira la URL del navegador y no la del router: al arrancar, la
    // navegacion inicial todavia no ha resuelto y `router.url` va vacio.
    if (window.location.pathname.startsWith('/admin')) {
      this.finish();
      return;
    }

    document.documentElement.classList.add('is-loading');
    this.smoothScroll.scrollToTop();
    this.smoothScroll.pause();

    const overlay = this.overlay().nativeElement;
    const star = this.star().nativeElement;
    const letters = overlay.querySelectorAll('.loader__letter');
    const slogan = overlay.querySelector('.loader__slogan');

    this.zone.runOutsideAngular(() => {
      const timeline = gsap.timeline({ onComplete: () => this.finish() });

      if (prefersReducedMotion()) {
        timeline.to(overlay, { opacity: 0, duration: 0.3, delay: 0.6 });
        return;
      }

      timeline
        .fromTo(
          star,
          { scale: 0.2, rotation: -90, opacity: 0 },
          { scale: 1, rotation: 0, opacity: 1, duration: 1, ease: 'expo.out' },
        )
        .fromTo(
          letters,
          { yPercent: 110, opacity: 0 },
          { yPercent: 0, opacity: 1, duration: 0.7, ease: 'power4.out', stagger: 0.07 },
          '-=0.5',
        )
        .fromTo(
          slogan,
          { opacity: 0, y: 8 },
          { opacity: 1, y: 0, duration: 0.6, ease: 'power2.out' },
          '-=0.3',
        )
        .to(overlay, { opacity: 0, duration: 0.7, ease: 'power2.inOut' }, '+=0.7');
    });
  }

  private finish(): void {
    document.documentElement.classList.remove('is-loading');
    this.smoothScroll.scrollToTop();
    this.smoothScroll.resume();
    this.zone.run(() => this.visible.set(false));
  }
}
