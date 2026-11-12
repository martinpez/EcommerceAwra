import { ChangeDetectionStrategy, Component, effect, inject, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { firstValueFrom } from 'rxjs';
import { mensajeDeError } from '../../../core/admin/api-error';
import { AdminGateway } from '../../../core/admin/ports/admin.gateway';
import { PanelMensaje } from '../panel-mensaje/panel-mensaje';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [FormsModule, PanelMensaje],
  selector: 'app-site-page',
  templateUrl: './site-page.html',
  styleUrl: './site-page.scss',
})
export class SitePage {
  private readonly admin = inject(AdminGateway);

  protected readonly error = signal<string | null>(null);
  protected readonly aviso = signal<string | null>(null);
  protected readonly guardando = signal(false);
  protected readonly editandoSeccion = signal<number | null>(null);

  protected readonly empresa = rxResource({
    stream: () => this.admin.obtenerEmpresa(),
  });

  protected readonly secciones = rxResource({
    stream: () => this.admin.listarSecciones(),
  });

  /** Campos de la empresa, editables en el formulario. */
  protected readonly formulario = signal({
    nombre: '',
    slogan: '',
    descripcion: '',
    whatsapp: '',
    telefono: '',
    email: '',
    direccion: '',
    ciudad: '',
    pais: '',
    instagram: '',
    tiktok: '',
    facebook: '',
    youtube: '',
    horario: '',
  });

  /** Sección que se está editando. */
  protected readonly seccionFormulario = signal({
    clave: '',
    eyebrow: '',
    titulo: '',
    cuerpo: '',
    orden: 0,
    activa: true,
  });

  constructor() {
    effect(() => {
      const recurso = this.empresa;
      if (!recurso.hasValue()) {
        return;
      }
      const empresa = recurso.value();
      this.formulario.set({
        nombre: empresa.nombre ?? '',
        slogan: empresa.slogan ?? '',
        descripcion: empresa.descripcion ?? '',
        whatsapp: empresa.whatsapp ?? '',
        telefono: empresa.telefono ?? '',
        email: empresa.email ?? '',
        direccion: empresa.direccion ?? '',
        ciudad: empresa.ciudad ?? '',
        pais: empresa.pais ?? '',
        instagram: empresa.instagram ?? '',
        tiktok: empresa.tiktok ?? '',
        facebook: empresa.facebook ?? '',
        youtube: empresa.youtube ?? '',
        horario: empresa.horario ?? '',
      });
    });
  }

  protected campo(nombre: string, valor: string): void {
    this.formulario.update((formulario) => ({ ...formulario, [nombre]: valor }));
  }

  protected async guardarEmpresa(): Promise<void> {
    this.guardando.set(true);
    this.error.set(null);
    this.aviso.set(null);
    try {
      await firstValueFrom(this.admin.actualizarEmpresa(this.formulario()));
      this.aviso.set('Datos de la empresa guardados.');
      this.empresa.reload();
    } catch (error) {
      this.error.set(mensajeDeError(error));
    } finally {
      this.guardando.set(false);
    }
  }

  protected editarSeccion(id: number): void {
    const seccion = (this.secciones.value() ?? []).find((item) => item.id === id);
    if (!seccion) {
      return;
    }
    this.editandoSeccion.set(id);
    this.seccionFormulario.set({
      clave: seccion.clave,
      eyebrow: seccion.eyebrow ?? '',
      titulo: seccion.titulo ?? '',
      cuerpo: seccion.cuerpo ?? '',
      orden: seccion.orden,
      activa: seccion.activa,
    });
  }

  protected campoSeccion(nombre: string, valor: string | number | boolean): void {
    this.seccionFormulario.update((formulario) => ({
      ...formulario,
      [nombre]: valor,
    }));
  }

  protected async guardarSeccion(id: number): Promise<void> {
    this.error.set(null);
    this.aviso.set(null);
    const datos = this.seccionFormulario();
    try {
      await firstValueFrom(
        this.admin.actualizarSeccion(id, {
          clave: datos.clave,
          eyebrow: datos.eyebrow,
          titulo: datos.titulo,
          cuerpo: datos.cuerpo,
          orden: Number(datos.orden) || 0,
          activa: datos.activa,
        }),
      );
      this.editandoSeccion.set(null);
      this.aviso.set('Sección guardada.');
      this.secciones.reload();
    } catch (error) {
      this.error.set(mensajeDeError(error));
    }
  }
}
