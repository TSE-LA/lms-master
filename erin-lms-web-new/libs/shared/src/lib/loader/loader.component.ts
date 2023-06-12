import {Component, Input} from '@angular/core';

@Component({
  selector: 'jrs-loader',
  template: `<div [class]="[color, 'lds-dual-ring']"></div>`,
  styleUrls: ['./loader.component.scss']
})
export class LoaderComponent {
@Input() color = 'primary';
}
