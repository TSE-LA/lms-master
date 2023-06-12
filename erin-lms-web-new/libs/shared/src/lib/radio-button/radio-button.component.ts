import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'jrs-radio-button',
  template: `
    <input type="radio"
           [class]="[size]"
           [class.margin]="margin"
           [name]="group"
           [value]="label"
           [checked]="check"
           [disabled]="disabled"
           (change)="this.clickedOnRadio()">
    <label *ngIf="label" [class]="[labelSize]">{{label}}</label><br>
  `,
  styleUrls: ['./radio-button.component.scss']
})
export class RadioButtonComponent {

  @Input() label: string;
  @Input() check: boolean;
  @Input() group: string;
  @Input() size = "medium";
  @Input() margin = false;
  @Input() labelSize = "label-medium";
  @Input() disabled: boolean;
  @Output() clicked = new EventEmitter<boolean>();

  clickedOnRadio() {
    this.clicked.emit(!this.check);
  }
}
