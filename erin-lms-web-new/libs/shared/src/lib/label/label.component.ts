import {Component, Input} from '@angular/core';

@Component({
  selector: 'jrs-label',
  template: `
    <label class="label-style" [class]="size + (isBold ? ' label-style-bold' : '')" [class.error]="error" [style.font-style]="fontStyle">
      {{text}}
      <span *ngIf="required" class="required-star-style">*</span>
    </label>
  `,
  styleUrls: ['./label.component.scss']
})
export class LabelComponent {
  @Input() text: string;
  @Input() size = 'small';
  @Input() error = false;
  @Input() required: boolean;
  @Input() isBold = false;
  @Input() fontStyle = 'normal';

}
