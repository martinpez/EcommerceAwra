import {
  ChangeDetectionStrategy,
  Component,
  computed,
  effect,
  inject,
  signal,
} from '@angular/core';
import { rxResource, toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { map } from 'rxjs';
import { BRAND } from '../../../core/brand/brand.config';
import { ImagenProducto, Producto } from '../../../core/productos/models/producto.model';
import { ProductosGateway } from '../../../core/productos/ports/productos.gateway';
import { SitioGateway } from '../../../core/sitio/ports/sitio.gateway';
import { CatalogCoverFlow } from '../../home/sections/catalog-cover-flow/catalog-cover-flow';

interface DetalleProducto {
  id: string;
  title: string;
  body: string;
}

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CatalogCoverFlow, RouterLink],
  selector: 'app-product-page',
  styleUrl: './product-page.scss',
  templateUrl: './product-page.html',
})
export class ProductPage {
  private readonly gateway = inject(ProductosGateway);
  private readonly sitioGateway = inject(SitioGateway);
  private readonly route = inject(ActivatedRoute);

  /** Id del producto en la ruta. Cambia al navegar entre productos. */
  private readonly id = toSignal(
    this.route.paramMap.pipe(map((params) => Number(params.get('id')))),
    { initialValue: 0 },
  );

  protected readonly productoResource = rxResource({
    params: () => this.id(),
    stream: ({ params }) => this.gateway.obtenerPorId(params),
    debugName: 'producto',
  });

  protected readonly productosResource = rxResource({
    stream: () => this.gateway.listar(),
    debugName: 'productos',
  });

  protected readonly sitioResource = rxResource({
    stream: () => this.sitioGateway.obtenerSitio(),
    debugName: 'sitio',
  });

  protected readonly cargando = computed(() => this.productoResource.isLoading());

  protected readonly error = computed(() => this.productoResource.error());

  /** El producto pedido. `null` si no existe, no está publicado o falla. */
  protected readonly product = computed<Producto | null>(() => {
    const recurso = this.productoResource;
    return recurso.hasValue() ? recurso.value() : null;
  });

  protected readonly otherProducts = computed(() => {
    const current = this.product();
    const recurso = this.productosResource;
    const all = recurso.hasValue() ? (recurso.value()?.content ?? []) : [];
    return current ? all.filter((item) => item.id !== current.id) : all;
  });

  protected readonly selectedSize = signal<string | null>(null);

  /** Imagen de la galería que se muestra en la foto principal. */
  protected readonly selectedIndex = signal(0);

  /** Vista previa de la otra imagen al pasar el mouse (Fase 6). */
  private readonly showOtherView = signal(false);

  /** Sección abierta del acordeón de detalles (7E). */
  protected readonly openDetailId = signal<string | null>(BRAND.productDetails.material.id);

  protected readonly isAvailable = computed(() => this.product()?.disponibilidad === 'DISPONIBLE');

  protected readonly images = computed<ImagenProducto[]>(() => this.product()?.imagenes ?? []);

  /** Índice de la imagen que se muestra al pasar el mouse: la otra disponible. */
  protected readonly otherIndex = computed(() => {
    if (this.images().length < 2) {
      return this.selectedIndex();
    }
    return this.selectedIndex() === 0 ? 1 : 0;
  });

  /**
   * Opacidad de cada capa de la foto. Solo una queda visible a la vez, así el
   * cambio de vista es un crossfade real y no un salto de imagen.
   */
  protected readonly layers = computed(() => {
    const visible = this.showOtherView() ? this.otherIndex() : this.selectedIndex();
    return this.images().map((_, index) => (index === visible ? 1 : 0));
  });

  protected readonly details = computed<DetalleProducto[]>(() => [
    BRAND.productDetails.material,
    BRAND.productDetails.care,
    BRAND.productDetails.shipping,
  ]);

  /**
   * El número de WhatsApp lo administra el usuario en la tabla de empresa.
   * Si la API no responde se usa el de respaldo del código.
   */
  protected readonly whatsappUrl = computed(() => {
    const product = this.product();
    if (!product || !this.isAvailable()) {
      return null;
    }
    const empresa = this.sitioResource.hasValue() ? this.sitioResource.value()?.empresa : null;
    const numero = empresa?.whatsapp ?? BRAND.contact.whatsapp;
    const size = this.selectedSize();
    const text = `${BRAND.contact.whatsappMessage} ${product.nombre}${size ? `, talla ${size}` : ''}`;
    return `https://wa.me/${numero}?text=${encodeURIComponent(text)}`;
  });

  private readonly copFormatter = new Intl.NumberFormat('es-CO', {
    style: 'currency',
    currency: 'COP',
    maximumFractionDigits: 0,
  });

  constructor() {
    effect(() => {
      const product = this.product();
      const firstAvailable =
        product?.variantes.find((variant) => variant.disponible)?.talla ?? null;
      this.selectedSize.set(firstAvailable);
      this.selectedIndex.set(0);
      this.showOtherView.set(false);
      this.openDetailId.set(BRAND.productDetails.material.id);
    });
  }

  protected selectSize(size: string): void {
    this.selectedSize.set(size);
  }

  /** 7A: la galería decide qué imagen se muestra en la foto principal. */
  protected selectImage(index: number): void {
    this.selectedIndex.set(index);
    this.showOtherView.set(false);
  }

  /** 7E: abre o cierra un panel del acordeón. */
  protected toggleDetail(id: string): void {
    this.openDetailId.update((current) => (current === id ? null : id));
  }

  protected onPhotoKeydown(event: KeyboardEvent): void {
    if (event.key === 'Enter' || event.key === ' ') {
      event.preventDefault();
      this.showOtherView.update((value) => !value);
    }
  }

  protected photoOf(product: Producto): string {
    return product.imagenes.find((image) => image.principal)?.url ?? product.imagenes[0]?.url ?? '';
  }

  protected priceOf(value: number): string {
    return this.copFormatter.format(value);
  }
}
