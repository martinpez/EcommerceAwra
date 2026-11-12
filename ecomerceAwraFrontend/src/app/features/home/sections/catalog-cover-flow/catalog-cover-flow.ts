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
  signal,
  viewChild,
} from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';
import { Producto } from '../../../../core/productos/models/producto.model';
import { ProductosGateway } from '../../../../core/productos/ports/productos.gateway';

/** Grados de giro sobre el eje Y por cada paso desde el centro. */
const ROTATE_DEG = 48;
/** Separación lateral por paso, en % del ancho de la tarjeta. */
const STEP_X = 85;
/** Profundidad por paso (hacia atrás), en rem. */
const DEPTH_REM = 7;
const SCALE_STEP = 0.12;
/** Más allá de esta distancia la tarjeta queda invisible. */
const MAX_DISTANCE = 2;
const OPACITY_STEPS = [1, 0.75, 0.3, 0];
/** Pixeles de arrastre acumulados antes de cambiar de tarjeta. */
const DRAG_STEP_PX = 90;
/** Pixeles de rueda (deltaX) acumulados antes de cambiar de tarjeta. */
const WHEEL_THRESHOLD = 60;

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink],
  selector: 'app-catalog-cover-flow',
  templateUrl: './catalog-cover-flow.html',
  styleUrl: './catalog-cover-flow.scss',
})
export class CatalogCoverFlow implements AfterViewInit {
  private readonly zone = inject(NgZone);
  private readonly destroyRef = inject(DestroyRef);
  private readonly gateway = inject(ProductosGateway);
  private readonly viewportRef = viewChild.required<ElementRef<HTMLElement>>('viewport');

  readonly productsInput = input<Producto[]>();
  readonly sectionEyebrow = input('Colección');
  readonly sectionTitle = input('Piezas para la noche.');

  private readonly page = toSignal(this.gateway.listar());
  protected readonly products = computed(() => this.productsInput() ?? this.page()?.content ?? []);
  protected readonly activeIndex = signal(0);
  protected readonly dragging = signal(false);
  protected readonly atStart = computed(() => this.activeIndex() === 0);
  protected readonly atEnd = computed(() => this.activeIndex() >= this.products().length - 1);

  private readonly copFormatter = new Intl.NumberFormat('es-CO', {
    style: 'currency',
    currency: 'COP',
    maximumFractionDigits: 0,
  });

  private dragged = false;
  private dragStartX = 0;
  private wheelAccum = 0;

  ngAfterViewInit(): void {
    const viewport = this.viewportRef().nativeElement;

    const onWheel = (event: WheelEvent) => {
      const delta =
        Math.abs(event.deltaX) > Math.abs(event.deltaY)
          ? event.deltaX
          : event.shiftKey
            ? event.deltaY
            : 0;

      if (delta === 0) {
        return;
      }

      // Rueda horizontal (o shift + rueda): la consumimos para la tarjeta y
      // frenamos la propagación para que Lenis no intente mover la página.
      event.preventDefault();
      event.stopPropagation();
      this.wheelAccum += delta;

      if (Math.abs(this.wheelAccum) >= WHEEL_THRESHOLD) {
        this.step(Math.sign(this.wheelAccum));
        this.wheelAccum = 0;
      }
    };

    const onPointerMove = (event: PointerEvent) => {
      if (!this.dragging()) {
        return;
      }

      const dx = event.clientX - this.dragStartX;
      if (Math.abs(dx) < DRAG_STEP_PX) {
        return;
      }

      // Arrastrar hacia la derecha muestra el producto anterior.
      this.step(-Math.sign(dx));
      this.dragStartX = event.clientX;
      this.dragged = true;
    };

    const onPointerUp = () => {
      this.dragging.set(false);
    };

    const onPointerDown = (event: PointerEvent) => {
      if (event.button !== 0 || !(event.target as Element).closest('.catalog__card')) {
        return;
      }

      this.dragged = false;
      this.dragStartX = event.clientX;
      this.dragging.set(true);
    };

    // Captura en la fase de captura: se ejecuta antes que el click de
    // RouterLink, así podemos cancelar la navegación de una tarjeta lateral.
    const onClickCapture = (event: MouseEvent) => {
      const card = (event.target as Element).closest('.catalog__card');
      if (!card || !this.dragged) {
        return;
      }

      // Arrastre: evita que el click posterior dispare la navegación del enlace.
      event.preventDefault();
      event.stopPropagation();
      this.dragged = false;
    };

    this.zone.runOutsideAngular(() => {
      viewport.addEventListener('wheel', onWheel, { passive: false });
      viewport.addEventListener('pointerdown', onPointerDown);
      viewport.addEventListener('click', onClickCapture, { capture: true });
      window.addEventListener('pointermove', onPointerMove);
      window.addEventListener('pointerup', onPointerUp);
      window.addEventListener('pointercancel', onPointerUp);
    });

    this.destroyRef.onDestroy(() => {
      viewport.removeEventListener('wheel', onWheel);
      viewport.removeEventListener('pointerdown', onPointerDown);
      viewport.removeEventListener('click', onClickCapture, { capture: true });
      window.removeEventListener('pointermove', onPointerMove);
      window.removeEventListener('pointerup', onPointerUp);
      window.removeEventListener('pointercancel', onPointerUp);
      this.dragging.set(false);
    });
  }

  protected onKeydown(event: KeyboardEvent): void {
    if (event.key === 'ArrowLeft') {
      event.preventDefault();
      this.previous();
    } else if (event.key === 'ArrowRight') {
      event.preventDefault();
      this.next();
    }
  }

  protected next(): void {
    this.step(1);
  }

  protected previous(): void {
    this.step(-1);
  }

  protected select(index: number): void {
    const last = Math.max(this.products().length - 1, 0);
    this.activeIndex.set(Math.min(Math.max(index, 0), last));
  }

  private step(delta: number): void {
    this.select(this.activeIndex() + delta);
  }

  /** Transform de la tarjeta según su distancia al producto activo. */
  protected cardTransform(index: number): string {
    const offset = index - this.activeIndex();
    const distance = Math.min(Math.abs(offset), MAX_DISTANCE);
    const direction = Math.sign(offset);

    const x = direction * distance * STEP_X;
    const rotate = direction * distance * ROTATE_DEG;
    const z = -distance * DEPTH_REM;
    const scale = 1 - distance * SCALE_STEP;

    return `translate(-50%, -50%) translateX(${x}%) rotateY(${rotate}deg) translateZ(${z}rem) scale(${scale})`;
  }

  protected cardOpacity(index: number): number {
    const distance = Math.min(Math.abs(index - this.activeIndex()), MAX_DISTANCE);
    return OPACITY_STEPS[distance] ?? 1;
  }

  /** Lejanas: fuera del flujo de tabulación y del árbol de accesibilidad. */
  protected isFar(index: number): boolean {
    return Math.abs(index - this.activeIndex()) > 1;
  }

  protected photoOf(product: Producto): string {
    return product.imagenes.find((image) => image.principal)?.url ?? product.imagenes[0]?.url ?? '';
  }

  protected priceOf(value: number): string {
    return this.copFormatter.format(value);
  }

  protected pad(value: number): string {
    return value.toString().padStart(2, '0');
  }
}
