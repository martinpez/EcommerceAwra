import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';
import { BRAND } from '../../../../core/brand/brand.config';
import { Empresa, SeccionSitio } from '../../../../core/sitio/models/sitio.model';
import { Reveal } from '../../../../shared/directives/reveal.directive';
import { LetterTitle } from '../../../../shared/ui/letter-title/letter-title';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [LetterTitle, Reveal],
  selector: 'app-contact-section',
  templateUrl: './contact-section.html',
  styleUrl: './contact-section.scss',
})
export class ContactSection {
  /** Texto de la sección. Si no llega, se usa el de respaldo del código. */
  readonly section = input<SeccionSitio | null>(null);

  /** Datos de contacto que administra el usuario. */
  readonly empresa = input<Empresa | null>(null);

  protected readonly brand = BRAND;

  /** Enlace de WhatsApp con el número de la empresa. */
  protected readonly whatsappUrl = computed(() => {
    const numero = this.empresa()?.whatsapp ?? BRAND.contact.whatsapp;
    const texto = encodeURIComponent(BRAND.contact.whatsappMessage);
    return `https://wa.me/${numero}?text=${texto}`;
  });

  /** Redes sociales con datos: si el administrador no puso una, no se muestra. */
  protected readonly redes = computed(() => {
    const empresa = this.empresa();
    if (!empresa) {
      return [];
    }
    return [
      { nombre: 'Instagram', url: empresa.instagram },
      { nombre: 'TikTok', url: empresa.tiktok },
      { nombre: 'Facebook', url: empresa.facebook },
      { nombre: 'YouTube', url: empresa.youtube },
    ].filter((red) => !!red.url);
  });

  /** Dirección y horario, en una línea o en null si no hay datos. */
  protected readonly datos = computed(() => {
    const empresa = this.empresa();
    if (!empresa) {
      return null;
    }
    const partes = [
      [empresa.direccion, empresa.ciudad, empresa.pais].filter(Boolean).join(', '),
      empresa.horario,
    ].filter(Boolean);
    return partes.length ? partes.join(' · ') : null;
  });
}
