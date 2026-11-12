import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { BRAND } from '../../../../core/brand/brand.config';
import { SeccionSitio } from '../../../../core/sitio/models/sitio.model';
import { Reveal } from '../../../../shared/directives/reveal.directive';
import { LetterTitle } from '../../../../shared/ui/letter-title/letter-title';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [LetterTitle, Reveal],
  selector: 'app-about-section',
  templateUrl: './about-section.html',
  styleUrl: './about-section.scss',
})
export class AboutSection {
  /** Texto de la sección. Si no llega, se usa el de respaldo del código. */
  readonly section = input<SeccionSitio | null>(null);

  protected readonly brand = BRAND;
}
