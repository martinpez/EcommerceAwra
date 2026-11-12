import {
  AfterViewInit,
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  ElementRef,
  NgZone,
  inject,
  input,
  viewChild,
} from '@angular/core';
import gsap from 'gsap';
import { ScrollTrigger } from 'gsap/ScrollTrigger';
import { BRAND } from '../../../../core/brand/brand.config';
import { prefersReducedMotion } from '../../../../core/browser/media-queries';
import { SmoothScrollService } from '../../../../core/scroll/smooth-scroll.service';
import { MediaSitio } from '../../../../core/sitio/models/sitio.model';
import { AwraLogo } from '../../../../shared/ui/awra-logo/awra-logo';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [AwraLogo],
  selector: 'app-hero-section',
  templateUrl: './hero-section.html',
  styleUrl: './hero-section.scss',
})
export class HeroSection {
  private readonly zone = inject(NgZone);
  private readonly destroyRef = inject(DestroyRef);
  private readonly smoothScroll = inject(SmoothScrollService);
  private readonly section = viewChild.required<ElementRef<HTMLElement>>('section');
  private readonly logoComponent = viewChild.required(AwraLogo);

  /** Logo que el administrador subió. El componente usa uno de respaldo. */
  readonly logo = input<MediaSitio | null>(null);

  protected readonly brand = BRAND;

  ngAfterViewInit(): void {
    if (prefersReducedMotion()) {
      return;
    }

    const section = this.section().nativeElement;
    const logo = this.logoComponent();

    gsap.registerPlugin(ScrollTrigger);

    this.zone.runOutsideAngular(() => {
      const pin = ScrollTrigger.create({
        trigger: section,
        start: 'top top',
        end: () => `+=${window.innerHeight}`,
        pin: true,
        invalidateOnRefresh: true,
        onUpdate: (self) => logo.sweepTo(self.progress),
      });

      this.destroyRef.onDestroy(() => pin.kill());
    });
  }
}
