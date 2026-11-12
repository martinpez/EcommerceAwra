import {
  ChangeDetectionStrategy,
  Component,
  computed,
  effect,
  inject,
  signal,
} from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { rxResource, toSignal } from '@angular/core/rxjs-interop';
import { Observable, firstValueFrom, map, of } from 'rxjs';
import { mensajeDeError } from '../../../core/admin/api-error';
import { AdminGateway } from '../../../core/admin/ports/admin.gateway';
import { PanelMensaje } from '../panel-mensaje/panel-mensaje';
import { ImagenProducto, Producto, Variante } from '../../../core/admin/models/admin.model';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [ReactiveFormsModule, RouterLink, PanelMensaje],
  selector: 'app-product-form',
  templateUrl: './product-form.html',
  styleUrl: './product-form.scss',
})
export class ProductForm {
  private readonly admin = inject(AdminGateway);
  private readonly formBuilder = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  /**
   * Id del producto en la ruta. `nuevo` abre el formulario en alta.
   *
   * Se lee de forma reactiva: al navegar de un producto a otro se reutiliza
   * el mismo componente y hay que enterarse del cambio.
   */
  private readonly idParam = toSignal(this.route.paramMap.pipe(map((params) => params.get('id'))), {
    initialValue: this.route.snapshot.paramMap.get('id'),
  });

  protected readonly id = computed<number | 'nuevo'>(() => {
    const valor = this.idParam();
    return valor === 'nuevo' ? 'nuevo' : Number(valor);
  });

  protected readonly esNuevo = computed(() => this.id() === 'nuevo');

  protected readonly error = signal<string | null>(null);
  protected readonly aviso = signal<string | null>(null);
  protected readonly guardando = signal(false);
  protected readonly subiendo = signal(false);

  protected readonly categorias = rxResource({
    stream: () => this.admin.listarCategorias(),
  });

  protected readonly producto = rxResource<Producto | null, number | 'nuevo'>({
    params: () => this.id(),
    stream: ({ params }): Observable<Producto | null> =>
      params === 'nuevo' ? of(null) : this.admin.obtenerProducto(params),
  });

  protected readonly imagenes = rxResource<ImagenProducto[], number | 'nuevo'>({
    params: () => this.id(),
    stream: ({ params }): Observable<ImagenProducto[]> =>
      params === 'nuevo' ? of([]) : this.admin.listarImagenes(params),
  });

  protected readonly formulario = this.formBuilder.nonNullable.group({
    nombre: ['', [Validators.required, Validators.maxLength(150)]],
    descripcion: ['', [Validators.maxLength(1000)]],
    precio: [0, [Validators.required, Validators.min(0.01)]],
    categoriaId: [0, [Validators.required]],
  });

  /** Tallas que se están creando con el producto (solo en alta). */
  protected readonly nuevasVariantes = signal<
    { talla: string; color: string; sku: string; stockMinimo: number }[]
  >([{ talla: '', color: '', sku: '', stockMinimo: 0 }]);

  /** Variante en edición (talla, color, sku y stock mínimo). */
  protected readonly editandoVariante = signal<number | null>(null);
  protected readonly varianteFormulario = this.formBuilder.nonNullable.group({
    talla: ['', [Validators.required]],
    color: ['', [Validators.required]],
    sku: ['', [Validators.required]],
    stockMinimo: [0, [Validators.min(0)]],
  });

  /** Formulario para añadir una talla nueva a un producto ya creado. */
  protected readonly anadiendoVariante = signal(false);
  protected readonly nuevaVarianteFormulario = this.formBuilder.nonNullable.group({
    talla: ['', [Validators.required]],
    color: ['', [Validators.required]],
    sku: ['', [Validators.required]],
    stockMinimo: [0, [Validators.min(0)]],
  });

  protected readonly nuevaImagen = signal<File | null>(null);

