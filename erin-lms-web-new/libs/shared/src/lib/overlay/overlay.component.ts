import {Component, Input, Output, EventEmitter} from '@angular/core';

@Component({
  selector: 'jrs-overlay',
  template: `
    <div *ngIf="show"
         (contextmenu)="contextMenuDisable($event)"
         (click)="overlayClick()" class="overlay"
         [class.transparent]="transparent"
         [class.background-blur]="blur"
         [ngStyle]="{'z-index': zIndex}">
      <ng-content></ng-content>
    </div>`,
  styleUrls: ['./overlay.component.scss']
})
export class OverlayComponent {
  @Input() show: boolean;
  @Input() transparent = false;
  @Input() zIndex = '10';
  @Input() blur = false;
  @Output() clicked = new EventEmitter<any>();

  overlayClick(): void {
    this.clicked.emit();
  }

  contextMenuDisable(e): void {
    e.preventDefault();
    this.overlayClick()
  }
}
