import {Component, Input, ViewChild} from '@angular/core';
import {DropdownComponent} from "../dropdown/dropdown.component";

@Component({
  selector: 'jrs-button-dropdown',
  template: `
    <jrs-button (clicked)="onSelectorClick()" [size]="size" [color]="color" [title]="title">
      <jrs-dropdown [size]="size"  [values]="values" [width]="width" ></jrs-dropdown>
    </jrs-button>`,
  styleUrls: ['./button-dropdown.component.scss']
})
export class ButtonDropdownComponent {
  @ViewChild('dropdown') dropdown: DropdownComponent;
  @Input() size: string;
  @Input() color: string;
  @Input() title: string;
  @Input() width : string;
  @Input() values = Array<Object>();

  onSelectorClick() {
    this.dropdown.toggle();
  }
}
