import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'jrs-slide-toggle',
  template: `
    <label class="switch">
      <input type="checkbox" [checked]="checked" (change)="this.check()">
      <span class="slider round"></span>
    </label>
  `,
  styleUrls: ['./slide-toggle.component.scss']
})
export class SlideToggleComponent {
  @Input() checked = false;
  @Output() clicked = new EventEmitter<boolean>();

  check(): void {
    this.clicked.emit(!this.checked);
  }
}
