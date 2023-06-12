import {Component, Input} from '@angular/core';

@Component({
  selector: 'jrs-skeleton-loader',
  template: `
    <div class="loader margin-bottom" *ngIf="load">
      <div *ngFor="let item of [].constructor(amount)" class="shimmer"></div>
    </div>

  `,
  styleUrls: ['./skeleton-loader.component.scss']
})
export class SkeletonLoaderComponent {
  @Input() amount = 1;
  @Input() load: boolean;
}
