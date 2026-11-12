import {
  AfterViewInit,
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  ElementRef,
  NgZone,
  inject,
  viewChild,
} from '@angular/core';
import gsap from 'gsap';
import { hasFinePointer, prefersReducedMotion } from '../../../core/browser/media-queries';
import { StarMark } from '../star-mark/star-mark';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [StarMark],
  selector: 'app-cursor-star',
  templateUrl: './cursor-star.html',
  styleUrl: './cursor-star.scss',
})
export class CursorStar implements AfterViewInit {
  private readonly zone = inject(NgZone);
  private readonly destroyRef = inject(DestroyRef);
  private readonly cursor = viewChild.required<ElementRef<HTMLElement>>('cursor');

  ngAfterViewInit(): void {
    if (!hasFinePointer()) {
      return;
    }

    const element = this.cursor().nativeElement;
    const duration = prefersReducedMotion() ? 0 : 0.35;
    const moveX = gsap.quickTo(element, 'x', { duration, ease: 'power3.out' });
    const moveY = gsap.quickTo(element, 'y', { duration, ease: 'power3.out' });

    const onPointerMove = (event: PointerEvent) => {
      moveX(event.clientX);
      moveY(event.clientY);
    };

    const onPointerOver = (event: PointerEvent) => {
      const target = event.target;
      const isInteractive = target instanceof Element && target.closest('a, button') !== null;
      element.classList.toggle('is-active', isInteractive);
    };

    document.documentElement.classList.add('has-custom-cursor');

    this.zone.runOutsideAngular(() => {
      window.addEventListener('pointermove', onPointerMove, { passive: true });
      document.addEventListener('pointerover', onPointerOver);
    });

    this.destroyRef.onDestroy(() => {
      window.removeEventListener('pointermove', onPointerMove);
      document.removeEventListener('pointerover', onPointerOver);
      document.documentElement.classList.remove('has-custom-cursor');
    });
  }
}
