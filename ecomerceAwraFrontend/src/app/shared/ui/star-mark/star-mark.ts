import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-star-mark',
  templateUrl: './star-mark.html',
  styleUrl: './star-mark.scss',
})
export class StarMark {}
