import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'jrs-button',
  template: `
    <button [ngClass]="[color, size, textColor, float]"
            [class.outline]="outline"
            [class.no-outline]="noOutline"
            [class.margin-bottom]="margin"
            [class.loading]="load"
            [class.circle]="circle"
            [class.square]="square"
            [style.width]="width"
            [class.square-left]="squareLeft"
            [class.square-right]="squareRight"
            [class.bold]="bold"
            [class.fixed-resolution]="fixedResolution"
            [class.no-pointer]="noPointer"
            [class.padding-none]="padding"
            [disabled]="disabled || load"
            [type]="type"
            (click)="onClick($event)">
      <div class="display">
        <div *ngIf="iconName"
             class="icon"
             [class.icon-button]="!!title">
          <jrs-icon
            [mat]="isMaterial"
            [color]="disabled? 'light': iconColor"
            [size]="getIconSize()">
            {{iconName}}
          </jrs-icon>
        </div>
        <div class="title">
          {{title}}
          <ng-content></ng-content>
        </div>
        <div *ngIf="load"
             class="loader">
          <jrs-loader
            [color]="'light'">
          </jrs-loader>
        </div>
      </div>
    </button>
  `,
  styleUrls: ['./button.component.scss']
})
export class ButtonComponent {
  @Input() title: string = null;
  @Input() color = 'primary';
  @Input() width = 'auto';
  @Input() textColor = 'text-light';
  @Input() iconName: string;
  @Input() iconColor = 'light';
  @Input() iconSize: string;
  @Input() isMaterial = true;
  @Input() size = 'icon-large';
  @Input() type = "button";
  @Input() disabled: boolean;
  @Input() load: boolean;
  @Input() bold: boolean;
  @Input() outline: boolean;
  @Input() noOutline: boolean;
  @Input() circle: boolean;
  @Input() square: boolean;
  @Input() squareLeft: boolean;
  @Input() squareRight: boolean;
  @Input() float = 'none';
  @Input() noPointer = false;
  @Input() margin = false;
  @Input() padding = false;
  @Input() fixedResolution = false;
  @Output() clicked = new EventEmitter<Event>()

  onClick(e): void {
    if (!this.disabled) {
      this.clicked.emit(e);
    }
  }

  getIconSize(): string {
    if (this.size === 'icon-large') return 'large';
    if (this.size === 'icon-medium') return 'small';
    if (this.size === 'icon-medium-responsive') return 'small';
    if (this.size === 'icon-long') return 'small';
    if(this.size==='icon-medium-large') return 'fs-23'
    if(this.iconSize) return this.iconSize;
    else return 'medium';
  }
}
