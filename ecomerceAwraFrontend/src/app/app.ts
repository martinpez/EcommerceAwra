import { ChangeDetectionStrategy, Component, OnInit, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { SmoothScrollService } from './core/scroll/smooth-scroll.service';
import { CursorStar } from './shared/ui/cursor-star/cursor-star';
import { LoaderScreen } from './shared/ui/loader-screen/loader-screen';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CursorStar, LoaderScreen, RouterOutlet],
  selector: 'app-root',
  styleUrl: './app.scss',
  templateUrl: './app.html',
})
export class App implements OnInit {
  private readonly smoothScroll = inject(SmoothScrollService);

  ngOnInit(): void {
    this.smoothScroll.start();
  }
}