  private readonly copFormatter = new Intl.NumberFormat('es-CO', {
    style: 'currency',
    currency: 'COP',
    maximumFractionDigits: 0,
  });

  constructor() {
    // Cuando llega el producto, el formulario se rellena con sus datos.
    // Un efecto es lo indicado: `value` es una señal, no un observable.
    effect(() => {
      const recurso = this.producto;
      if (!recurso.hasValue()) {
        return;
      }
      const producto = recurso.value();
      if (!producto) {
        return;
      }
      this.formulario.setValue({
        nombre: producto.nombre,
        descripcion: producto.descripcion ?? '',
        precio: producto.precio,
        categoriaId: producto.categoriaId,
      });
    });
  }

  protected precio(valor: number): string {
    return this.copFormatter.format(valor);
  }

  protected async guardar(): Promise<void> {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      this.error.set('Revisa los datos del producto.');
      return;
    }

    this.guardando.set(true);
    this.error.set(null);
    this.aviso.set(null);

    const datos = this.formulario.getRawValue();
    // El `<select>` entrega los valores como texto: la categoría vuelve a ser
    // número antes de mandarla.
    const categoriaId = Number(datos.categoriaId) || 0;

    try {
      if (this.esNuevo()) {
        const variantes = this.nuevasVariantes().filter((v) => v.talla.trim());
        if (!variantes.length) {
          this.error.set('Añade al menos una talla.');
          this.guardando.set(false);
          return;
        }
        const creado = await firstValueFrom(
          this.admin.crearProducto({
            nombre: datos.nombre,
            descripcion: datos.descripcion,
            precio: datos.precio,
            categoriaId,
            variantes: variantes.map((v) => ({
              talla: v.talla,
              color: v.color,
              sku: v.sku,
              stockMinimo: v.stockMinimo,
            })),
          }),
        );
        this.aviso.set('Producto creado en borrador. Añade fotos y publícalo.');
        await this.router.navigate(['/admin/productos', creado.id]);
      } else {
        await firstValueFrom(
          this.admin.actualizarProducto(this.id() as number, {
            nombre: datos.nombre,
            descripcion: datos.descripcion,
            precio: datos.precio,
            categoriaId,
          }),
        );
        this.aviso.set('Cambios guardados.');
        this.producto.reload();
      }
    } catch (error) {
      this.error.set(mensajeDeError(error));
    } finally {
      this.guardando.set(false);
    }
  }

  // ------------------------------------------------------------- variantes

  protected anadirVariante(): void {
    this.nuevasVariantes.update((lista) => [
      ...lista,
      { talla: '', color: '', sku: '', stockMinimo: 0 },
    ]);
  }

  protected quitarVariante(indice: number): void {
    this.nuevasVariantes.update((lista) => lista.filter((_, i) => i !== indice));
  }

  protected actualizarVarianteNueva(
    indice: number,
    campo: 'talla' | 'color' | 'sku' | 'stockMinimo',
    valor: string,
  ): void {
    this.nuevasVariantes.update((lista) =>
      lista.map((variante, i) =>
        i === indice
          ? {
              ...variante,
              [campo]: campo === 'stockMinimo' ? Number(valor) || 0 : valor,
            }
          : variante,
      ),
    );
  }

  protected editarVariante(variante: Variante): void {
    this.editandoVariante.set(variante.id);
    this.varianteFormulario.setValue({
      talla: variante.talla,
      color: variante.color,
      sku: variante.sku,
      stockMinimo: variante.stockMinimo,
    });
  }

  protected cancelarEdicionVariante(): void {
    this.editandoVariante.set(null);
  }

  protected async guardarVariante(variante: Variante): Promise<void> {
    if (this.varianteFormulario.invalid) {
      this.varianteFormulario.markAllAsTouched();
      return;
    }
    this.error.set(null);
    const datos = this.varianteFormulario.getRawValue();
    try {
      await firstValueFrom(this.admin.actualizarVariante(variante.id, datos));
      this.aviso.set('Talla actualizada.');
      this.editandoVariante.set(null);
      this.producto.reload();
    } catch (error) {
      this.error.set(mensajeDeError(error));
    }
  }

  protected async eliminarVariante(variante: Variante): Promise<void> {
    if (!confirm(`¿Quitar la talla ${variante.talla}? La variante se da de baja.`)) {
      return;
    }
    this.error.set(null);
    try {
      await firstValueFrom(this.admin.eliminarVariante(variante.id));
      this.aviso.set('Talla eliminada.');
      this.producto.reload();
    } catch (error) {
      this.error.set(mensajeDeError(error));
    }
  }

  /**
   * Añade una talla nueva a un producto que ya existe.
   *
   * La variante nace con stock 0: las unidades entran después, con
   * movimientos, desde Inventario.
   */
  protected mostrarFormularioVariante(): void {
    this.nuevaVarianteFormulario.reset({ talla: '', color: '', sku: '', stockMinimo: 0 });
    this.anadiendoVariante.set(true);
  }

  protected cancelarNuevaVariante(): void {
    this.anadiendoVariante.set(false);
  }

  protected async crearVariante(): Promise<void> {
    if (this.nuevaVarianteFormulario.invalid) {
      this.nuevaVarianteFormulario.markAllAsTouched();
      return;
    }
    this.error.set(null);
    this.aviso.set(null);
    const datos = this.nuevaVarianteFormulario.getRawValue();
    try {
      await firstValueFrom(this.admin.crearVariante(this.id() as number, datos));
      this.cerrarFormularioVariante();
      this.aviso.set('Talla añadida. Ya puedes registrarle stock.');
      this.producto.reload();
    } catch (error) {
      this.error.set(mensajeDeError(error));
    }
  }

  private cerrarFormularioVariante(): void {
    this.anadiendoVariante.set(false);
    this.nuevaVarianteFormulario.reset({
      talla: '',
      color: '',
      sku: '',
      stockMinimo: 0,
    });
  }

  // --------------------------------------------------------------- imágenes

  protected elegirImagen(event: Event): void {
    const entrada = event.target as HTMLInputElement;
    this.nuevaImagen.set(entrada.files?.[0] ?? null);
  }

  protected async subirImagen(): Promise<void> {
    const archivo = this.nuevaImagen();
    if (!archivo || this.esNuevo()) {
      return;
    }
    this.subiendo.set(true);
    this.error.set(null);
    try {
      await firstValueFrom(this.admin.subirImagen(this.id() as number, archivo));
      this.nuevaImagen.set(null);
      this.aviso.set('Foto subida.');
      this.imagenes.reload();
    } catch (error) {
      this.error.set(mensajeDeError(error));
    } finally {
      this.subiendo.set(false);
    }
  }

  protected async marcarPrincipal(imagenId: number): Promise<void> {
    this.error.set(null);
    try {
      await firstValueFrom(this.admin.marcarImagenPrincipal(this.id() as number, imagenId));
      this.aviso.set('Foto principal cambiada.');
      this.imagenes.reload();
    } catch (error) {
      this.error.set(mensajeDeError(error));
    }
  }

  protected async moverImagen(imagenId: number, orden: number): Promise<void> {
    this.error.set(null);
    try {
      await firstValueFrom(this.admin.cambiarOrdenImagen(this.id() as number, imagenId, orden));
      this.imagenes.reload();
    } catch (error) {
      this.error.set(mensajeDeError(error));
    }
  }

  protected async eliminarImagen(imagenId: number): Promise<void> {
    if (!confirm('¿Eliminar esta foto?')) {
      return;
    }
    this.error.set(null);
    try {
      await firstValueFrom(this.admin.eliminarImagen(this.id() as number, imagenId));
      this.aviso.set('Foto eliminada.');
      this.imagenes.reload();
    } catch (error) {
      this.error.set(mensajeDeError(error));
    }
  }
}
