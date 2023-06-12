import {Component, EventEmitter, Input, Output, ViewChild} from '@angular/core';
import {MatMenu, MatMenuTrigger} from "@angular/material/menu";

@Component({
  selector: 'jrs-dropdown',
  template: `
    <div #menuTrigger [matMenuTriggerFor]="menu" (menuClosed)="closeMenu()" (menuOpened)="openMenu()">
      <ng-content></ng-content>
    </div>
    <mat-menu #menu="matMenu">
      <li *ngFor="let value of values" class="option"
          [ngStyle]="{'color': value.textColor}" [attr.data-id]="value.id" (selectionchange)="value.name"
          (click)="onClick(value)" mat-menu-item>
        <div class="content-wrapper">
          <div *ngIf="hasPrefix" class="circle-prefix" [ngStyle]="{'background-color': value.background}"></div>
          <span>{{value.name}}</span>
          <span class="spacer"></span>
          <div *ngIf="hasSuffix && value.count > 0" class="notification-suffix">{{value.count}}</div>
        </div>
      </li>
    </mat-menu>
  `,
  styleUrls: ['./dropdown.component.scss']

})
export class DropdownComponent {
  @ViewChild(MatMenuTrigger) menuTrigger: MatMenuTrigger;
  @ViewChild('matMenu') menu: MatMenu;
  @Input() hasOverlay = true;
  @Input() size = 'medium';
  @Input() width = 'long';
  @Input() top: string;
  @Input() values: any[] = [];
  @Input() context = false;
  @Input() moves = false;
  @Input() yTranslate = true;
  @Input() mouseEvent: MouseEvent;
  @Input() offsets = {offsetWidth: 0, offsetHeight: 0};
  @Input() tooltip: boolean;
  @Input() hasPrefix = false;
  @Input() hasSuffix = false;
  @Output() selectionChange = new EventEmitter<any>();
  @Output() outsideClicked = new EventEmitter<string>();
  @Output() opened = new EventEmitter<boolean>();
  @Output() closed = new EventEmitter<boolean>();

  hidden = true;

  onClick(value: any): void {
    this.selectionChange.emit(value);
  }

  toggle(close?: boolean): void {
    if (close) {
      this.hidden = true;
    } else {
      this.hidden = !this.hidden;
    }
  }

  closeMenu(): void {
    this.outsideClicked.emit();
    this.closed.emit(true);
  }

  openMenu(): void {
    this.opened.emit(true);
  }

  manualOpen(): void {
    this.menuTrigger.toggleMenu();
  }
}
