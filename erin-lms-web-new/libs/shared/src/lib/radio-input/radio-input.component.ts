import {Component, Input, Output, EventEmitter} from '@angular/core';

@Component({
  selector: 'jrs-radio-input',
  template: `
    <div (click)="clicked()">
      <input type="radio" id="{{id}}" name="{{groupName}}" [checked]="checked">
      <label for="{{id}}">{{label}}</label>
    </div>`,
  styleUrls: ['./radio-input.component.scss']
})
export class RadioInputComponent{
  @Input() label: string;
  @Input() id: any;
  @Input() groupName = 'radio-group';
  @Input() checked: boolean;
  @Output() onClick = new EventEmitter<boolean>();
  clicked(): void{
    this.checked = !this.checked;
    this.onClick.emit(true);
  }
}
