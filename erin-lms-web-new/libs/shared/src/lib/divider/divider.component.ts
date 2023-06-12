import {Component, Input} from '@angular/core';

@Component({
  selector: 'jrs-divider',
  template: `
    <div class="divider"
         [ngStyle]="{
         'margin-left': marginLeft,
         'margin-right': marginRight,
         'margin-top': marginTop,
         'margin-bottom': marginBottom}">
    </div>
  `,
  styleUrls: ['./divider.component.scss']
})
export class DividerComponent {
  @Input() marginLeft = '0';
  @Input() marginRight = '0';
  @Input() marginTop = '16px';
  @Input() marginBottom = '16px';
}
