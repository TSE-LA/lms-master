import {Component, Input} from '@angular/core';

@Component({
  selector: 'jrs-icon-label',
  template: `
    <div class="event-row" *ngIf="!load" [class.grid]="grid">
      <jrs-icon
        class="icon"
        [size]="'medium-large'"
        [color]="'gray'"
        [mat]="true">
        {{icon}}
      </jrs-icon>
      <div
        class="text">
        {{text}}
      </div>
    </div>
    <div class="loader event-row" *ngIf="load">
      <div class="shimmer"></div>
      <div class="right-loader shimmer"></div>
    </div>
  `,
  styleUrls: ['./icon-label.component.scss']
})
export class IconLabelComponent {

  @Input() icon: string;
  @Input() text: string;
  @Input() grid = false;
  @Input() load: boolean;
}
