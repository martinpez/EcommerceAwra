import { DestroyRef, Injectable, NgZone, inject } from '@angular/core';
import gsap from 'gsap';
import { ScrollTrigger } from 'gsap/ScrollTrigger';
import Lenis from 'lenis';

@Injectable({ providedIn: 'root' })
export class SmoothScrollService {
  private readonly zone = inject(NgZone);
  private lenis: Lenis | null = null;

  private readonly tick = (time: number) => {
    this.lenis?.raf(time * 1000);
  };

  constructor() {
    inject(DestroyRef).onDestroy(() => this.destroy());
  }

  start(): void {
    if (this.lenis) {
      return;
    }

    history.scrollRestoration = 'manual';

    this.zone.runOutsideAngular(() => {
      gsap.registerPlugin(ScrollTrigger);

      this.lenis = new Lenis({
        autoRaf: false,
        lerp: 0.1,
        anchors: true,
        respectReducedMotion: true,
      });

      this.lenis.on('scroll', ScrollTrigger.update);
      gsap.ticker.add(this.tick);
      // Sin suavizado de retraso, un frame lento se convierte en un tirón
      // visible: la posición salta de golpe en vez de continuar.
      gsap.ticker.lagSmoothing(1000, 16);
    });
  }

  scrollToTop(): void {
    window.scrollTo(0, 0);
    this.lenis?.scrollTo(0, { immediate: true, force: true });
  }

  pause(): void {
    this.lenis?.stop();
  }

  resume(): void {
    this.lenis?.start();
  }

  destroy(): void {
    gsap.ticker.remove(this.tick);
    this.lenis?.destroy();
    this.lenis = null;
  }
}
