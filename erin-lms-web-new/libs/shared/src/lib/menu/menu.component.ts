import {Component, EventEmitter, Input, Output, ViewChild} from '@angular/core';
import {DropdownComponent} from "../dropdown/dropdown.component";

@Component({
  selector: 'jrs-menu',
  template: `
    <jrs-dropdown
      id="drop-down"
      [hasOverlay]="true"
      [width]="'fit'"
      [context]="context"
      [values]="contextActions"
      (outsideClicked)="outsideClick()"
      (opened)="openTriggered()"
      (closed)="closeTriggered()"
      (selectionChange)="onActionSelect($event)">
      <jrs-circle-button
        class="context-button"
        [color]="noCircle?'none':'gray'"
        [size]="'medium'"
        [iconName]="'more_vert'"
        [isMaterial]="true"
        [iconColor]="noCircle? 'gray':'light'">
      </jrs-circle-button>
    </jrs-dropdown>
  `,
  styleUrls: ['./menu.component.scss']
})
export class MenuComponent {
  @ViewChild('dropdown') dropdown: DropdownComponent;
  @Input() contextActions: any[] = [];
  @Input() context = true;
  @Input() noCircle = false;
  @Output() selectedAction = new EventEmitter<any>();
  @Output() toggleMenu = new EventEmitter<boolean>();

  onActionSelect(action): void {
    this.selectedAction.emit(action);
  }

  outsideClick(): void {
    this.selectedAction.emit(null);
  }

  openTriggered(): void {
    this.toggleMenu.emit(true);
  }

  closeTriggered(): void {
    this.toggleMenu.emit(false);
  }
}
