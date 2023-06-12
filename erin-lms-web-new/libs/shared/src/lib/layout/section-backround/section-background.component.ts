import {Component, Input} from "@angular/core";

@Component({
  selector: 'jrs-section',
  template: `
    <div [ngClass]="background"
         [class]="color"
         [class.outline]="outline"
         [class.padding]="padding"
         [class.overflow]="overflow"
         [style.min-width]="minWidth"
         [style.max-width]="maxWidth"
         [style.width]="width"
         [style.height]="height"
         [style.min-height]="minHeight">
      <ng-content></ng-content>
    </div>
  `,
  styleUrls: ['./section-background.component.scss']
})
export class SectionBackgroundComponent {
  @Input() background = 'section-background';
  @Input() color = 'primary';
  @Input() padding = true;
  @Input() minWidth = '65vw';
  @Input() maxWidth = 'unset';
  @Input() width = '65vw';
  @Input() height = 'unset';
  @Input() minHeight = 'unset'
  @Input() outline = false;
  @Input() overflow = false;
}
