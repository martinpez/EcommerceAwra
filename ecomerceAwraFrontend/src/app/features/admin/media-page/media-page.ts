import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { firstValueFrom } from 'rxjs';
import { mensajeDeError } from '../../../core/admin/api-error';
import { AdminGateway } from '../../../core/admin/ports/admin.gateway';
import { PanelMensaje } from '../panel-mensaje/panel-mensaje';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [FormsModule, PanelMensaje],
  selector: 'app-media-page',
  templateUrl: './media-page.html',
  styleUrl: './media-page.scss',
})
export class MediaPage {
  private readonly admin = inject(AdminGateway);

  protected readonly error = signal<string | null>(null);
  protected readonly aviso = signal<string | null>(null);
  protected readonly subiendo = signal(false);

  protected readonly medioArchivo = signal<File | null>(null);
  protected readonly medioTipo = signal<'LOGO' | 'IMAGEN'>('LOGO');
  protected readonly medioSeccion = signal('HERO');
  protected readonly medioAlt = signal('');
  protected readonly medioOrden = signal(1);

  protected readonly videoArchivo = signal<File | null>(null);
  protected readonly videoTitulo = signal('');
  protected readonly videoEtiqueta = signal('');
  protected readonly videoSeccion = signal('MOVIMIENTO');
  protected readonly videoOrden = signal(1);

  protected readonly medios = rxResource({
    stream: () => this.admin.listarMedios(),
  });

  protected readonly videos = rxResource({
    stream: () => this.admin.listarVideos(),
  });

  protected readonly secciones = rxResource({
    stream: () => this.admin.listarSecciones(),
  });

  // ------------------------------------------------------------------ medios

  protected elegirMedio(event: Event): void {
    const entrada = event.target as HTMLInputElement;
    this.medioArchivo.set(entrada.files?.[0] ?? null);
  }

  protected async subirMedio(): Promise<void> {
    const archivo = this.medioArchivo();
    if (!archivo) {
      return;
    }
    this.subiendo.set(true);
    this.error.set(null);
    this.aviso.set(null);
    try {
      await firstValueFrom(
        this.admin.subirMedio(archivo, {
          seccionClave: this.medioSeccion(),
          tipo: this.medioTipo(),
          alt: this.medioAlt().trim(),
          orden: Number(this.medioOrden()) || 0,
          activa: true,
        }),
      );
      this.medioArchivo.set(null);
      this.medioAlt.set('');
      this.aviso.set('Medio subido.');
      this.medios.reload();
    } catch (error) {
      this.error.set(mensajeDeError(error));
    } finally {
      this.subiendo.set(false);
    }
  }

  protected async alternarMedio(
    id: number,
    medio: {
      seccionClave: string | null;
      tipo: 'LOGO' | 'IMAGEN';
      alt: string | null;
      orden: number;
      activa: boolean;
    },
  ): Promise<void> {
    this.error.set(null);
    try {
      await firstValueFrom(
        this.admin.actualizarMedio(id, {
          seccionClave: medio.seccionClave ?? '',
          tipo: medio.tipo,
          alt: medio.alt ?? '',
          orden: medio.orden,
          activa: !medio.activa,
        }),
      );
      this.medios.reload();
    } catch (error) {
      this.error.set(mensajeDeError(error));
    }
  }

  protected async eliminarMedio(id: number): Promise<void> {
    if (!confirm('¿Eliminar este medio? El archivo se borra también del almacenamiento.')) {
      return;
    }
    this.error.set(null);
    try {
      await firstValueFrom(this.admin.eliminarMedio(id));
      this.aviso.set('Medio eliminado.');
      this.medios.reload();
    } catch (error) {
      this.error.set(mensajeDeError(error));
    }
  }

  // ------------------------------------------------------------------ videos

  protected elegirVideo(event: Event): void {
    const entrada = event.target as HTMLInputElement;
    this.videoArchivo.set(entrada.files?.[0] ?? null);
  }

  protected async subirVideo(): Promise<void> {
    const archivo = this.videoArchivo();
    if (!archivo) {
      return;
    }
    this.subiendo.set(true);
    this.error.set(null);
    this.aviso.set(null);
    try {
      await firstValueFrom(
        this.admin.subirVideo(archivo, {
          seccionClave: this.videoSeccion(),
          titulo: this.videoTitulo().trim(),
          etiqueta: this.videoEtiqueta().trim(),
          orden: Number(this.videoOrden()) || 0,
          activa: true,
        }),
      );
      this.videoArchivo.set(null);
      this.videoTitulo.set('');
      this.videoEtiqueta.set('');
      this.aviso.set('Video subido.');
      this.videos.reload();
    } catch (error) {
      this.error.set(mensajeDeError(error));
    } finally {
      this.subiendo.set(false);
    }
  }

  protected async alternarVideo(
    id: number,
    video: {
      seccionClave: string | null;
      titulo: string;
      etiqueta: string | null;
      orden: number;
      activa: boolean;
    },
  ): Promise<void> {
    this.error.set(null);
    try {
      await firstValueFrom(
        this.admin.actualizarVideo(id, {
          seccionClave: video.seccionClave ?? '',
          titulo: video.titulo,
          etiqueta: video.etiqueta ?? '',
          orden: video.orden,
          activa: !video.activa,
        }),
      );
      this.videos.reload();
    } catch (error) {
      this.error.set(mensajeDeError(error));
    }
  }

  protected async eliminarVideo(id: number): Promise<void> {
    if (!confirm('¿Eliminar este video? El archivo se borra también del almacenamiento.')) {
      return;
    }
    this.error.set(null);
    try {
      await firstValueFrom(this.admin.eliminarVideo(id));
      this.aviso.set('Video eliminado.');
      this.videos.reload();
    } catch (error) {
      this.error.set(mensajeDeError(error));
    }
  }
}
