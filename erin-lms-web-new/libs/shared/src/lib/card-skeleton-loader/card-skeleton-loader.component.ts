import {Component, OnInit} from '@angular/core';

@Component({
  selector: 'jrs-card-skeleton-loader',
  template: `
    <div class="loading-item">
      <div class="glow-text">
        <div class="regular"></div>
        <div class="smaller"></div>
        <div class="smallest"></div>
      </div>
    </div>
  `,
  styleUrls: ['./card-skeleton-loader.component.scss']
})
export class CardSkeletonLoaderComponent {
}
