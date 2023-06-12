import {Component, Input} from '@angular/core';

@Component({
  selector: 'jrs-icon',
  template: `
    <div class="jrs-font">
      <span [class.mat-icon]="mat"
            [class.jrs-icon]="!mat"
            [class]="[color, size]">
        {{name}}
        <ng-content></ng-content>
      </span>
    </div>
  `
  ,
  styleUrls: ['./icons.component.scss']
})

export class IconsComponent {
  @Input() name = '';
  @Input() size = 'small';
  @Input() color = 'gray';
  @Input() mat: boolean;

  /* reference icon names please don't delete */
  defaultIconNames = [
    "jrs-erin", "jrs-download", "jrs-computer",
    "jrs-calendar", "jrs-settings", "jrs-analytics",
    "jrs-price-tag", "jrs-bubbles", "jrs-search",
    "jrs-trash-can", "jrs-award", "jrs-list", "jrs-military",
    "jrs-trophy", "jrs-feather"
  ];
}
