import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'jrs-circle-button',
  template: `
    <button [ngClass]="[color, size] "
            [disabled]="disabled"
            (click)="click($event)">
          <jrs-icon [mat]="isMaterial"
                    [color]="iconColor"
                    [size]="size">
            {{iconName}}
          </jrs-icon>
    </button>
  `,
  styleUrls: ['./circle-button.component.scss']
})
export class CircleButtonComponent {
  @Input() color = 'primary';
  @Input() iconName: string;
  @Input() iconColor = 'light';
  @Input() isMaterial = true;
  @Input() size = 'medium';
  @Input() disabled: boolean;
  @Output() clicked = new EventEmitter<Event>()

  click(e): void {
    this.clicked.emit(e)
  }
}
